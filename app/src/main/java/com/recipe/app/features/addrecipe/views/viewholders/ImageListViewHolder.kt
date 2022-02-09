package com.recipe.app.features.addrecipe.views.viewholders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.db.entity.Recipe
import com.recipe.app.utility.image.ImageLoaderEntryPointAccessor
import com.recipe.app.utility.image.abstraction.ImageLoader
import com.recipe.app.utility.image.abstraction.ImageScaleType

class ImageListViewHolder(
    itemView: View,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) :
    RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.image)
    private var imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)
    private val imageLoader: ImageLoader = ImageLoaderEntryPointAccessor.access(itemView.context).imageLoaderBareBoneProvider()
    init {
        itemView.setOnClickListener { onItemClick.invoke(absoluteAdapterPosition) }
        imageViewDelete.setOnClickListener { onDeleteClick.invoke(absoluteAdapterPosition) }
    }

    fun bindType(uri: String?) {
        uri?.let {
            imageLoader.load(
                image,
                it,
                ImageScaleType.CENTER_CROP,
                R.drawable.ic_add
            )
        }
    }
}
