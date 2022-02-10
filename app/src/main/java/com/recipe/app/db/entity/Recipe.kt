package com.recipe.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    val title: String,
    val description: String,
    val images: List<String>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
