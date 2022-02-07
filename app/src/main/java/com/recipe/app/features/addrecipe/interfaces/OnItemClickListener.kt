package com.recipe.app.features.addrecipe.interfaces

import android.view.View

interface OnItemClickListener {
    fun onItemClick(position: Int, v: View?)
    fun onItemDelete(position: Int)
}