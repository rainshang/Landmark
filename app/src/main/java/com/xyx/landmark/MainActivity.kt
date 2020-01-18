package com.xyx.landmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NavHostFragment.create(R.navigation.nav_app).apply {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, this)
                .setPrimaryNavigationFragment(this)
                .commitNow()
        }
    }
}
