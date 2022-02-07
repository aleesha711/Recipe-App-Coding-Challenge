package com.recipe.app.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipe.app.data.RecipeRepository
import com.recipe.app.db.entity.Recipe
import com.recipe.app.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _recipes: MutableLiveData<List<Recipe>> = MutableLiveData()
    private val _newRecipe: MutableLiveData<Recipe> = MutableLiveData()

    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    val newRecipe: LiveData<Recipe>
        get() = _newRecipe

    init {
        loadRecipes()
    }

    internal fun insert(recipe: Recipe) = viewModelScope.launch(ioDispatcher) {
        recipeRepository.insert(recipe)
        _newRecipe.postValue(recipe)
    }

    internal fun loadRecipes() {
        viewModelScope.launch(ioDispatcher) {
            _recipes.postValue(recipeRepository.getAllRecipes())
        }
    }
}
