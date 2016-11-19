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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import co.metalab.asyncawait.async
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

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        val binding: ActivityScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)

        cameraPreview = binding.preview
        graphicOverlay = binding.graphicOverlay as GraphicOverlay<BarcodeGraphic>

//        TODO("Tap currently has an offset, needs fixing")
        graphicOverlay.setTapListener { barcodeGraphic ->
            if (barcodeGraphic.barcode != null) {
                Log.d(ScanActivity::class.java.simpleName, "Clicked barcode (${barcodeGraphic.barcode})")
            }
        }

        setupActionBar(binding.toolbar)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        cameraManager.permissionsResult(requestCode, permissions, grantResults)
    }
}
