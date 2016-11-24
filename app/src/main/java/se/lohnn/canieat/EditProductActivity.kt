package se.lohnn.canieat

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import se.lohnn.canieat.databinding.ActivityEditProductBinding
import se.lohnn.canieat.product.Product

class EditProductActivity : AppCompatActivity() {
    companion object {
        val KEY_UUID = "uuid"
        val  KEY_PRODUCT = "product"
    }

    private lateinit var uuid: String
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityEditProductBinding>(this, R.layout.activity_edit_product)
        uuid = intent.getStringExtra(KEY_UUID)
        product = intent.getSerializableExtra(KEY_PRODUCT) as Product
        binding.product = product

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
