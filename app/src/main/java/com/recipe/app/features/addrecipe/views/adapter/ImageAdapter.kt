package com.recipe.app.features.addrecipe.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.recipe.app.R
import com.recipe.app.db.entity.Recipe
import java.util.*

class ImageAdapter(private var context: Context, private val imageList: ArrayList<Recipe>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == IMAGE_LIST) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list, parent, false)
            ImageListViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_picker_list, parent, false)
            ImagePickerViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < 2) IMAGE_PICKER else IMAGE_LIST
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == IMAGE_LIST) {
            val viewHolder = holder as ImageListViewHolder
            Glide.with(context)
                    .load(imageList[position].uri)
                    .placeholder(R.color.colorAccent)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.image)
        } else {
            val viewHolder = holder as ImagePickerViewHolder
            viewHolder.image.setImageResource(imageList[position].resImg)
            viewHolder.title.text = imageList[position].title
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    internal inner class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        private var imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)

        init {
            itemView.setOnClickListener { v -> onItemClickListener!!.onItemClick(adapterPosition, v) }
            imageViewDelete.setOnClickListener { onItemClickListener!!.onItemDelete(adapterPosition) }
        }
    }

    inner class ImagePickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var title: TextView = itemView.findViewById(R.id.title)

        init {
            itemView.setOnClickListener { v -> onItemClickListener!!.onItemClick(adapterPosition, v) }
        }
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, v: View?)
        fun onItemDelete(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    companion object {
        private const val IMAGE_LIST = 0
        private const val IMAGE_PICKER = 1
    }

}