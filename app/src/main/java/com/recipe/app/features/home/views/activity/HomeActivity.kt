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
import com.recipe.app.features.addrecipe.views.activity.RecipeAdditionActivity
import com.recipe.app.features.home.viewmodel.HomeViewModel
import com.recipe.app.features.home.views.adapter.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityHomeBinding::bind)
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpToolBar()
        setupRecyclerview()
        setupObservers()
    }

    private fun setUpToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.your_recipes)
        setSupportActionBar(toolbar)
    }

    private fun setupObservers() {
        homeViewModel.recipes.observe(this, { recipes -> adapter.submitList(recipes) })
        homeViewModel.newRecipe.observe(this, { recipe -> adapter.addNewRecipe(recipe) })
    }

    private fun setupRecyclerview() {
        adapter = HomeAdapter()
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val item = data?.getStringExtra(EXTRA_TITLE)?.let { title ->
                data.getStringExtra(EXTRA_DESCRIPTION)?.let { description ->
                    data.getStringArrayListExtra(EXTRA_IMAGES)?.let { images ->
                        Recipe(
                            title = title,
                            description = description,
                            images = images
                        )
                    }
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
                val intent = Intent(this@HomeActivity, RecipeAdditionActivity::class.java)
                resultLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
