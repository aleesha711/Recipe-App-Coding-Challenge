package com.recipe.app.data

import androidx.lifecycle.LiveData
import com.recipe.app.db.entity.Recipe

interface RecipeRepository {
    suspend fun getAllRecipes(): LiveData<List<Recipe>>
    suspend fun insert(recipe: Recipe)
}