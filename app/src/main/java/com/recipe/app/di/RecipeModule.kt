package com.recipe.app.di

import android.content.Context
import com.recipe.app.data.RecipeRepository
import com.recipe.app.data.RecipeRepositoryImpl
import com.recipe.app.data.datasource.RecipeLocalDataSource
import com.recipe.app.data.datasource.RecipeLocalDataSourceImpl
import com.recipe.app.db.RecipeDatabase
import com.recipe.app.db.dao.RecipeDao
import com.recipe.app.utility.image.abstraction.ImageLoader
import com.recipe.app.utility.image.abstraction.PicassoImageLoader
import com.squareup.picasso.Picasso
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RecipeModule {

    @Provides
    fun provideRecipeDataBase(@ApplicationContext context: Context): RecipeDatabase {
        return RecipeDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideRecipeDao(recipeDao: RecipeDatabase): RecipeDao {
        return recipeDao.recipeDao()
    }

    @Provides
    fun provideRecipeLocalSource(
        recipeDatabase: RecipeDatabase
    ): RecipeLocalDataSource {
        return RecipeLocalDataSourceImpl(recipeDatabase)
    }

    @Provides
    fun provideRecipeRepository(
        recipeLocalDataSource: Lazy<RecipeLocalDataSource>
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            recipeLocalDataSource
        )
    }

    @Provides
    @Singleton
    fun providePicasso(
        @ApplicationContext context: Context,
    ): Picasso {
        val picassoBuilder = Picasso.Builder(context)
        return picassoBuilder.build()
    }

    @Provides
    @Singleton
    fun providePicassoImageLoader(
        picasso: Lazy<Picasso>
    ): ImageLoader {
        return PicassoImageLoader(picasso)
    }
}
