package com.recipe.app.features.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.recipe.app.features.home.views.activity.HomeActivity

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, HomeActivity::class.java))
    }

    //TODO
    //3. add unit testcases
    //fix database
}
    