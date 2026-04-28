package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyCouponsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_coupons)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        TaskManager.completeTask(this, "Redeem your first reward", 20, 10)
        val rv = findViewById<RecyclerView>(R.id.rvCoupons)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

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
        val inventoryPrefs = getSharedPreferences("UserInventory", Context.MODE_PRIVATE)
        val allItems = inventoryPrefs.all // 獲取所有 Map 數據

        val purchasedList = allItems.filter { it.value as Int > 0 }
            .map { Pair(it.key, it.value as Int) }

        if (purchasedList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rv.visibility = View.VISIBLE

            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = MyCouponsAdapter(purchasedList)
        }
    }
}
