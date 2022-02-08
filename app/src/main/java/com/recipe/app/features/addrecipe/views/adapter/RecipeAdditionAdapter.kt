package com.recipe.app.features.addrecipe.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.features.addrecipe.views.adapter.RecipeDataItemWrapper.Companion.VIEW_TYPE_IMAGE_LIST
import com.recipe.app.features.addrecipe.views.viewholders.ImageListViewHolder
import com.recipe.app.features.addrecipe.views.viewholders.ImagePickerViewHolder

class RecipeAdditionAdapter(
    private val itemWrapperList: List<RecipeDataItemWrapper>,
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
        if (itemWrapperList[position].viewType == VIEW_TYPE_IMAGE_LIST) {
            (holder as ImageListViewHolder).bindType(itemWrapperList[position].recipe)
        } else {
            (holder as ImagePickerViewHolder).bindType(itemWrapperList[position].imageChooser)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemWrapperList[position].viewType
    }

    override fun getItemCount(): Int {
        return itemWrapperList.size
    }

    fun removeItem(position: Int) {
        (itemWrapperList as ArrayList).removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemWrapperList.size)
    }
}
