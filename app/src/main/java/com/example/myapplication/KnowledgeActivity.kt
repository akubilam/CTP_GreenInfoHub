package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class KnowledgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_info

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

        setupCardClickListeners()
    }

    private fun setupCardClickListeners() {
        // 1. Paper
        findViewById<CardView>(R.id.cardPaper).setOnClickListener {
            startActivity(Intent(this, PaperActivity::class.java))
        }

        // 2. Plastics
        findViewById<CardView>(R.id.cardPlastic).setOnClickListener {
            startActivity(Intent(this, PlasticActivity::class.java))
        }

        // 3. Glass
        findViewById<CardView>(R.id.cardGlass).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 4. Metals
        findViewById<CardView>(R.id.cardMetal).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 5. Electrical products
        findViewById<CardView>(R.id.cardElectronics).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 6. Old clothes
        findViewById<CardView>(R.id.cardClothes).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 7. Kitchen waste
        findViewById<CardView>(R.id.cardFoodWaste).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 8. Traditional three-color bin
        findViewById<CardView>(R.id.cardThreeColorBin).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 9. GREEN@COMMUNITY
        findViewById<CardView>(R.id.cardGreenDistrict).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}
