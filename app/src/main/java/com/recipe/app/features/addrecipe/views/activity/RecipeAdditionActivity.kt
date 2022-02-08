package com.recipe.app.features.addrecipe.views.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.recipe.app.R
import com.recipe.app.databinding.ActivityRecipeAdditionBinding
import com.recipe.app.features.addrecipe.viewmodel.RecipeAdditionViewModel
import com.recipe.app.features.addrecipe.views.adapter.RecipeAdditionAdapter
import com.recipe.app.utility.MediaUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeAdditionActivity : AppCompatActivity() {

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
        recipeAdditionAdapter =
            RecipeAdditionAdapter(
                this, recipeAdditionViewModel.recipeDataList, {
                    recipeAdditionViewModel.requestPermissionOrGetIntent(it, this)?.let { pair ->
                        openCameraOrGalleryIntent(pair.first)
                    }
                },
                {
                    confirmDeleteImageDialog(it)
                }
            )
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(
                this@RecipeAdditionActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
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

        recipeAdditionViewModel.showPermissionError.observe(this, {
            Toast.makeText(this, getString(R.string.allow_permission_msg), Toast.LENGTH_SHORT)
                .show()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        recipeAdditionViewModel.openIntent(this, requestCode, grantResults, callBack = { pair ->
            pair?.let { openCameraOrGalleryIntent(it.first) }
        })
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                recipeAdditionViewModel.insertImageToList(MediaUtil.getCameraUri())
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    MediaUtil.getImageFilePath(it, contentResolver, callBack = { filePath ->
                        recipeAdditionViewModel.checkImageForDuplication("file:$filePath")
                    })
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_recipe -> {
                recipeAdditionViewModel.saveRecipe(
                    binding.editTextTitle.text.toString(),
                    binding.editTextDescription.text.toString(),
                    intent
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCameraOrGalleryIntent(intent: Intent) {
        when (intent.action) {
            MediaStore.ACTION_IMAGE_CAPTURE -> {
                cameraLauncher.launch(intent)
            }
            Intent.ACTION_OPEN_DOCUMENT -> {
                galleryLauncher.launch(intent)
            }
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
