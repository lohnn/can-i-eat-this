package se.lohnn.canieatthis.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Build
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.maxcruz.reactivePermissions.ReactivePermissions
import com.maxcruz.reactivePermissions.entity.Permission
import se.lohnn.canieatthis.R
import se.lohnn.canieatthis.camera.CameraSource
import se.lohnn.canieatthis.camera.CameraSourcePreview
import se.lohnn.canieatthis.camera.GraphicOverlay
import java.io.IOException

class CameraManager(val activity: Activity,
                    val graphicOverlay: GraphicOverlay<BarcodeGraphic>,
                    val cameraPreview: CameraSourcePreview) {
    companion object {
        private val TAG = "Barcode-reader"

        // intent request code to handle updating play services if needed.
        private val RC_HANDLE_GMS = 9001

        // permission request codes need to be < 256
        private val RC_HANDLE_CAMERA_PERM = 2
    }

    private var cameraSource: CameraSource? = null
    val cameraPermission: List<Permission>
    val reactive: ReactivePermissions

    init {
        cameraPermission = listOf(Permission(Manifest.permission.CAMERA,
                R.string.permission_camera_rationale,
                true))
        reactive = ReactivePermissions(activity, RC_HANDLE_CAMERA_PERM)
        reactive.observeResultPermissions().subscribe({ event ->
            if (event.second) {
                createCameraSource(true, false)
                startCamera()
            } else {
                AlertDialog.Builder(activity)
                        .setMessage(R.string.no_camera_permission)
                        .setPositiveButton(R.string.ok, { dialog, id -> })
                        .show()
            }
        }, { throwable ->
            Log.e(TAG, "Darn it, something went wrong with trying to get permissions", throwable)
        })
        reactive.evaluate(cameraPermission)
    }

    fun startCamera() {
        startCameraSource()
//        TODO("How do we want to handle denial of camera permissions")
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        val barcodeDetector = BarcodeDetector.Builder(activity).build()
        val barcodeFactory = BarcodeTrackerFactory(graphicOverlay)
        barcodeDetector.setProcessor(MultiProcessor.Builder(barcodeFactory).build())

        if (!barcodeDetector.isOperational) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = activity.registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(activity, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                Log.w(TAG, activity.getString(R.string.low_storage_error))
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        var builder: CameraSource.Builder = CameraSource.Builder(activity, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else null)
        }

        cameraSource = builder
                .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
                .build()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (cameraSource != null) {
            try {
                cameraPreview.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }

        }
    }

    fun permissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            reactive.receive(permissions, grantResults)
        }
    }

    fun stopCamera() {
        cameraPreview.stop()
    }
}