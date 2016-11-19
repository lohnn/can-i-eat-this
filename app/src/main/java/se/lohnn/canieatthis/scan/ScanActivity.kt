/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.lohnn.canieatthis.scan

import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.GestureDetector
import android.view.MotionEvent
import co.metalab.asyncawait.async
import com.google.android.gms.common.api.CommonStatusCodes
import se.lohnn.canieatthis.R
import se.lohnn.canieatthis.camera.CameraSourcePreview
import se.lohnn.canieatthis.camera.GraphicOverlay
import se.lohnn.canieatthis.databinding.ActivityScanBinding

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
class ScanActivity : AppCompatActivity() {
    companion object {
        // constants used to pass extra data in the intent
        val AutoFocus = "AutoFocus"
        val UseFlash = "UseFlash"
        val BarcodeObject = "Barcode"
    }

    private lateinit var cameraPreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<BarcodeGraphic>
    private lateinit var cameraManager: CameraManager

    // helper objects for detecting taps and pinches.
    private lateinit var gestureDetector: GestureDetector

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        val binding: ActivityScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)

        cameraPreview = binding.preview
        graphicOverlay = binding.graphicOverlay as GraphicOverlay<BarcodeGraphic>

        setupActionBar(binding.toolbar)

        gestureDetector = GestureDetector(this, CaptureGestureListener())

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - binding.appBar.totalScrollRange == 0) {
                async { await { cameraManager.stopCamera() } }
            } else {
                async { await { cameraManager.startCamera() } }
            }
        }

        // read parameters from the intent used to launch the activity.
        val autoFocus = intent.getBooleanExtra(AutoFocus, true)
        val useFlash = intent.getBooleanExtra(UseFlash, false)

        cameraManager = CameraManager(this, graphicOverlay, cameraPreview)
        cameraManager.startCamera()
    }

    private fun setupActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val c = gestureDetector.onTouchEvent(e)
        return c || super.onTouchEvent(e)
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        cameraManager.startCamera()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        cameraManager.stopCamera()
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
        cameraPreview.release()
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *

     * @param requestCode  The request code passed in [.requestPermissions].
     * *
     * @param permissions  The requested permissions. Never null.
     * *
     * @param grantResults The grant results for the corresponding permissions
     * *                     which is either [PackageManager.PERMISSION_GRANTED]
     * *                     or [PackageManager.PERMISSION_DENIED]. Never null.
     * *
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        cameraManager.permissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.

     * @param rawX - the raw position of the tap
     * *
     * @param rawY - the raw position of the tap.
     * *
     * @return true if the activity is ending.
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        val barcodeGraphic = graphicOverlay.getGraphicAtLocation(rawX, rawY)

        if (barcodeGraphic != null && barcodeGraphic.barcode != null) {
            val data = Intent()
            data.putExtra(BarcodeObject, barcodeGraphic.barcode)
            setResult(CommonStatusCodes.SUCCESS, data)
            finish()
            return true
        }
        return false
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }
}
