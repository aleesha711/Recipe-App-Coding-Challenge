package com.recipe.app.features.addrecipe.views.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.model.ImagePicker

class ImagePickerViewHolder(itemView: View, private val onItemClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.image)
    var title: TextView = itemView.findViewById(R.id.title)

    init {
        itemView.setOnClickListener { onItemClick.invoke(absoluteAdapterPosition) }
    }

    fun bindType(imagePicker: ImagePicker?) {
        imagePicker?.placeholder?.let { image.setImageResource(it) }
        title.text = imagePicker?.title
    }
}