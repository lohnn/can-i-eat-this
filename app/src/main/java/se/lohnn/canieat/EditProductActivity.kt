package se.lohnn.canieat

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import se.lohnn.canieat.databinding.ActivityEditProductBinding

class EditProductActivity : AppCompatActivity() {
    companion object {
        val KEY_UUID = "KEY_UUID"
        val  KEY_PRODUCT = "product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = DataBindingUtil.setContentView<ActivityEditProductBinding>(this, R.layout.activity_edit_product)
    }
}
