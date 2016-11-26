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
package se.lohnn.canieat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import se.lohnn.canieat.camera.CameraSourcePreview
import se.lohnn.canieat.camera.GraphicOverlay
import se.lohnn.canieat.databinding.ActivityScanBinding
import se.lohnn.canieat.dataservice.DataService
import se.lohnn.canieat.product.Product
import se.lohnn.canieat.product.temp.ProductFactory
import se.lohnn.canieat.scan.BarcodeGraphic
import se.lohnn.canieat.scan.CameraManager
import java.util.concurrent.TimeUnit


/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
class ScanActivity : AppCompatActivity() {
    companion object {
        // constants used to pass extra data in the intent
        val KEY_AUTO_FOCUS = "KEY_AUTO_FOCUS"
        val KEY_USE_FLASH = "KEY_USE_FLASH"
    }

    private lateinit var cameraPreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<BarcodeGraphic>
    private lateinit var cameraManager: CameraManager

    private var currentProductUUID: String? = null
    private var currentProduct: Product? = null
    private lateinit var binding: ActivityScanBinding

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        binding.clickListener = this
        cameraPreview = binding.preview
        graphicOverlay = binding.graphicOverlay as GraphicOverlay<BarcodeGraphic>

        graphicOverlay.setTapListener { barcodeGraphic ->
            if (barcodeGraphic.barcode != null) {
                Log.d(ScanActivity::class.java.simpleName, "Clicked barcode (${barcodeGraphic.barcode})")
            }
        }

        setupActionBar(binding.toolbar)

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - binding.appBar.totalScrollRange == 0) {
                doAsync { cameraManager.stopCamera() }
            } else {
                doAsync { cameraManager.startCamera() }
            }
        }

        // read parameters from the intent used to launch the activity.
        val autoFocus = intent.getBooleanExtra(KEY_AUTO_FOCUS, true)
        val useFlash = intent.getBooleanExtra(KEY_USE_FLASH, false)

        cameraManager = CameraManager(this, graphicOverlay, cameraPreview)
        cameraManager.barcodeSubject
                .sample(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged { t1, t2 -> t1.rawValue == t2.rawValue }
                .subscribe({ barcode ->
                    DataService.instance.getProduct(barcode.rawValue) { product ->
                        binding.productOverview.product = product
                        currentProduct = product
                        currentProductUUID = barcode.rawValue
                    }
                })
    }

    fun openEditView() {
        if (currentProduct != null && currentProductUUID != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.productOverview.imageView as View, "transition_image")
            val intent = intentFor<EditProductActivity>(
                    EditProductActivity.KEY_UUID to currentProductUUID!!,
                    EditProductActivity.KEY_PRODUCT to currentProduct!!
            )
            startActivity(intent, options.toBundle())
        }
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
        doAsync { cameraManager.stopCamera() }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        doAsync { cameraManager.stopCamera() }
        cameraManager.barcodeSubject.onComplete()
        cameraPreview.release()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        cameraManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
