package com.recipe.app.features.home.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.recipe.app.R
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.home.views.adapter.HomeAdapter.ViewHolder
import kotlinx.android.synthetic.main.recyclerview_recipe_item.view.*

class HomeAdapter(private val recipeImageAdapter: RecipeImageAdapter) : ListAdapter<Recipe, ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_recipe_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecipe = getItem(position)
        holder.textViewTitle.text = currentRecipe.title
        holder.textViewDescription.text = currentRecipe.description
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerView.setHasFixedSize(true)
        recipeImageAdapter.setImages(currentRecipe.images)
        holder.recyclerView.adapter = recipeImageAdapter
    }


    fun addNewRecipe(recipe: Recipe) {
        val newRecipesList = mutableListOf<Recipe>()
        newRecipesList.addAll(currentList)
        newRecipesList.add(recipe)
        submitList(newRecipesList)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.textViewTitle
        val textViewDescription: TextView = itemView.textViewDescription
        val recyclerView: RecyclerView = itemView.recyclerView
    }

    private class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.title == newItem.title && oldItem.description == newItem.description
    }
}