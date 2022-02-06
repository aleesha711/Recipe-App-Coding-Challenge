package com.recipe.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.recipe.app.db.entity.Recipe

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe?)

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>
}