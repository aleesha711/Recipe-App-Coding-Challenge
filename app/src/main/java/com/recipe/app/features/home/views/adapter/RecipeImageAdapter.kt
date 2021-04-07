package com.recipe.app.features.home.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_receipe_image.view.*


class RecipeImageAdapter(private val images: List<String>) : RecyclerView.Adapter<RecipeImageAdapter.RecipeImageHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeImageHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_receipe_image, parent, false)
        context = parent.context
        return RecipeImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeImageHolder, position: Int) {
        Glide.with(context)
            .load(images[position])
            .placeholder(R.color.colorPrimary)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class RecipeImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.imageView

    }
}