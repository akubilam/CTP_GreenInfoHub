package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

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
                    startActivity(Intent(this, EnvironmentalEventsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val eventName = intent.getStringExtra("eventName")
        val eventDate = intent.getStringExtra("eventDate")
        val eventLocation = intent.getStringExtra("eventLocation")
        val eventType = intent.getStringExtra("eventType")
        val eventDescription = intent.getStringExtra("eventDescription")
        val eventContact = intent.getStringExtra("eventContact")
        val eventWebsite = intent.getStringExtra("eventWebsite")

        findViewById<TextView>(R.id.detailEventName).text = eventName ?: "無資料"
        findViewById<TextView>(R.id.detailEventDate).text = eventDate ?: "無資料"
        findViewById<TextView>(R.id.detailEventLocation).text = eventLocation ?: "無資料"
        findViewById<TextView>(R.id.detailEventType).text = eventType ?: "無資料"
        findViewById<TextView>(R.id.detailEventDescription).text = eventDescription ?: "無資料"
        findViewById<TextView>(R.id.detailEventContact).text = eventContact ?: "無資料"
        val websiteTextView = findViewById<TextView>(R.id.detailEventWebsite)

        if (!eventWebsite.isNullOrBlank()) {
            val spannableString = SpannableString(eventWebsite)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            websiteTextView.text = spannableString
            websiteTextView.setOnClickListener {
                // Handle the click to open the URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventWebsite))
                startActivity(intent)
            }
        }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}