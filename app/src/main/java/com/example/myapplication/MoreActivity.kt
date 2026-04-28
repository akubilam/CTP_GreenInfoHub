package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        findViewById<View>(R.id.nav_account_row).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.nav_task_row).setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
        }

        findViewById<View>(R.id.nav_store_row).setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        findViewById<View>(R.id.nav_event_row).setOnClickListener {
            startActivity(Intent(this, EnvironmentalEventsActivity::class.java))
        }

        findViewById<View>(R.id.nav_coupons_row).setOnClickListener {
            startActivity(Intent(this, MyCouponsActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_more

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_location -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.nav_info -> {
                    startActivity(Intent(this, KnowledgeActivity::class.java))
                    true
                }
                R.id.nav_scanner -> {
                    startActivity(Intent(this, ScannerActivity::class.java))
                    true
                }
                R.id.nav_more -> {
                    startActivity(Intent(this, MoreActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
