package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class StoreActivity : AppCompatActivity() {

    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var userPointsTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var lastDisplayedPoints = 0

    private val storeItems = listOf(
        StoreItem("$50 Supermarket Voucher", 100, R.drawable.ic_product_a),
        StoreItem("Toilet Paper Roll", 200, R.drawable.ic_product_b)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        storeRecyclerView = findViewById(R.id.storeRecyclerView)
        userPointsTextView = findViewById(R.id.userPoints)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        refreshPointsUI()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        storeRecyclerView.layoutManager = LinearLayoutManager(this)
        storeAdapter = StoreAdapter(storeItems) { item ->
            val currentPoints = getStoredPoints()
            if (currentPoints >= item.pointsRequired) {
                savePoints(currentPoints - item.pointsRequired)

                val inventoryPrefs = getSharedPreferences("UserInventory", Context.MODE_PRIVATE)
                val currentCount = inventoryPrefs.getInt(item.name, 0)
                inventoryPrefs.edit().putInt(item.name, currentCount + 1).apply()

                playFloatingAnimation(item.pointsRequired, isAdd = false)
                refreshPointsUI()

                Toast.makeText(this, "Successful exchange ${item.name}！", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Insufficient points！", Toast.LENGTH_SHORT).show()
            }
        }
        storeRecyclerView.adapter = storeAdapter

        bottomNav.selectedItemId = R.id.nav_more

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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
        findViewById<View>(R.id.btnTestAddPoints).setOnClickListener {
            val currentPoints = getStoredPoints()
            val newPoints = currentPoints + 100
            savePoints(newPoints)

            refreshPointsUI()

            playFloatingAnimation(100, isAdd = true)

            Toast.makeText(this, "Debug: Added 100 Pts!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getStoredPoints(): Int {
        return sharedPreferences.getInt("current_points", 0)
    }

    private fun savePoints(points: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("current_points", points)
        editor.apply()
    }
    private fun refreshPointsUI() {
        val targetPoints = getStoredPoints()
        animatePoints(targetPoints)
    }
    // animation
    private fun animatePoints(targetPoints: Int) {
        val pointsTextView = findViewById<TextView>(R.id.userPoints)

        val animator = android.animation.ValueAnimator.ofInt(lastDisplayedPoints, targetPoints)
        animator.duration = 800
        animator.addUpdateListener { animation ->
            pointsTextView.text = animation.animatedValue.toString()
        }
        animator.start()

        lastDisplayedPoints = targetPoints
    }

    private fun playFloatingAnimation(amount: Int, isAdd: Boolean) {
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.storeRootLayout)
        val pointsCard = findViewById<View>(R.id.pointsCard)

        val floatingText = TextView(this).apply {
            text = if (isAdd) "+$amount" else "-$amount"

            // Use green for bonus points and red for deduction points.
            setTextColor(if (isAdd) android.graphics.Color.GREEN else android.graphics.Color.RED)

            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 28f)
            setTypeface(null, android.graphics.Typeface.BOLD)
            elevation = 30f
            id = View.generateViewId()
        }

        rootLayout.addView(floatingText)

        val params = floatingText.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        params.startToStart = pointsCard.id
        params.endToEnd = pointsCard.id
        params.topToTop = pointsCard.id
        params.bottomToBottom = pointsCard.id
        floatingText.layoutParams = params

        // Floating upward animation
        val moveUp = android.view.animation.TranslateAnimation(0f, 0f, 0f, -350f).apply { duration = 1000 }
        val fadeOut = android.view.animation.AlphaAnimation(1f, 0f).apply { startOffset = 600; duration = 400 }

        val animSet = android.view.animation.AnimationSet(true).apply {
            addAnimation(moveUp)
            addAnimation(fadeOut)
        }

        floatingText.startAnimation(animSet)
        animSet.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                rootLayout.post { rootLayout.removeView(floatingText) }
            }
            override fun onAnimationStart(p0: android.view.animation.Animation?) {}
            override fun onAnimationRepeat(p0: android.view.animation.Animation?) {}
        })
    }
}

