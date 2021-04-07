package com.recipe.app.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.recipe.app.db.entity.Recipe
import com.recipe.app.repository.RecipeRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository = RecipeRepository(application)
    private lateinit var allRecipes: LiveData<List<Recipe>>

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        viewModelScope.launch {
            allRecipes = repository.getAllRecipes()
        }
        return allRecipes
    }
}