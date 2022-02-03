package com.recipe.app.utility.image.abstraction

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Picasso
import dagger.Lazy
import javax.inject.Inject

class PicassoImageLoader @Inject constructor(private val picassoLazy: Lazy<Picasso>) : ImageLoader {

    override fun load(
        imageView: ImageView,
        imageUrl: String,
        scalingType: ImageScaleType,
        @DrawableRes placeHolder: Int?,
        @DrawableRes errorDrawable: Int?
    ) {
        render(imageView, imageUrl, scalingType, placeHolder, errorDrawable)
    }

    private fun render(
        imageView: ImageView,
        imageUrl: String,
        scalingType: ImageScaleType,
        @DrawableRes placeHolder: Int?,
        @DrawableRes errorDrawable: Int?
    ) {
        when (scalingType) {
            ImageScaleType.CENTER_INSIDE -> {
                picassoLazy.get()?.let {
                    it.load(imageUrl)
                        .fit()
                        .centerInside()
                        .also { requestCreator ->
                            placeHolder?.let { requestCreator.placeholder(placeHolder) }
                            errorDrawable?.let { requestCreator.error(errorDrawable) }
                        }.into(imageView)
                }
            }
            ImageScaleType.CENTER_CROP -> {
                picassoLazy.get()?.let {
                    it.load(imageUrl)
                        .fit()
                        .centerCrop()
                        .also { requestCreator ->
                            placeHolder?.let { requestCreator.placeholder(placeHolder) }
                            errorDrawable?.let { requestCreator.error(errorDrawable) }
                        }.into(imageView)
                }
            }
        }
    }
}
