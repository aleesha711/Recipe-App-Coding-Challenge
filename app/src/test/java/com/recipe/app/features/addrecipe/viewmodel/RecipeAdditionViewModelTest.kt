package com.recipe.app.features.addrecipe.viewmodel

import android.content.Intent
import com.recipe.app.features.BaseTest
import junit.framework.TestCase
import org.junit.Test

class RecipeAdditionViewModelTest : BaseTest() {

    private val addRecipeViewModel by lazy {
        RecipeAdditionViewModel()
    }

    @Test
    fun `test save recipe shows error when title and description is empty`() {
        addRecipeViewModel.saveRecipe("", "", Intent())
        TestCase.assertEquals(true, addRecipeViewModel.showError.value)
    }
}
