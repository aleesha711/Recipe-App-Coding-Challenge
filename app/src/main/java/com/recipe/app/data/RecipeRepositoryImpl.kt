package com.recipe.app.data

import androidx.lifecycle.LiveData
import com.recipe.app.data.datasource.RecipeLocalDataSource
import com.recipe.app.db.entity.Recipe
import dagger.Lazy
import javax.inject.Inject


class RecipeRepositoryImpl @Inject constructor(
    private val recipeLocalDataSource: Lazy<RecipeLocalDataSource>,
) : RecipeRepository {

    override suspend fun insert(recipe: Recipe) {
        recipeLocalDataSource.get().insert(recipe)
    }

    override suspend fun getAllRecipes(): LiveData<List<Recipe>> {
        return recipeLocalDataSource.get().getAllRecipes()
    }
}
