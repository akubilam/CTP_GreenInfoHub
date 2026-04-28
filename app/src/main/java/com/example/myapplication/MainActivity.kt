package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private var oldPoints = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        // delay 2 seconds for display animation
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            keepSplashScreen = false
        }, 2000)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val splashView = splashScreenView.view as android.view.ViewGroup
            val iconView = splashScreenView.iconView

            // 1. build App Name TextView
            val appNameText = android.widget.TextView(this).apply {
                text = "GreenInfo Hub"
                setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 28f)
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                alpha = 0f
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = android.view.Gravity.CENTER
                    topMargin = 320
                }
            }

            // 2. Build "recycling bins" and "plastic bottles"
            val bin = android.widget.ImageView(this).apply {
                setImageResource(R.drawable.ic_anim_bin)
                layoutParams = android.widget.FrameLayout.LayoutParams(300, 300).apply { gravity = android.view.Gravity.CENTER }
            }
            val bottle = android.widget.ImageView(this).apply {
                setImageResource(R.drawable.ic_anim_bottle)
                layoutParams = android.widget.FrameLayout.LayoutParams(150, 150).apply { gravity = android.view.Gravity.CENTER }
                translationY = -1000f
            }

            splashView.addView(appNameText)
            splashView.addView(bin)
            splashView.addView(bottle)
            iconView.alpha = 0f

            // --- Animation Setting ---

            val bottleFall = android.animation.ObjectAnimator.ofFloat(bottle, "translationY", -1000f, 0f).setDuration(700)
            bottleFall.interpolator = android.view.animation.AccelerateInterpolator()

            val binShake = android.animation.ObjectAnimator.ofFloat(bin, "translationY", 0f, 30f, -15f, 0f).setDuration(400)

            val transform = android.animation.AnimatorSet().apply {
                playTogether(
                    android.animation.ObjectAnimator.ofFloat(bin, "alpha", 1f, 0f),
                    android.animation.ObjectAnimator.ofFloat(bottle, "alpha", 1f, 0f),
                    android.animation.ObjectAnimator.ofFloat(iconView, "alpha", 0f, 1f),
                    android.animation.ObjectAnimator.ofFloat(iconView, "scaleX", 0.5f, 1f),
                    android.animation.ObjectAnimator.ofFloat(iconView, "scaleY", 0.5f, 1f),
                    android.animation.ObjectAnimator.ofFloat(appNameText, "alpha", 0f, 1f),
                    android.animation.ObjectAnimator.ofFloat(appNameText, "translationY", 100f, 0f)
                )
                duration = 600
            }

            // delay 1.5 seconds for display animation
            val stayStill = android.animation.ObjectAnimator.ofFloat(iconView, "alpha", 1f, 1f).setDuration(1500)

            val finalFadeOut = android.animation.ObjectAnimator.ofFloat(splashScreenView.view, "alpha", 1f, 0f).setDuration(400)

            android.animation.AnimatorSet().apply {
                playSequentially(bottleFall, binShake, transform, stayStill, finalFadeOut)
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        splashScreenView.remove()
                    }
                })
                start()
            }
        }


        setContentView(R.layout.activity_main)

        val storeIcon = findViewById<View>(R.id.layout_store)
        val taskIcon = findViewById<View>(R.id.layout_tasks)
        val eventIcon = findViewById<View>(R.id.layout_event)

        storeIcon.setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        taskIcon.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
        }

        eventIcon.setOnClickListener {
            startActivity(Intent(this, EnvironmentalEventsActivity::class.java))
        }

        findViewById<View>(R.id.btnQRScan).setOnClickListener {
            startActivity(Intent(this, MyQrActivity::class.java))
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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
        updateRecentProgress()
    }
    private fun updateRecentProgress() {
        val taskPrefs = getSharedPreferences("TaskStatus", MODE_PRIVATE)
        val json = taskPrefs.getString("progress_list", "[]")

        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<ProgressRecord>>() {}.type
        val progressList: List<ProgressRecord> = gson.fromJson(json, type)

        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvRecentProgress)
        val layoutEmpty = findViewById<android.view.View>(R.id.layoutEmptyProgress)

        if (progressList.isNotEmpty()) {
            rv.visibility = android.view.View.VISIBLE
            layoutEmpty.visibility = android.view.View.GONE
            rv.adapter = ProgressAdapter(progressList)
        } else {
            rv.visibility = android.view.View.GONE
            layoutEmpty.visibility = android.view.View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val currentPoints = sharedPreferences.getInt("current_points", 0)

        animatePoints(currentPoints)
        updateGreeting(sharedPreferences)
        updateRecentProgress()
    }
    private fun updateGreeting(sharedPreferences: SharedPreferences) {
        val userName = sharedPreferences.getString("user_name", "Pioneer")
        val welcomeTextView = findViewById<TextView>(R.id.tvWelcomeGreeting)

        welcomeTextView.text = "Welcome back, $userName!"
    }
    private fun animatePoints(targetPoints: Int) {
        val pointsTextView = findViewById<TextView>(R.id.user_points)

        val animator = android.animation.ValueAnimator.ofInt(oldPoints, targetPoints)
        animator.duration = 800
        animator.addUpdateListener { animation ->
            pointsTextView.text = "${animation.animatedValue} Pts"
        }
        animator.start()

        oldPoints = targetPoints
    }
}
