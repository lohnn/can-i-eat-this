package se.lohnn.canieat

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import se.lohnn.canieat.databinding.ActivityEditProductBinding
import se.lohnn.canieat.PhotoUtil
import se.lohnn.canieat.product.Product


class EditProductActivity : AppCompatActivity() {
    companion object {
        val KEY_UUID = "uuid"
        val KEY_PRODUCT = "product"
        private val REQUEST_TAKE_PHOTO = 321
    }

    private lateinit var uuid: String
    private lateinit var binding: ActivityEditProductBinding
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityEditProductBinding>(this, R.layout.activity_edit_product)
        uuid = intent.getStringExtra(KEY_UUID)
        binding.product = intent.getSerializableExtra(KEY_PRODUCT) as Product

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.takePhoto.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageFile = PhotoUtil.createImageFile(cacheDir)
        if (imageFile != null) {
            val photoIntent = PhotoUtil.takePhotoIntent(this, imageFile)
            if (photoIntent != null) {
                currentPhotoPath = imageFile.absolutePath
                startActivityForResult(photoIntent, EditProductActivity.REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && currentPhotoPath != null) {
            val product = binding.product
            product.imageURL = currentPhotoPath!!
            binding.product = product
        }
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
                intent.putExtra(KEY_PRODUCT, binding.product)
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
