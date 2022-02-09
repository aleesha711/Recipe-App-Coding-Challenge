package com.recipe.app.features.addrecipe.viewmodel

import android.content.Context
import android.content.Intent
import com.recipe.app.constants.RecipeConstants
import com.recipe.app.features.BaseTest
import com.recipe.app.features.addrecipe.model.ImagePicker
import com.recipe.app.features.addrecipe.views.adapter.RecipeImageItemWrapper
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Test

class RecipeAdditionViewModelTest : BaseTest() {

    private val context = mockk<Context>(relaxed = true)

    private val addRecipeViewModel by lazy {
        RecipeAdditionViewModel()
    }

    @Test
    fun `test save recipe shows error when title and description is empty`() {
        addRecipeViewModel.saveRecipe("", "", Intent())
        TestCase.assertEquals(true, addRecipeViewModel.showError.value)
    }

    @Test
    fun `test insert image to list`() {
        addRecipeViewModel.insertImageToList("filePath")
        TestCase.assertEquals(true, addRecipeViewModel.notifyImageAdded.value)
    }

    @Test
    fun `test default items for image picker`() {
        addRecipeViewModel.setDefaultItems()
        TestCase.assertEquals(buildImagePickerList(), addRecipeViewModel.recipeImageItems)
    }

    @Test
    fun `test open intent shows error permission denied`() {
        addRecipeViewModel.openIntent(context, 0, intArrayOf()) {}
        TestCase.assertEquals(true, addRecipeViewModel.showPermissionError.value)
    }

    @Test
    fun `test open intent when no permission error`() {
        addRecipeViewModel.openIntent(context, RecipeConstants.STORAGE_PERMISSION, intArrayOf(0)) {}
        TestCase.assertEquals(null, addRecipeViewModel.showPermissionError.value)
    }


    private fun buildImagePickerList(): Set<RecipeImageItemWrapper> {
        val placeholderList = LinkedHashSet<RecipeImageItemWrapper>()
        for (i in RecipeConstants.IMAGE_CHOOSER_ICONS.indices) {
            val imageModel = ImagePicker(RecipeConstants.IMAGE_CHOOSER_TITLES[i], RecipeConstants.IMAGE_CHOOSER_ICONS[i])
            val data = RecipeImageItemWrapper(RecipeImageItemWrapper.VIEW_TYPE_IMAGE_PICKER, imageModel, null)
            placeholderList.add(data)
        }
        return placeholderList
    }
}
