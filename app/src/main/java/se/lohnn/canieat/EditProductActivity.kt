package se.lohnn.canieat

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.intentFor
import se.lohnn.canieat.databinding.ActivityEditProductBinding
import se.lohnn.canieat.product.Product

class EditProductActivity : AppCompatActivity() {
    companion object {
        val KEY_UUID = "uuid"
        val KEY_PRODUCT = "product"
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                val intent = Intent()
                intent.putExtra(KEY_UUID, uuid)
                product.description += " yo"
                intent.putExtra(KEY_PRODUCT, product)
                //TODO: Create product from either two way databinding or reading from textareas
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
