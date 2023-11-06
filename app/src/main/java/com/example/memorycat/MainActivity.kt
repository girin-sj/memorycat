package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import navigation.DetailViewFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                // Handle item selection here
                when (item.itemId) {
                    R.id.action_mypage -> {
                        var detailViewFragment = DetailViewFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content, detailViewFragment).commit()
                        return true
                    }
                }
                return false
            }
        })
    }
}
