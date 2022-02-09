package com.recipe.app.features.addrecipe.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper.Companion.VIEW_TYPE_IMAGE_LIST
import com.recipe.app.features.addrecipe.views.viewholders.ImageListViewHolder
import com.recipe.app.features.addrecipe.views.viewholders.ImagePickerViewHolder

class RecipeAdditionAdapter(
    private val items: Set<RecipeDataItemWrapper>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_IMAGE_LIST) {
            return ImageListViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_recipe_image_item, parent, false),
                onItemClick, onDeleteClick
            )
        }
        return ImagePickerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.add_recipe_image_picker, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items.elementAt(position).viewType == VIEW_TYPE_IMAGE_LIST) {
            (holder as ImageListViewHolder).bindType(items.elementAt(position).imageUri)
        } else {
            (holder as ImagePickerViewHolder).bindType(items.elementAt(position).imagePicker)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items.elementAt(position).viewType
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(items: Set<RecipeDataItemWrapper>) {
        (this.items as HashSet).addAll(items)
        notifyItemRangeChanged(0, items.size)
    }

    fun removeItem(position: Int) {
        (items as HashSet).remove(items.elementAt(position))
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}
