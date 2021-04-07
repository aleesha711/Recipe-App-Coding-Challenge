package com.recipe.app.features.addrecipe.views.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.recipe.app.R
import com.recipe.app.features.addrecipe.viewmodel.AddRecipeViewModel
import com.recipe.app.features.addrecipe.views.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_add_recipe.*

class AddRecipeActivity : AppCompatActivity(), ImageAdapter.OnItemClickListener {

    private var addRecipeViewModel: AddRecipeViewModel? = null
    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        addRecipeViewModel = ViewModelProvider(this).get(AddRecipeViewModel::class.java)
        setUpToolBar()

        addRecipeViewModel?.status?.observe(this, Observer { status ->
            status?.let {
                addRecipeViewModel?.status!!.value = null
                Toast.makeText(this, getString(R.string.save_error_msg), Toast.LENGTH_SHORT).show()
            }
        })

        addRecipeViewModel?.permissionDenied?.observe(this, Observer { permission ->
            permission?.let {
                addRecipeViewModel?.permissionDenied!!.value = null
                Toast.makeText(this, getString(R.string.allow_permission_msg), Toast.LENGTH_SHORT).show()
            }
        })

        addRecipeViewModel?.getRecipeToSave?.observe(this, Observer { data ->
            setResult(Activity.RESULT_OK, data)
            finish()
        })

        setImageList()

        addRecipeViewModel?.imagePickerListAdded?.observe(this, Observer {
            imageAdapter?.notifyDataSetChanged()
        })
        addRecipeViewModel?.imageList?.observe(this, Observer {
            imageAdapter?.notifyDataSetChanged()
        })
    }

    private fun setUpToolBar() {
        val toolBar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val toolBarTextView = toolBar.findViewById(R.id.toolbarTitle) as TextView
        toolBarTextView.text = resources.getString(R.string.your_recipes)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_recipe -> {
                addRecipeViewModel?.saveRecipe(editTextTitle?.text.toString(), editTextDescription?.text.toString(), intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setImageList() {
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(applicationContext, addRecipeViewModel!!.imageList.value!!)
        imageAdapter?.setOnItemClickListener(this)
        recyclerView?.adapter = imageAdapter
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        addRecipeViewModel?.openIntent(this, requestCode, grantResults, callBack = { pair ->
            if (pair == null) {
                setImageList()
            } else {
                startActivityForResult(pair.first, pair.second)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            addRecipeViewModel?.addCameraGalleryImages(requestCode,data,contentResolver)
        }
    }

    override fun onItemClick(position: Int, v: View?) {
        addRecipeViewModel?.getIntentBasedOn(position, this)?.let { pair ->
            startActivityForResult(pair.first, pair.second)
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
                    addRecipeViewModel?.unSelectImage(position)
                    imageAdapter?.removeItem(position)
                }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
        val alert = dialogBuilder.create()
        alert.show()
    }
}