package com.recipe.app.features.addrecipe.views.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.model.ImageChooser

class ImagePickerViewHolder(itemView: View, private val callback: (Int) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.image)
    var title: TextView = itemView.findViewById(R.id.title)

    init {
        itemView.setOnClickListener { callback.invoke(absoluteAdapterPosition) }
    }

    fun bindType(imageChooser: ImageChooser?) {
        imageChooser?.placeholder?.let { image.setImageResource(it) }
        title.text = imageChooser?.title
    }
}