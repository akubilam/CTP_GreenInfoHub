package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskActivity : AppCompatActivity() {

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private val taskItems = listOf(
        TaskItem("Scan 3 plastic bottles", "GO", 30, 20),
        TaskItem("Visit a Recycling station", "GO", 50, 10),
        TaskItem("Read the Plastic Recycling Guide", "GO", 10, 10),
        TaskItem("Redeem your first reward", "GO", 20, 10),
        TaskItem("Update your profile picture", "GO", 5, 5),
        TaskItem("Complete the GreenInfo Hub Feedback Questionnaire", "GO", 5, 5),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

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
                    startActivity(Intent(this, MoreActivity::class.java))
                    true
                }
                else -> false
            }
        }
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(taskItems)
        taskRecyclerView.adapter = taskAdapter
    }
    override fun onResume() {
        super.onResume()
        val recyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}