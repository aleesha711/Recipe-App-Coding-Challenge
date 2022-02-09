package com.recipe.app.features.addrecipe.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipe.app.constants.RecipeConstants
import com.recipe.app.constants.RecipeConstants.IMAGE_CHOOSER_ICONS
import com.recipe.app.constants.RecipeConstants.IMAGE_CHOOSER_TITLES
import com.recipe.app.features.addrecipe.enum.IntentCategory
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper
import com.recipe.app.features.addrecipe.model.ImageChooser
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper.Companion.VIEW_TYPE_IMAGE_LIST
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper.Companion.VIEW_TYPE_IMAGE_PICKER
import com.recipe.app.utility.MediaUtil
import com.recipe.app.utility.PermissionUtil
import kotlin.collections.ArrayList

class RecipeAdditionViewModel : ViewModel() {

    private val _showError = MutableLiveData<Boolean>()
    private val _saveRecipe: MutableLiveData<Intent> = MutableLiveData()
    private val _notifyImageAdded = MutableLiveData<Boolean>()
    private val _showPermissionError = MutableLiveData<Boolean>()
    private var _recipeRecipeDataItemWrapperList: LinkedHashSet<RecipeDataItemWrapper> = linkedSetOf()
    private var images = LinkedHashSet<String>()

    val showError: LiveData<Boolean>
        get() = _showError

    val notifyImageAdded: LiveData<Boolean>
        get() = _notifyImageAdded

    val saveRecipe: LiveData<Intent>
        get() = _saveRecipe

    val recipeRecipeDataItemWrapperList: Set<RecipeDataItemWrapper>
        get() = _recipeRecipeDataItemWrapperList

    val showPermissionError: LiveData<Boolean>
        get() = _showPermissionError

    init {
        addItemsForImagePicker()
    }

    fun addItemsForImagePicker() {
        val placeholderList = linkedSetOf<RecipeDataItemWrapper>()
        for (i in IMAGE_CHOOSER_ICONS.indices) {
            val imageModel = ImageChooser(IMAGE_CHOOSER_TITLES[i], IMAGE_CHOOSER_ICONS[i])
            val data = RecipeDataItemWrapper(VIEW_TYPE_IMAGE_PICKER, imageModel, null)
            placeholderList.add(data)
        }

        _recipeRecipeDataItemWrapperList = placeholderList
    }

    fun removeImageFromList(position: Int) {
        for (i in images.indices) {
            if (images.elementAt(i) == _recipeRecipeDataItemWrapperList.elementAt(position).imageUri) {
                images.remove(images.elementAt(i))
                break
            }
        }
    }

    fun insertImageToList(filePath: String?) {
        val data = RecipeDataItemWrapper(VIEW_TYPE_IMAGE_LIST, null, filePath)
        _recipeRecipeDataItemWrapperList.add(data)
        filePath?.let { images.add(it) }
        _notifyImageAdded.value = true
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
        data.putStringArrayListExtra(RecipeConstants.EXTRA_IMAGES, ArrayList(images))
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
                return MediaUtil.capturePicture()
            }
            IntentCategory.PICK_IMAGES.value -> {
                return MediaUtil.pickImagesFromGallery()
            }
        }
        return null
    }
}
