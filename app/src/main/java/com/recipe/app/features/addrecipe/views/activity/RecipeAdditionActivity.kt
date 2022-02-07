package com.recipe.app.features.addrecipe.views.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.recipe.app.R
import com.recipe.app.constants.RecipeConstants
import com.recipe.app.databinding.ActivityRecipeAdditionBinding
import com.recipe.app.features.addrecipe.enum.IntentCategory
import com.recipe.app.features.addrecipe.interfaces.OnItemClickListener
import com.recipe.app.features.addrecipe.viewmodel.RecipeAdditionViewModel
import com.recipe.app.features.addrecipe.views.adapter.RecipeAdditionAdapter
import com.recipe.app.utility.MediaUtil
import com.recipe.app.utility.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeAdditionActivity : AppCompatActivity(), OnItemClickListener {

    private val recipeAdditionViewModel: RecipeAdditionViewModel by viewModels()
    private val binding by viewBinding(ActivityRecipeAdditionBinding::bind)
    private lateinit var recipeAdditionAdapter: RecipeAdditionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_addition)
        setUpToolBar()
        setRecyclerView()
        setupObservers()
    }

    private fun setUpToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.add_recipe)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setRecyclerView() {
        recipeAdditionAdapter = RecipeAdditionAdapter(this, recipeAdditionViewModel.recipeDataList)
        recipeAdditionAdapter.setOnItemClickListener(this)
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@RecipeAdditionActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = recipeAdditionAdapter
        }
    }

    private fun setupObservers() {

        recipeAdditionViewModel.showError.observe(this, {
            Toast.makeText(this, getString(R.string.save_error_msg), Toast.LENGTH_SHORT).show()
        })

        recipeAdditionViewModel.saveRecipe.observe(this, { data ->
            setResult(Activity.RESULT_OK, data)
            finish()
        })

        recipeAdditionViewModel.notifyImageAdded.observe(this, {
            recipeAdditionAdapter.notifyDataSetChanged()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        openIntent(requestCode, grantResults, callBack = { pair ->
            if (pair == null) {
                setRecyclerView()
            } else {
                observeCameraOrGalleryIntent(pair.first)
            }
        })
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            recipeAdditionViewModel.insertImageToList(MediaUtil.getCameraUri())
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                MediaUtil.getImageFilePath(it, contentResolver, callBack = { filePath ->
                    recipeAdditionViewModel.checkImageForDuplication("file:$filePath")
                })
            }
        }
    }

    override fun onItemClick(position: Int, v: View?) {
        requestPermissionOrGetIntent(position)?.let { pair ->
            observeCameraOrGalleryIntent(pair.first)
        }
    }

    private fun openIntent(
        requestCode: Int,
        grantResults: IntArray,
        callBack: (pair: Pair<Intent, Int>?) -> Unit
    ) {
        when {
            requestCode == RecipeConstants.STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                callBack(null)
            }
            requestCode == RecipeConstants.REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionOrGetIntent(IntentCategory.ACTION_IMAGE_CAPTURE.value)
                    ?.let { pair ->
                        callBack(pair)
                    }
            }
            requestCode == RecipeConstants.PICK_IMAGES && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionOrGetIntent(IntentCategory.PICK_IMAGES.value)?.let { pair ->
                    callBack(pair)
                }
            }
            else -> {
                Toast.makeText(this, getString(R.string.allow_permission_msg), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissionOrGetIntent(position: Int): Pair<Intent, Int>? {
        when {
            PermissionUtil.shouldRequestPermission(this) -> {
                PermissionUtil.requestPermission(this, position)
            }
            else -> {
                return getIntent(position)
            }
        }
        return null
    }

    private fun getIntent(position: Int): Pair<Intent, Int>? {
        when (position) {
            IntentCategory.ACTION_IMAGE_CAPTURE.value -> {
                return MediaUtil.takePicture()
            }
            IntentCategory.PICK_IMAGES.value -> {
                return MediaUtil.getPickImageIntent()
            }
        }
        return null
    }

    private fun observeCameraOrGalleryIntent(intent: Intent) {
        when (intent.action) {
            MediaStore.ACTION_IMAGE_CAPTURE -> {
                cameraLauncher.launch(intent)
            }
            Intent.ACTION_OPEN_DOCUMENT -> {
                galleryLauncher.launch(intent)
            }
        }
    }

    override fun onItemDelete(position: Int) {
        confirmDeleteImageDialog(position)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_recipe -> {
                recipeAdditionViewModel.saveRecipe(binding.editTextTitle.text.toString(), binding.editTextDescription.text.toString(), intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDeleteImageDialog(position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.delete_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                recipeAdditionViewModel.removeImageFromList(position)
                recipeAdditionAdapter.removeItem(position)
            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }
}
