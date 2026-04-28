package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyQrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_my_qr)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_home

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
                    startActivity(Intent(this, EnvironmentalEventsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
