package com.recipe.app.features.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.recipe.app.R
import com.recipe.app.constants.RecipeConstants.SPLASH_TIME_OUT
import com.recipe.app.features.home.views.activity.HomeActivity

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}