package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.opencsv.CSVReader
import java.io.InputStreamReader

class EnvironmentalEventsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EnvironmentalEventsAdapter
    private lateinit var eventList: List<EnvironmentalEvent>
    private lateinit var loadingOverlay: View

    private var currentPage = 0
    private val PAGE_SIZE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_environmental_events)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadingOverlay = findViewById(R.id.loading_overlay_full)

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
        showLoading()

        Thread {
            eventList = readCsv("environmental_events.csv")
            runOnUiThread {
                hideLoading()
                adapter = EnvironmentalEventsAdapter(getCurrentPageEvents()) { event ->
                    val intent = Intent(this, EventDetailActivity::class.java)
                    intent.putExtra("eventName", event.eventName)
                    intent.putExtra("eventDate", event.eventDate)
                    intent.putExtra("eventLocation", event.eventLocation)
                    intent.putExtra("eventType", event.eventType)
                    intent.putExtra("eventDescription", event.eventDescription)
                    intent.putExtra("eventContact", event.eventContact)
                    intent.putExtra("eventWebsite", event.eventWebsite)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }.start()

        findViewById<Button>(R.id.buttonPrevious).setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updateRecyclerView()
            } else {
                showMessage("This is the first page.")
            }
        }
        findViewById<Button>(R.id.buttonNext).setOnClickListener {
            if ((currentPage + 1) * PAGE_SIZE < eventList.size) {
                currentPage++
                updateRecyclerView()
            } else {
                showMessage("There is no next page.")
            }
        }
    }

    private fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("continue", null)
            .create()

        builder.setOnShowListener { dialogInterface ->
            val dialog = dialogInterface as AlertDialog
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.BLUE)

            positiveButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        builder.show()
    }

    private fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
        recyclerView.visibility = RecyclerView.GONE
    }

    private fun hideLoading() {
        loadingOverlay.visibility = View.GONE
        recyclerView.visibility = RecyclerView.VISIBLE
    }

    private fun readCsv(fileName: String): List<EnvironmentalEvent> {
        val events = mutableListOf<EnvironmentalEvent>()
        try {
            val inputStream = assets.open(fileName)
            val reader = CSVReader(InputStreamReader(inputStream))
            reader.readNext()

            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                if (line!!.isNotEmpty() && line.size >= 7) {
                    val eventName = line[0]
                    val eventDate = line[1]
                    val eventLocation = line[2]
                    val eventType = line[3]
                    val eventDescription = line[4]
                    val eventContact = line[5]
                    val eventWebsite = line[6]

                    events.add(EnvironmentalEvent(eventName, eventDate, eventLocation, eventType, eventDescription, eventContact, eventWebsite))
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Data loading failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return events
    }

    private fun getCurrentPageEvents(): List<EnvironmentalEvent> {
        return eventList.drop(currentPage * PAGE_SIZE).take(PAGE_SIZE)
    }

    private fun updateRecyclerView() {
        adapter = EnvironmentalEventsAdapter(getCurrentPageEvents()) { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventName", event.eventName)
            intent.putExtra("eventDate", event.eventDate)
            intent.putExtra("eventLocation", event.eventLocation)
            intent.putExtra("eventType", event.eventType)
            intent.putExtra("eventDescription", event.eventDescription)
            intent.putExtra("eventContact", event.eventContact)
            intent.putExtra("eventWebsite", event.eventWebsite)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}