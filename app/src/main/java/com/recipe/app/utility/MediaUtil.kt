package com.recipe.app.utility

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import com.recipe.app.constants.RecipeConstants
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object MediaUtil {

    private lateinit var cameraPhotoUri: String

    fun getImageFilePath(uri: Uri, contentResolver: ContentResolver, callBack: (uri: String?) -> Unit) {
        val file = File(uri.path)
        val filePath = file.path.split(":".toRegex()).toTypedArray()
        val image_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val absolutePathOfImage =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            cursor.close()
            callBack(absolutePathOfImage)
        }
    }

    fun capturePicture(): Pair<Intent, Int> {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = createImageFile()
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
        }
        return Pair(cameraIntent, RecipeConstants.REQUEST_IMAGE_CAPTURE)
    }

    fun pickImagesFromGallery(): Pair<Intent, Int> {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        return Pair(intent, RecipeConstants.PICK_IMAGES)
    }

    private fun createImageFile(): File? {
        var imageFile: File? = null
        val dateTime = SimpleDateFormat(RecipeConstants.DATE_PATTERN).format(Date())
        val imageFileName = "IMG_" + dateTime + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        cameraPhotoUri = "file:" + imageFile?.absolutePath
        return imageFile
    }

    fun getCameraUri() = cameraPhotoUri
}
