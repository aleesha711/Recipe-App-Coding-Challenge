package com.recipe.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class Recipe(
        var title: String = "",
        var description: String = "",
        var images: List<String> = ArrayList(),
        var uri: String = "",
        var resImg: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}