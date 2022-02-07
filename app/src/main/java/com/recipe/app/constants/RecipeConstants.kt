package com.recipe.app.constants

import com.recipe.app.R

object RecipeConstants {
    const val REQUEST_IMAGE_CAPTURE = 1
    const val PICK_IMAGES = 2
    const val STORAGE_PERMISSION: Int = 100
    const val EXTRA_ID = "EXTRA_ID"
    const val EXTRA_TITLE = "EXTRA_TITLE"
    const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
    const val EXTRA_IMAGES = "EXTRA_IMAGES"
    const val DATE_PATTERN = "yyyyMMdd_HHmmss"
    const val CAMERA = "Camera"
    const val FOLDER = "Folder"
    val chooserTitle = arrayOf(RecipeConstants.CAMERA, RecipeConstants.FOLDER)
    val placeholderIcons =
        intArrayOf(R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp)
}
