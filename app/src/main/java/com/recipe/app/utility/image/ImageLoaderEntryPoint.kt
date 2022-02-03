package com.recipe.app.utility.image

import com.recipe.app.utility.image.abstraction.ImageLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ImageLoaderEntryPoint {
    fun imageLoaderBareBoneProvider(): ImageLoader
}
