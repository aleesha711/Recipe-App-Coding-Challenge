package com.recipe.app.features.addrecipe.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipe.app.constants.RecipeConstants
import com.recipe.app.constants.RecipeConstants.chooserTitle
import com.recipe.app.constants.RecipeConstants.placeholderIcons
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.addrecipe.enum.IntentCategory
import com.recipe.app.features.addrecipe.model.Data
import com.recipe.app.features.addrecipe.model.ImageChooser
import com.recipe.app.features.addrecipe.views.adapter.RecipeAdditionAdapter
import com.recipe.app.utility.MediaUtil
import com.recipe.app.utility.PermissionUtil
import kotlin.collections.ArrayList

class RecipeAdditionViewModel : ViewModel() {

    private val _showError = MutableLiveData<Boolean>()
    private val _saveRecipe: MutableLiveData<Intent> = MutableLiveData()
    private val _notifyImageAdded = MutableLiveData<Boolean>()
    private val _showPermissionError = MutableLiveData<Boolean>()
    private var _recipeDataList: MutableList<Data> = ArrayList()
    private var images = mutableListOf<String>()

    val showError: LiveData<Boolean>
        get() = _showError

    val notifyImageAdded: LiveData<Boolean>
        get() = _notifyImageAdded

    val saveRecipe: LiveData<Intent>
        get() = _saveRecipe

    val recipeDataList: List<Data>
        get() = _recipeDataList

    val showPermissionError: LiveData<Boolean>
        get() = _showPermissionError

    init {
        addItemsForImagePicker()
    }

    private fun addItemsForImagePicker() {
        val placeholderList = arrayListOf<Data>()
        for (i in placeholderIcons.indices) {
            val imageModel = ImageChooser(chooserTitle[i], placeholderIcons[i])
            val data = Data(RecipeAdditionAdapter.VIEW_TYPE_IMAGE_PICKER, imageModel, null)
            placeholderList.add(data)
        }

        _recipeDataList = placeholderList
    }

    fun removeImageFromList(position: Int) {
        for (i in images.indices) {
            if (images[i] == _recipeDataList[position].recipe?.uri) {
                images.removeAt(i)
                break
            }
        }
    }

    fun insertImageToList(filePath: String?) {
        val imageModel = Recipe()
        if (filePath != null) {
            imageModel.uri = filePath
        }

        val data = Data(RecipeAdditionAdapter.VIEW_TYPE_IMAGE_LIST, null, imageModel)
        _recipeDataList.add(data)
        filePath?.let { images.add(it) }
        _notifyImageAdded.value = true
    }

    fun checkImageForDuplication(filePath: String?) {
        if (!images.contains(filePath)) {
            for (pos in _recipeDataList.indices) {
                if (_recipeDataList[pos].recipe?.uri.equals(filePath, ignoreCase = true)) {
                    _recipeDataList.removeAt(pos)
                }
            }
            insertImageToList(filePath)
        }
    }

    fun saveRecipe(recipeTitle: String, recipeDescription: String, intent: Intent) {
        if (recipeTitle.trim { it <= ' ' }.isEmpty() || recipeDescription.trim { it <= ' ' }
            .isEmpty() || images.isEmpty()
        ) {
            _showError.value = true
            return
        }

        val data = Intent()
        data.putExtra(RecipeConstants.EXTRA_TITLE, recipeTitle)
        data.putExtra(RecipeConstants.EXTRA_DESCRIPTION, recipeDescription)
        data.putStringArrayListExtra(RecipeConstants.EXTRA_IMAGES, images as ArrayList)
        val id = intent.getIntExtra(RecipeConstants.EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(RecipeConstants.EXTRA_ID, id)
        }
        _saveRecipe.value = data
    }

    fun openIntent(
        context: Context,
        requestCode: Int,
        grantResults: IntArray,
        callBack: (pair: Pair<Intent, Int>?) -> Unit
    ) {
        when {
            requestCode == RecipeConstants.STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                callBack(null)
            }
            requestCode == RecipeConstants.REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionOrGetIntent(IntentCategory.ACTION_IMAGE_CAPTURE.value, context)
                    ?.let { pair ->
                        callBack(pair)
                    }
            }
            requestCode == RecipeConstants.PICK_IMAGES && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionOrGetIntent(IntentCategory.PICK_IMAGES.value, context)?.let { pair ->
                    callBack(pair)
                }
            }
            else -> {
                _showPermissionError.value = true
            }
        }
    }


    fun requestPermissionOrGetIntent(position: Int, context: Context): Pair<Intent, Int>? {
        when {
            PermissionUtil.shouldRequestPermission(context) -> {
                PermissionUtil.requestPermission(context, position)
            }
            else -> {
                return getIntent(position)
            }
        }
        return null
    }

    private fun getIntent(position: Int): Pair<Intent, Int>? {
        when (position) {
            IntentCategory.ACTION_IMAGE_CAPTURE.value -> {
                return MediaUtil.takePicture()
            }
            IntentCategory.PICK_IMAGES.value -> {
                return MediaUtil.getPickImageIntent()
            }
        }
        return null
    }
}
