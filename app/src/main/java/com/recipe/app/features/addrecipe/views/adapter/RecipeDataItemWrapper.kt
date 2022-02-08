package com.recipe.app.features.addrecipe.views.adapter

import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.addrecipe.model.ImageChooser

data class RecipeDataItemWrapper(val viewType: Int, val imageChooser: ImageChooser?, val recipe: Recipe?) {
    companion object {
        internal const val VIEW_TYPE_IMAGE_LIST = 0
        internal const val VIEW_TYPE_IMAGE_PICKER = 1
    }
}