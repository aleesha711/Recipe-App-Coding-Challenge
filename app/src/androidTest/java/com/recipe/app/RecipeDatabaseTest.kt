package com.recipe.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.recipe.app.db.RecipeDatabase
import com.recipe.app.db.dao.RecipeDao
import com.recipe.app.db.entity.Recipe
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipeDatabaseTest : TestCase() {

    private lateinit var appDatabase: RecipeDatabase
    private val recipe = Recipe("title", "description", arrayListOf())
    private lateinit var recipeDao: RecipeDao

    @Before
    fun initDb() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        recipeDao = appDatabase.recipeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun `testGetAllRecipes`() = runBlocking {
        recipeDao.insert(recipe)
        val result = recipeDao.getAllRecipes()
        assertEquals(arrayListOf(recipe), result)
    }

    @Test
    fun insertAndReadRecipe() = runBlocking {
        recipeDao.insert(recipe)
        assertThat(recipeDao.getAllRecipes().contains(recipe)).isTrue()
    }
}
