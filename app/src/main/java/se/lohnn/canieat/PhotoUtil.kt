package se.lohnn.canieat

import android.content.Intent
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.util.*

/**
 * Starts an Image capture intent with the specified request code.
 * If request code is left out, this method will provide one for you.
 * @param photoFile File location of photo
 * @param requestCode Optional request code of intent
 * @return Request code
 */
fun AppCompatActivity.takePhoto(photoFile: File?, requestCode: Int = 1337): Int {
    if (photoFile == null) return requestCode
    
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(packageManager) != null) {
        // Continue only if the File was successfully created
        val photoURI = FileProvider.getUriForFile(this,
                "se.lohnn.fileprovider",
                photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, requestCode)
    }
    return requestCode
}

fun AppCompatActivity.createRandomImageInCache(): File? {
    // Create an image file name
    val uuid = UUID.randomUUID().toString()
    val cachePhotoDir = File(cacheDir, "photoCache")
    if (cachePhotoDir.exists() || cachePhotoDir.mkdirs()) {
        val image = File.createTempFile(
                uuid, /* prefix */
                ".jpg", /* suffix */
                cachePhotoDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        return image
    }
    //TODO: Print something useful to the user
    return null
}