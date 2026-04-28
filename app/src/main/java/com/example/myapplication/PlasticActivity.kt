package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class PlasticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plastic)
        TaskManager.completeTask(this, "Read the Plastic Recycling Guide", 10, 10)

        val imgGuide = findViewById<ImageView>(R.id.img_process_guide)

        imgGuide.setOnClickListener {
            showImageDetailDialog(R.drawable.image_recycling_steps)
        }
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val cardCan = findViewById<LinearLayout>(R.id.card_can_recycle)
        val cardCannot = findViewById<CardView>(R.id.card_cannot_recycle)
        val cardProcess = findViewById<CardView>(R.id.card_process)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        cardCan.visibility = View.VISIBLE
        cardCannot.visibility = View.GONE
        cardProcess.visibility = View.GONE

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                cardCan.visibility = View.GONE
                cardCannot.visibility = View.GONE
                cardProcess.visibility = View.GONE

                when (tab?.position) {
                    0 -> cardCan.visibility = View.VISIBLE
                    1 -> cardCannot.visibility = View.VISIBLE
                    2 -> cardProcess.visibility = View.VISIBLE // 第三格：📋 處理流程
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

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
        findViewById<View>(R.id.layout_pet).setOnClickListener {
            val content = findViewById<View>(R.id.content_pet)
            val arrow = findViewById<ImageView>(R.id.arrow_pet)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }
        // PET
        findViewById<View>(R.id.layout_pet).setOnClickListener {
            val content = findViewById<View>(R.id.content_pet)
            val arrow = findViewById<ImageView>(R.id.arrow_pet)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // HDPE
        findViewById<View>(R.id.layout_hdpe).setOnClickListener {
            val content = findViewById<View>(R.id.content_hdpe)
            val arrow = findViewById<ImageView>(R.id.arrow_hdpe)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // PVC
        findViewById<View>(R.id.layout_pvc).setOnClickListener {
            val content = findViewById<View>(R.id.content_pvc)
            val arrow = findViewById<ImageView>(R.id.arrow_pvc)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // LDPE
        findViewById<View>(R.id.layout_ldpe).setOnClickListener {
            val content = findViewById<View>(R.id.content_ldpe)
            val arrow = findViewById<ImageView>(R.id.arrow_ldpe)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // PP
        findViewById<View>(R.id.layout_pp).setOnClickListener {
            val content = findViewById<View>(R.id.content_pp)
            val arrow = findViewById<ImageView>(R.id.arrow_pp)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // PS
        findViewById<View>(R.id.layout_ps).setOnClickListener {
            val content = findViewById<View>(R.id.content_ps)
            val arrow = findViewById<ImageView>(R.id.arrow_ps)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // OTHER
        findViewById<View>(R.id.layout_other).setOnClickListener {
            val content = findViewById<View>(R.id.content_other)
            val arrow = findViewById<ImageView>(R.id.arrow_other)
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }
    }
    private fun showImageDetailDialog(resId: Int) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        val imageView = ImageView(this).apply {
            setImageResource(resId)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(imageView)
        dialog.show()
    }
}
