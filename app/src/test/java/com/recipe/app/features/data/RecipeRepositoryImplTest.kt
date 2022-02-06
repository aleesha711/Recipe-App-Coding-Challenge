package com.recipe.app.features.data

import com.google.common.truth.Truth
import com.recipe.app.data.RecipeRepositoryImpl
import com.recipe.app.data.datasource.RecipeLocalDataSource
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.BaseTest
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RecipeRepositoryImplTest : BaseTest() {

    private val recipeLocalDataSource: Lazy<RecipeLocalDataSource> = mockk(relaxed = true)

    private val recipe = Recipe("title", "description", arrayListOf(), "uri", 0)

    @ExperimentalCoroutinesApi
    private val recipeRepositoryImpl by lazy {
        RecipeRepositoryImpl(
            recipeLocalDataSource
        )
    }

    @Test
    fun `verify recipe is inserting in local data successfully`() = runBlocking {
        coEvery { recipeLocalDataSource.get().insert(recipe) } returns Unit
        coEvery { recipeLocalDataSource.get().getAllRecipes() } returns listOf(recipe)

        recipeRepositoryImpl.insert(recipe)

        Truth.assertThat(recipeRepositoryImpl.getAllRecipes().contains(recipe)).isTrue()
    }

    @Test
    fun `verify recipes are fetching from local data successfully`() = runBlocking {
        coEvery { recipeLocalDataSource.get().insert(recipe) } returns Unit
        coEvery { recipeLocalDataSource.get().getAllRecipes() } returns listOf(recipe)

        recipeRepositoryImpl.insert(recipe)
        val result = recipeRepositoryImpl.getAllRecipes()

        TestCase.assertEquals(arrayListOf(recipe), result)
    }
}
