package com.recipe.app.features.addrecipe.viewmodel

import android.content.Intent
import android.provider.MediaStore
import com.recipe.app.features.BaseTest
import junit.framework.TestCase
import org.junit.Test

class AddRecipeViewModelTest : BaseTest() {

    private val addRecipeViewModel by lazy {
        AddRecipeViewModel()
    }

    @Test
    fun `test save recipe shows error when title and description is empty`() {
        addRecipeViewModel.saveRecipe("", "", Intent())
        TestCase.assertEquals(true, addRecipeViewModel.showError.value)
    }

    @Test
    fun `test to open camera intent`() {
      //  every { PendingIntentHelper.cancelPendingIntent(context, setReminderOld.reminderType.id, any()) } returns Unit
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        addRecipeViewModel.observeCameraOrGalleryIntent(cameraIntent)
        TestCase.assertEquals(cameraIntent, addRecipeViewModel.intentCamera.value)
    }

  /*  @Test
    fun `test add only new recipe image to the list`() {
        every { addRecipeViewModel.selectedImageList } returns ArrayList()
        addRecipeViewModel.checkImage("filepath1")
        verify(exactly = 1) { addRecipeViewModel.addImage("filepath1") }
    }*/

   /* @Test
    fun `test save recipe save data when title and description is not empty`() {
        addRecipeViewModelx.saveRecipe("title", "description", Intent())
        val data = Intent()
        data.putExtra(RecipeConstants.EXTRA_TITLE, "title")
        data.putExtra(RecipeConstants.EXTRA_DESCRIPTION, "description")
        data.putExtra(RecipeConstants.EXTRA_ID, 1)
        TestCase.assertEquals(data, addRecipeViewModel.recipeToSave.value)
    }*/
}
