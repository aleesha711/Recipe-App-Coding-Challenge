package com.recipe.app.features.home.viewmodel

import com.google.common.truth.Truth
import com.recipe.app.data.RecipeRepository
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.BaseTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class HomeViewModelTest : BaseTest() {

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    private val recipe = Recipe("title", "description", listOf())

    @ExperimentalCoroutinesApi
    private val homeViewModel by lazy {
        HomeViewModel(
            coroutinesTestRule.dispatcher,
            recipeRepository
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test loadAllRecipes post liveData value is list`() = runBlocking {

        val recipes: List<Recipe> = listOf(
            recipe
        )

        coEvery { recipeRepository.getAllRecipes() } returns recipes
        homeViewModel.loadRecipes()
        Assert.assertTrue(homeViewModel.recipes.value!!.isNotEmpty())
        TestCase.assertEquals(arrayListOf(recipe), homeViewModel.recipes.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test insert recipe post liveData value a recipe`() = runBlocking {
        coEvery { recipeRepository.insert(recipe) } returns Unit
        coEvery { recipeRepository.getAllRecipes() } returns listOf(recipe)
        homeViewModel.insert(recipe)
        Truth.assertThat(recipeRepository.getAllRecipes().contains(recipe)).isTrue()
        TestCase.assertEquals(recipe, homeViewModel.newRecipe.value)
    }
}
