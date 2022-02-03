package com.recipe.app.data.datasource

import androidx.lifecycle.LiveData
import com.recipe.app.db.entity.Recipe

interface RecipeLocalDataSource {
    suspend fun insert(recipe: Recipe)
    suspend fun getAllRecipes(): LiveData<List<Recipe>>
}