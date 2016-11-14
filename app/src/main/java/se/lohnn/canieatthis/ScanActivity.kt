package se.lohnn.canieatthis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import se.lohnn.canieatthis.databinding.ActivityScanBinding
import java.io.IOException


class ScanActivity : AppCompatActivity() {
    companion object {
        private val MY_PERMISSIONS_REQUEST_CAMERA = 88
    }

    private lateinit var cameraTexture: TextureView
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        cameraTexture = binding.cameraTexture
        cameraTexture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, with: Int, height: Int) {
                getCameraPermissions()
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                camera?.stopPreview()
                camera?.release()
                return true
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }
        }
    }

    private fun getCameraPermissions() {
        if (hasCameraPermissions()) {
            continueWithCamera()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
        }
    }

    private fun hasCameraPermissions(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            //Boo, user did not let the app use the camera
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(this)
                        .setMessage("You cannot scan products without the camera, please allow camera use.")
                        .setPositiveButton("Ok") { dialog, id -> }
                        .show()
            } else {
                continueWithCamera()
            }
        }
    }

    private fun continueWithCamera() {
        try {
            camera = Camera.open()
            camera?.setPreviewTexture(cameraTexture.surfaceTexture)
            val camInfo = Camera.CameraInfo()
            Camera.getCameraInfo(getBackFacingCameraId(), camInfo)
            val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val rotation = display.rotation
            var degrees = 0
            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }
            var result: Int
            if (camInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (camInfo.orientation + degrees) % 360
                result = (360 - result) % 360  // compensate the mirror
            } else {  // back-facing
                result = (camInfo.orientation - degrees + 360) % 360
            }
            camera?.setDisplayOrientation(result)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e(ScanActivity::class.java.simpleName, "Something went wrong when opening camera", e)
        }
    }

    private fun getBackFacingCameraId(): Int {
        var cameraId = -1
        // Search for the front facing camera
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0..numberOfCameras - 1) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing === Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i
                break
            }
        }
        return cameraId
    }

    private fun scanImage() {
        val detector = BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
                .build()
        val myBitmap = BitmapFactory.decodeResource(resources, R.drawable.puppy)

        if (!detector.isOperational) {
            Toast.makeText(this, "Could not set up the detector!", Snackbar.LENGTH_LONG).show()
            return
        }
        val frame = Frame.Builder().setBitmap(myBitmap).build()
        val barcodes = detector.detect(frame)
        val thisCode = barcodes.valueAt(0)
        Toast.makeText(this, thisCode.rawValue, Snackbar.LENGTH_LONG).show()
    }
}
