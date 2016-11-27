package se.lohnn.canieat

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import se.lohnn.canieat.databinding.ActivityEditProductBinding
import se.lohnn.canieat.product.Product
import java.io.File
import java.util.UUID


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
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            val photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        "se.lohnn.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val product = binding.product
            product.imageURL = currentPhotoPath!!
            binding.product = product
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val uuid = UUID.randomUUID().toString()
        val imageFileName = "JPEG_${uuid}_"
        val cachePhotoDir = File(cacheDir, "photoCache")
        if (cachePhotoDir.exists() || cachePhotoDir.mkdirs()) {
            val image = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    cachePhotoDir      /* directory */
            )

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.absolutePath
            return image
        }
        //TODO: Print something useful to the user
        return null
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
