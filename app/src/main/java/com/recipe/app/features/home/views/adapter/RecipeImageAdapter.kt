package com.recipe.app.features.home.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.utility.image.ImageLoaderEntryPointAccessor
import com.recipe.app.utility.image.abstraction.ImageLoader
import com.recipe.app.utility.image.abstraction.ImageScaleType
import kotlinx.android.synthetic.main.home_item_receipe_image.view.*

class RecipeImageAdapter(context: Context) : RecyclerView.Adapter<RecipeImageAdapter.RecipeImageHolder>() {
    private val imageLoader: ImageLoader = ImageLoaderEntryPointAccessor.access(context).imageLoaderBareBoneProvider()
    private var images: List<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item_receipe_image, parent, false)
        return RecipeImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeImageHolder, position: Int) {
        imageLoader.load(
            holder.imageView,
            images[position],
            ImageScaleType.CENTER_CROP,
            R.color.colorPrimary
        )
    }

    fun setImages(images: List<String>) {
        this.images = images
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class RecipeImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.imageView
    }
}
