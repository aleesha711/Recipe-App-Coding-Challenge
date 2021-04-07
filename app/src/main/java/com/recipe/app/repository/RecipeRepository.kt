package com.recipe.app.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.recipe.app.db.RecipeDatabase
import com.recipe.app.db.dao.RecipeDao
import com.recipe.app.db.entity.Recipe

class RecipeRepository(application: Application) {

    private val recipeDao: RecipeDao
    private val allRecipes: LiveData<List<Recipe>>

    init {
        val database = RecipeDatabase.getInstance(application)
        recipeDao = database.recipeDao()
        allRecipes = recipeDao.getAllRecipes()
    }

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return allRecipes
    }
}