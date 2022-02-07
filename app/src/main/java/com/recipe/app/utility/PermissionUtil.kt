package com.recipe.app.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.recipe.app.constants.RecipeConstants

object PermissionUtil {

    fun shouldRequestPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun requestPermission(context: Context, position: Int) {
        var requestCode = -1
        if (position == 0) {
            requestCode = RecipeConstants.REQUEST_IMAGE_CAPTURE
        } else if (position == 1) {
            requestCode = RecipeConstants.PICK_IMAGES
        }
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            requestCode
        )
    }
}
