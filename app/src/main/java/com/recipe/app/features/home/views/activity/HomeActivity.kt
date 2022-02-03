package com.recipe.app.features.home.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.recipe.app.constants.RecipeConstants.EXTRA_DESCRIPTION
import com.recipe.app.constants.RecipeConstants.EXTRA_IMAGES
import com.recipe.app.constants.RecipeConstants.EXTRA_TITLE
import com.recipe.app.databinding.ActivityHomeBinding
import com.recipe.app.db.entity.Recipe
import com.recipe.app.features.addrecipe.views.activity.AddRecipeActivity
import com.recipe.app.features.home.viewmodel.HomeViewModel
import com.recipe.app.features.home.views.adapter.HomeAdapter
import com.recipe.app.features.home.views.adapter.RecipeImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityHomeBinding::bind)
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpToolBar()
        val adapter = setupRecyclerview()
        homeViewModel.getAllRecipes().observe(this, { recipes -> adapter.submitList(recipes) })
    }

    private fun setUpToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.your_recipes)
        setSupportActionBar(toolbar)
    }

    private fun setupRecyclerview(): HomeAdapter {
        val recipeImageAdapter = RecipeImageAdapter(this@HomeActivity)
        val adapter = HomeAdapter(recipeImageAdapter)
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
        return adapter
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val item = data?.getStringExtra(EXTRA_DESCRIPTION)?.let {
                data.getStringArrayListExtra(EXTRA_IMAGES)?.let { uriList ->
                    Recipe(
                        data.getStringExtra(EXTRA_TITLE)!!,
                        it,
                        images = uriList
                    )
                }
            }
            item?.let { homeViewModel.insert(it) }
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
                resultLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
