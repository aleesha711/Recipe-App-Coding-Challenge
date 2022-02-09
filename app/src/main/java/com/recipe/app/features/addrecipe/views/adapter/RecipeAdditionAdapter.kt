package com.recipe.app.features.addrecipe.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper.Companion.VIEW_TYPE_IMAGE_LIST
import com.recipe.app.features.addrecipe.views.viewholders.ImageListViewHolder
import com.recipe.app.features.addrecipe.views.viewholders.ImagePickerViewHolder

class RecipeAdditionAdapter(
    private val itemWrapperList: Set<RecipeDataItemWrapper>,
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
        if (itemWrapperList.elementAt(position).viewType == VIEW_TYPE_IMAGE_LIST) {
            (holder as ImageListViewHolder).bindType(itemWrapperList.elementAt(position).imageUri)
        } else {
            (holder as ImagePickerViewHolder).bindType(itemWrapperList.elementAt(position).imageChooser)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemWrapperList.elementAt(position).viewType
    }

    override fun getItemCount(): Int {
        return itemWrapperList.size
    }

    fun updateList(items: Set<RecipeDataItemWrapper>) {
        (itemWrapperList as HashSet).addAll(items)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        (itemWrapperList as HashSet).remove(itemWrapperList.elementAt(position))
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemWrapperList.size)
    }
}
