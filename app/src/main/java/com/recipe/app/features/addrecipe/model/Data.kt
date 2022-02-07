package com.recipe.app.features.addrecipe.model

import com.recipe.app.db.entity.Recipe

data class Data(val viewType: Int, val imageChooser: ImageChooser?, val recipe: Recipe?)