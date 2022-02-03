package com.recipe.app.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipe.app.data.RecipeRepository
import com.recipe.app.db.entity.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val recipeRepository: RecipeRepository) : ViewModel() {

    fun insert(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.insert(recipe)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        var result: LiveData<List<Recipe>> = MutableLiveData()
        viewModelScope.launch {
            result = recipeRepository.getAllRecipes()
        }
        return result
    }
}
