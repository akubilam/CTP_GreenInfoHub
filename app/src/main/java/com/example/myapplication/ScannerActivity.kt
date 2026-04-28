package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.os.VibrationEffect
import android.widget.ImageButton
import androidx.camera.core.CameraControl

class ScannerActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView
    private var isScanning = true
    private var scanStartTime = System.currentTimeMillis()
    private val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var cameraControl: CameraControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        viewFinder = findViewById(R.id.viewFinder)

        val guideLayout = findViewById<View>(R.id.guideLayout)

        findViewById<ImageButton>(R.id.btnGuide).setOnClickListener {
            guideLayout.visibility = View.VISIBLE
            isScanning = false
        }

        findViewById<Button>(R.id.btnGotIt).setOnClickListener {
            guideLayout.visibility = View.GONE
            isScanning = true
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_scanner

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
                    true
                }
                R.id.nav_more -> {
                    startActivity(Intent(this, MoreActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val cardView = findViewById<View>(R.id.resultCard)

        cardView?.findViewById<Button>(R.id.btnClose)?.setOnClickListener {
            cardView.visibility = View.GONE
            findViewById<View>(R.id.scanLine).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvStatus).text = "Scanning... Align label in center"
            startScanAnimation()
            isScanning = true
            startScanningTimer()
        }

        // Check camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 10
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        startScanAnimation()
        startScanningTimer()
    }
    // Laser beam up and down scanning animation
    private fun startScanAnimation() {
        val animation = TranslateAnimation(0f, 0f, 0f, 400f) // 400f 對應 160dp 的寬度
        animation.duration = 2000
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        findViewById<View>(R.id.scanLine).startAnimation(animation)
    }

    // Add vibration feedback (when successfully detected)
    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun triggerVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100)
        }
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(1280, 720)) // 強制高清，細節更清楚
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, PlasticScannerAnalyzer { number ->
                        if (isScanning) {
                            runOnUiThread { showResult(number) }
                        }
                    })
                }

            try {
                val camera = cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer
                )

                cameraControl = camera.cameraControl
                cameraControl?.enableTorch(false)
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera start failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private var lastDetectedNumber = ""
    private var detectionCount = 0
    private fun showResult(number: String) {
        if (!isScanning) return

        if (number == lastDetectedNumber) {
            detectionCount++
        } else {
            lastDetectedNumber = number
            detectionCount = 1
        }

        if (detectionCount < 3) return

        val info = recyclingData[number] ?: return
        isScanning = false

        val cardView = findViewById<View>(R.id.resultCard) ?: return

        try {
            // 更新內容
            cardView.findViewById<TextView>(R.id.tvPlasticNumber).text = number
            cardView.findViewById<TextView>(R.id.tvPlasticName).text = info.name
            cardView.findViewById<TextView>(R.id.tvIntro).text = info.intro
            cardView.findViewById<TextView>(R.id.tvLocation).text = info.location
            cardView.findViewById<TextView>(R.id.tvWarning).text = info.warning
            cardView.findViewById<TextView>(R.id.tvSteps).text = info.steps

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(100)
            }

            findViewById<TextView>(R.id.tvStatus).text = "Success!"
            findViewById<View>(R.id.scanLine).apply {
                clearAnimation()
                visibility = View.INVISIBLE
            }
            cardView.visibility = View.VISIBLE
            TaskManager.completeTask(this, "Scan 3 plastic bottles", 30, 20)

        } catch (e: Exception) {
            android.util.Log.e("ScannerError", "UI Update failed: ${e.message}")
            isScanning = true
        }
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startScanningTimer() {
        scanStartTime = System.currentTimeMillis()
        timeoutHandler.postDelayed(object : Runnable {
            override fun run() {
                if (isScanning) {
                    val elapsed = System.currentTimeMillis() - scanStartTime
                    if (elapsed in 4000..4500) {
                        cameraControl?.setZoomRatio(2.0f)
                        findViewById<TextView>(R.id.tvStatus).text = "Zooming in for tiny labels..."
                    }
                    if (elapsed > 8000) { // 8秒未果
                        findViewById<TextView>(R.id.tvStatus).text = "Still searching... Try tilting the bottle for shadows."
                    }
                    timeoutHandler.postDelayed(this, 1000)
                } else {
                    cameraControl?.setZoomRatio(1.0f)
                }
            }
        }, 1000)
    }
}