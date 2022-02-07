package com.recipe.app.data

import com.recipe.app.db.entity.Recipe

interface RecipeRepository {
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun insert(recipe: Recipe)
}