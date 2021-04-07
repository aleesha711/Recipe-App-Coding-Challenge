package com.recipe.app.features.addrecipe.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.recipe.app.R
import com.recipe.app.constants.RecipeConstants
import com.recipe.app.constants.RecipeConstants.EXTRA_DESCRIPTION
import com.recipe.app.constants.RecipeConstants.EXTRA_ID
import com.recipe.app.constants.RecipeConstants.EXTRA_IMAGES
import com.recipe.app.constants.RecipeConstants.EXTRA_TITLE
import com.recipe.app.constants.RecipeConstants.PICK_IMAGES
import com.recipe.app.constants.RecipeConstants.REQUEST_IMAGE_CAPTURE
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.addrecipe.enum.IntentCategory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private var currentPhotoPath: String? = null
    private var image: File? = null
    private var selectedImageList: ArrayList<String> = ArrayList()
    private var chooserTitle = arrayOf(RecipeConstants.CAMERA, RecipeConstants.FOLDER)
    private var resImg = intArrayOf(R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp)

    var status = MutableLiveData<Boolean?>()
    var getRecipeToSave: MutableLiveData<Intent> = MutableLiveData()
    var imagePickerListAdded = MutableLiveData<Boolean>()
    var imageList: MutableLiveData<ArrayList<Recipe>> = MutableLiveData()
    var permissionDenied = MutableLiveData<Boolean?>()

    init {
        setImagePickerList()
    }

     private fun createImageFile(): File? {
        val dateTime = SimpleDateFormat(RecipeConstants.DATE_PATTERN).format(Date())
        val imageFileName = "IMG_" + dateTime + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image?.absolutePath
        return image
    }

    fun openIntent(context: Context, requestCode : Int,  grantResults: IntArray, callBack: (pair: Pair<Intent, Int>?) -> Unit) {
        if (requestCode == RecipeConstants.STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            callBack(null)

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getIntentBasedOn(IntentCategory.ACTION_IMAGE_CAPTURE.value, context)?.let { pair ->
                callBack(pair)
            }

        } else if (requestCode == PICK_IMAGES && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getIntentBasedOn(IntentCategory.PICK_IMAGES.value, context)?.let { pair ->
                callBack(pair)
            }
        } else {
            permissionDenied.value = true
        }
    }
    fun getIntentBasedOn(position: Int,context: Context): Pair<Intent, Int>? {
        var pair: Pair<Intent, Int>? = null
        if(shouldRequestPermission(context)) {
            requestPermission(context, position)
        } else {
            if (position == IntentCategory.ACTION_IMAGE_CAPTURE.value) {
                pair = takePicture()
            } else if (position == IntentCategory.PICK_IMAGES.value) {
                pair = getPickImageIntent()
            }
        }
        return pair
    }

    private fun takePicture() : Pair<Intent, Int> {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val cameraIntent = Intent(ACTION_IMAGE_CAPTURE)
        // Continue only if the File was successfully created;
        val photoFile: File? = createImageFile()
        if (photoFile != null) {
            cameraIntent.putExtra(EXTRA_OUTPUT, Uri.fromFile(photoFile))
        }
        val pair = Pair(cameraIntent, REQUEST_IMAGE_CAPTURE)
        return pair
    }

    private fun getPickImageIntent() : Pair<Intent, Int> {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        val pair = Pair(intent, PICK_IMAGES)
        return pair
    }

    fun unSelectImage(position: Int) {
        for (i in selectedImageList.indices) {
            if (selectedImageList[i] == imageList.value!![position].uri) {
                selectedImageList.removeAt(i)
                break
            }
        }
    }

    // Add Camera and Folder in ArrayList
     private fun setImagePickerList() {
        val imgList = arrayListOf<Recipe>()

        for (i in resImg.indices) {
            val imageModel = Recipe()
            imageModel.resImg = resImg.get(i)
            imageModel.title = chooserTitle[i]
            imgList.add(imageModel)
        }
        imageList.value = imgList
        imagePickerListAdded.value = true
    }

      private fun requestPermission(context:Context, position: Int)  {
          var requestCode = -1
          if (position == 0) {
              requestCode = REQUEST_IMAGE_CAPTURE
          } else if (position == 1) {
              requestCode = PICK_IMAGES
          }
          ActivityCompat.requestPermissions(
                  context as Activity,
                  arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                  requestCode
          )
    }

    private fun shouldRequestPermission(context: Context): Boolean {
            if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                return true
            }
            return false
    }

    private fun addCameraImage() {
        addImage(currentPhotoPath)
    }

    private fun addImage(filePath: String?) {
        val imageModel = Recipe()
        if (filePath != null) {
            imageModel.uri = filePath
        }
        imageList.value?.add(2, imageModel)
        selectedImageList.add(0, filePath!!)
        imagePickerListAdded.value = true
    }

    private fun checkImage(filePath: String?) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList.contains(filePath)) {
            for (pos in imageList.value!!.indices) {
                if (imageList.value!![pos].uri.equals(filePath,ignoreCase = true)) {
                    imageList.value?.removeAt(pos)
                }
            }
            addImage(filePath!!)
        }
    }

     private fun getImageFilePath(uri: Uri?,contentResolver: ContentResolver) {
        val cursor =
                uri?.let {
                    contentResolver.query(it,
                            arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
                }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val absolutePathOfImage =
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                absolutePathOfImage?.let { checkImage(it) } ?: checkImage(uri.toString())
            }
        }
    }

     fun saveRecipe(title : String, description : String, intent: Intent) {
        val title = title
        val description = description
        if (title.trim { it <= ' ' }.isEmpty() || description.trim { it <= ' ' }.isEmpty() || selectedImageList.isEmpty()) {
            status.value = true
            return
        }

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, description)
        data.putStringArrayListExtra(EXTRA_IMAGES, selectedImageList)
        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }
         getRecipeToSave.value =  data
    }

    fun addCameraGalleryImages(requestCode: Int,data: Intent?,contentResolver: ContentResolver){
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            addCameraImage()
        } else if (requestCode == PICK_IMAGES) {
            data?.clipData?.let {
                val mClipData = data.clipData
                for (i in 0 until mClipData?.itemCount!!) {
                    val item = mClipData.getItemAt(i)
                    val uri = item?.uri
                    getImageFilePath(uri, contentResolver)
                }
            } ?: run {
                val uri = data?.data
                getImageFilePath(uri, contentResolver)
            }
        }
    }
}