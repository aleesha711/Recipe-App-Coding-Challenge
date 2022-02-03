package com.recipe.app.features.addrecipe.views.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.recipe.app.R
import com.recipe.app.databinding.ActivityAddRecipeBinding
import com.recipe.app.features.addrecipe.viewmodel.AddRecipeViewModel
import com.recipe.app.features.addrecipe.views.adapter.AddRecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipeActivity : AppCompatActivity(), AddRecipeAdapter.OnItemClickListener {

    private val addRecipeViewModel: AddRecipeViewModel by viewModels()
    private val binding by viewBinding(ActivityAddRecipeBinding::bind)
    private var addRecipeAdapter: AddRecipeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        setUpToolBar()
        setImageList()
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

    private fun setImageList() {
        addRecipeAdapter = AddRecipeAdapter(applicationContext, addRecipeViewModel.imageList.value!!)
        addRecipeAdapter?.setOnItemClickListener(this)
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@AddRecipeActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = addRecipeAdapter
        }
    }

    private fun setupObservers() {
        addRecipeViewModel.recipeError.observe(
            this,
            Observer { status ->
                status?.let {
                    addRecipeViewModel.recipeError.value = null
                    Toast.makeText(this, getString(R.string.save_error_msg), Toast.LENGTH_SHORT).show()
                }
            }
        )

        addRecipeViewModel.permissionDenied.observe(
            this,
            Observer { permission ->
                permission?.let {
                    addRecipeViewModel.permissionDenied.value = null
                    Toast.makeText(this, getString(R.string.allow_permission_msg), Toast.LENGTH_SHORT).show()
                }
            }
        )

        addRecipeViewModel.recipeToSave.observe(
            this,
            { data ->
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        )

        addRecipeViewModel.imagePickerListAdded.observe(
            this,
            {
                addRecipeAdapter?.notifyDataSetChanged()
            }
        )
        addRecipeViewModel.imageList.observe(
            this,
            {
                addRecipeAdapter?.notifyDataSetChanged()
            }
        )

        addRecipeViewModel.addGalleryImage.observe(
            this,
            {
                galleryLauncher.launch(it)
            }
        )

        addRecipeViewModel.addCameraImage.observe(
            this,
            {
                cameraLauncher.launch(it)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_recipe -> {
                addRecipeViewModel.saveRecipe(binding.editTextTitle.text.toString(), binding.editTextDescription.text.toString(), intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        addRecipeViewModel.openIntent(this, requestCode, grantResults, callBack = { pair ->
            if (pair == null) {
                setImageList()
            } else {
                addRecipeViewModel.addCameraGalleryImages(pair.first)
            }
        })
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            addRecipeViewModel.addCameraImage()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            addRecipeViewModel.addGalleryImages(data, contentResolver)
        }
    }

    override fun onItemClick(position: Int, v: View?) {
        addRecipeViewModel.getIntentBasedOn(position, this)?.let { pair ->
            addRecipeViewModel.addCameraGalleryImages(pair.first)
        }
    }

    override fun onItemDelete(position: Int) {
        confirmDeleteImageDialog(position)
    }

    private fun confirmDeleteImageDialog(position: Int) {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.delete_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                addRecipeViewModel.unSelectImage(position)
                addRecipeAdapter?.removeItem(position)
            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }
}
