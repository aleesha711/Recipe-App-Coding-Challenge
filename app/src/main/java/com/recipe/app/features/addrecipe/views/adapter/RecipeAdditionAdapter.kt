package com.recipe.app.features.addrecipe.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.interfaces.OnItemClickListener
import com.recipe.app.features.addrecipe.model.Data
import com.recipe.app.utility.image.ImageLoaderEntryPointAccessor
import com.recipe.app.utility.image.abstraction.ImageLoader
import com.recipe.app.utility.image.abstraction.ImageScaleType

class RecipeAdditionAdapter(private val context: Context, private val imageList: List<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        internal const val VIEW_TYPE_IMAGE_LIST = 0
        internal const val VIEW_TYPE_IMAGE_PICKER = 1
    }

    private val imageLoader: ImageLoader = ImageLoaderEntryPointAccessor.access(context).imageLoaderBareBoneProvider()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_IMAGE_LIST) {
            return ImageListViewHolder(
                LayoutInflater.from(context).inflate(R.layout.add_recipe_image_item, parent, false)
            )
        }
        return ImagePickerViewHolder(
            LayoutInflater.from(context).inflate(R.layout.add_recipe_image_picker, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (imageList[position].viewType == VIEW_TYPE_IMAGE_LIST) {
            (holder as ImageListViewHolder).bind(position)
        } else {
            (holder as ImagePickerViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return imageList[position].viewType
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun removeItem(position: Int) {
        (imageList as ArrayList).removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private inner class ImageListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        private var imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)

        init {
            itemView.setOnClickListener { v -> onItemClickListener?.onItemClick(absoluteAdapterPosition, v) }
            imageViewDelete.setOnClickListener { onItemClickListener?.onItemDelete(absoluteAdapterPosition) }
        }

        fun bind(position: Int) {
            imageList[position].recipe?.let {
                imageLoader.load(
                    image,
                    it.uri,
                    ImageScaleType.CENTER_CROP,
                    R.drawable.ic_add
                )
            }
        }
    }

    private inner class ImagePickerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var title: TextView = itemView.findViewById(R.id.title)

        init {
            itemView.setOnClickListener { v -> onItemClickListener?.onItemClick(absoluteAdapterPosition, v) }
        }

        fun bind(position: Int) {
            imageList[position].imageChooser?.placeholder?.let { image.setImageResource(it) }
            title.text = imageList[position].imageChooser?.title
        }
    }
}
