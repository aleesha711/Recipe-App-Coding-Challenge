package com.recipe.app.utility.image

import android.content.Context
import dagger.hilt.android.EntryPointAccessors

object ImageLoaderEntryPointAccessor {
    fun access(context: Context): ImageLoaderEntryPoint {
        return EntryPointAccessors.fromApplication(context, ImageLoaderEntryPoint::class.java)
    }
}

