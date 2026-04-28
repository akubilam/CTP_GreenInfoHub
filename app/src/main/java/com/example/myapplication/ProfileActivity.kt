package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePic: ImageView
    private lateinit var tvTotalPoints: TextView
    private lateinit var tvProfileName: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)

                com.bumptech.glide.Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(ivProfilePic)

                saveProfileImage(it.toString())

                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                TaskManager.completeTask(this, "Update your profile picture", 5, 5)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to get permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ivProfilePic = findViewById(R.id.ivProfilePic)
        tvTotalPoints = findViewById(R.id.tvTotalPoints)
        tvProfileName = findViewById(R.id.tvProfileName)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        loadUserData()

        btnBack.setOnClickListener {
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.profileImageCard).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<TextView>(R.id.btnEditProfile).setOnClickListener {
            showEditNameDialog()
        }

        findViewById<TextView>(R.id.btnLanguage).setOnClickListener {
            Toast.makeText(this, "Language: English", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

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
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val currentPoints = sharedPreferences.getInt("current_points", 0)
        val userXp = sharedPreferences.getInt("user_xp", 0)

        tvTotalPoints.text = currentPoints.toString()

        val tvLevelTitle = findViewById<TextView>(R.id.tvMemberLevel)
        tvLevelTitle.text = LevelManager.getLevelTitle(userXp)
        tvLevelTitle.setTextColor(LevelManager.getLevelColor(userXp))

        val tvLevelProgress = findViewById<TextView>(R.id.tvLevelProgress)
        tvLevelProgress.text = LevelManager.getLevelInfo(userXp)

        val savedName = sharedPreferences.getString("user_name", "Green Pioneer")
        findViewById<TextView>(R.id.tvProfileName).text = savedName
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        val currentPoints = sharedPreferences.getInt("current_points", 0)
        tvTotalPoints.text = currentPoints.toString()

        val userXp = sharedPreferences.getInt("user_xp", 0)

        val tvLevelTitle = findViewById<TextView>(R.id.tvMemberLevel)
        tvLevelTitle.text = LevelManager.getLevelTitle(userXp)
        tvLevelTitle.setTextColor(LevelManager.getLevelColor(userXp))

        val tvLevelProgress = findViewById<TextView>(R.id.tvLevelProgress)
        tvLevelProgress.text = LevelManager.getLevelInfo(userXp)

        val savedName = sharedPreferences.getString("user_name", "Green Pioneer #8829")
        tvProfileName.text = savedName

        val savedUri = sharedPreferences.getString("profile_image_uri", null)
        if (savedUri != null) {
            com.bumptech.glide.Glide.with(this)
                .load(Uri.parse(savedUri))
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(ivProfilePic)
        } else {
            ivProfilePic.setImageResource(R.drawable.ic_default_avatar)
        }
    }

    private fun saveProfileImage(uriString: String) {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("profile_image_uri", uriString).apply()
    }
    private fun showEditNameDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_dialog_edit_name, null)
        val etNewName = dialogView.findViewById<android.widget.EditText>(R.id.etNewName)

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val currentName = sharedPreferences.getString("user_name", "Green Pioneer #8829")
        etNewName.setText(currentName)

        builder.setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val newName = etNewName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    sharedPreferences.edit().putString("user_name", newName).apply()

                    tvProfileName.text = newName
                    Toast.makeText(this, "Name updated!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()

        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.GRAY)
    }
}
