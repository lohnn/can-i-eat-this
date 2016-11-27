package se.lohnn.canieat

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.util.*

class PhotoUtil {
    companion object {
        fun createImageFile(cacheDir: File): File? {
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

        fun takePhotoIntent(activity: Activity, photoFile: File): Intent? {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
                // Continue only if the File was successfully created
                val photoURI = FileProvider.getUriForFile(activity,
                        "se.lohnn.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                return takePictureIntent
            }
            return null
        }
    }
}