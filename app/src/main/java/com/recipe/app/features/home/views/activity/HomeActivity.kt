package com.recipe.app.features.home.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.recipe.app.R
import com.recipe.app.constants.RecipeConstants.ADD_RECIPE_REQUEST
import com.recipe.app.constants.RecipeConstants.EXTRA_DESCRIPTION
import com.recipe.app.constants.RecipeConstants.EXTRA_IMAGES
import com.recipe.app.constants.RecipeConstants.EXTRA_TITLE
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.addrecipe.views.activity.AddRecipeActivity
import com.recipe.app.features.home.viewmodel.HomeViewModel
import com.recipe.app.features.home.views.adapter.HomeAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private var homeViewModel: HomeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val adapter = setupRecyclerview()

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel?.getAllRecipes()?.observe(this, Observer { recipes -> adapter.submitList(recipes) })
        setUpToolBar()
    }

    private fun setUpToolBar() {
        val toolBar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val toolBarTextView = toolBar.findViewById(R.id.toolbarTitle) as TextView
        toolBarTextView.text = resources.getString(R.string.your_recipes)
    }

    private fun setupRecyclerview(): HomeAdapter {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val adapter = HomeAdapter()
        recyclerView.adapter = adapter
        return adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_RECIPE_REQUEST && resultCode == Activity.RESULT_OK  && data != null) {
            val item = Recipe(
                    data.getStringExtra(EXTRA_TITLE),
                    data.getStringExtra(EXTRA_DESCRIPTION),
                    images = data.getStringArrayListExtra(EXTRA_IMAGES)
            )
            homeViewModel?.insert(item)
            Toast.makeText(this, getString(R.string.successfully_added), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addRecipe -> {
                val intent = Intent(this@HomeActivity, AddRecipeActivity::class.java)
                startActivityForResult(intent, ADD_RECIPE_REQUEST)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}