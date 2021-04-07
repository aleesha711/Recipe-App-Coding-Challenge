package com.recipe.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.recipe.app.db.dao.RecipeDao
import com.recipe.app.db.entity.Recipe
import com.recipe.app.typeconverters.Converters


@Database(entities = [Recipe::class], version = 1)
@TypeConverters(*[Converters::class])
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        private var instance: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase {
            if (instance == null) {
                synchronized(RecipeDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                            RecipeDatabase::class.java, "recipe_database"
                    ).fallbackToDestructiveMigration()
                        .addCallback(Companion.roomCallback)
                        .build()
                }
            }
            return instance!!
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) = super.onCreate(db)
        }

    }
}