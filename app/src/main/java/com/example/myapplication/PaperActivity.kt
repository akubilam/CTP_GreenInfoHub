package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class PaperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper)
        val imgGuide = findViewById<ImageView>(R.id.img_paper_process_guide)

        imgGuide.setOnClickListener {
            showImageDetailDialog(R.drawable.image_paper_recycling_steps)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

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
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        val cardRecyclable = findViewById<View>(R.id.card_recyclable_paper)
        val cardNonRecyclable = findViewById<View>(R.id.card_non_recyclable_paper)
        val cardProcess = findViewById<View>(R.id.card_paper_process)

        btnBack.setOnClickListener { finish() }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                cardRecyclable.visibility = View.GONE
                cardNonRecyclable.visibility = View.GONE
                cardProcess.visibility = View.GONE

                when (tab?.position) {
                    0 -> cardRecyclable.visibility = View.VISIBLE
                    1 -> cardNonRecyclable.visibility = View.VISIBLE
                    2 -> cardProcess.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun showImageDetailDialog(resId: Int) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        val imageView = ImageView(this).apply {
            setImageResource(resId)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // 背景設為黑色更專業
        }

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(imageView)
        dialog.show()
    }
}
