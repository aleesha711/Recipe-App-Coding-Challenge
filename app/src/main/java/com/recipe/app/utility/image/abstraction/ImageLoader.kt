package com.recipe.app.utility.image.abstraction

import android.widget.ImageView
import androidx.annotation.DrawableRes

interface ImageLoader {
    fun load(
        imageView: ImageView,
        imageUrl: String,
        scalingType: ImageScaleType,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorDrawable: Int? = null
    )
}
