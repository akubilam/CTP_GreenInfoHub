package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.opencsv.CSVReader
import java.io.InputStreamReader

data class RecycleStation(
    val address: String,
    val addressTC: String,
    val districtId: String,
    val wasteType: String,
    val legend: String,
    val contact: String,
    val openHour: String,
    val latitude: Double,
    val longitude: Double
) {
    // Calculate the distance (meters) between you and the user.
    fun getDistanceToLocation(loc: Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(loc.latitude, loc.longitude, latitude, longitude, results)
        return results[0]
    }
}

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastUserLocation: Location? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(this, "Location permissions are required to locate the recycle bin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_location
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_location -> true
                R.id.nav_info -> { startActivity(Intent(this, KnowledgeActivity::class.java)); true }
                R.id.nav_scanner -> { startActivity(Intent(this, ScannerActivity::class.java)); true }
                R.id.nav_more -> { startActivity(Intent(this, MoreActivity::class.java)); true }
                else -> false
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): android.view.View? {
                return null
            }

            override fun getInfoContents(marker: Marker): android.view.View {
                val view = layoutInflater.inflate(R.layout.custom_info_window, null)

                val tvTitle = view.findViewById<android.widget.TextView>(R.id.tvInfoTitle)
                val tvSnippet = view.findViewById<android.widget.TextView>(R.id.tvInfoSnippet)

                tvTitle.text = marker.title
                tvSnippet.text = marker.snippet // \n 換行

                return view
            }
        })
        map.uiSettings.isZoomControlsEnabled = true

        checkPermissionsAndGetLocation()

        TaskManager.completeTask(this, "Visit a Recycling station", 50, 10)
    }

    private fun checkPermissionsAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            map.isMyLocationEnabled = true
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lastUserLocation = location
                    val userLatLng = LatLng(location.latitude, location.longitude)

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

                    refreshMarkers(location)
                } else {
                    Toast.makeText(this, "Unable to obtain current location, please enable GPS.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun refreshMarkers(userLocation: Location) {
        if (!::map.isInitialized) return
        map.clear()

        val stations = readCsv("recycle_stations.csv")
        val oneKmMeters = 1000.0
        val boundsBuilder = LatLngBounds.Builder()
        val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
        var hasMarker = false

        map.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
        boundsBuilder.include(userLatLng)

        for (station in stations) {
            val dist = station.getDistanceToLocation(userLocation)
            if (dist <= oneKmMeters) {
                val stationLatLng = LatLng(station.latitude, station.longitude)
                val infoSnippet = "📍 Distance: ${"%.0f".format(dist)}m\n" +
                        "⏰ Hours: ${station.openHour}\n" +
                        "📞 Contact: ${station.contact}"
                map.addMarker(
                    MarkerOptions()
                        .position(stationLatLng)
                        .title(station.addressTC)
                        .snippet(infoSnippet)
                        .icon(getDescriptorFromDrawable(R.drawable.ic_recycle_bin, 80, 80))
                )
                boundsBuilder.include(stationLatLng)
                hasMarker = true
            }
        }

        if (hasMarker) {
            val bounds = boundsBuilder.build()
            val padding = (resources.displayMetrics.widthPixels * 0.15).toInt()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    // Read CSV
    private fun readCsv(fileName: String): List<RecycleStation> {
        val stations = mutableListOf<RecycleStation>()
        try {
            val inputStream = assets.open(fileName)
            val reader = CSVReader(InputStreamReader(inputStream))
            reader.readNext()
            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                if (line != null && line!!.size >= 22) {
                    val addressTC = "${line!![5]}, ${line!![6]}"
                    val longitude = line!![20].toDoubleOrNull() ?: continue
                    val latitude = line!![21].toDoubleOrNull() ?: continue

                    stations.add(RecycleStation(
                        address = "${line!![3]}, ${line!![4]}",
                        addressTC = addressTC,
                        districtId = line!![2],
                        wasteType = line!![11],
                        legend = line!![12],
                        contact = line!![14],
                        openHour = line!![17],
                        latitude = latitude,
                        longitude = longitude
                    ))
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MapActivity", "Error reading CSV", e)
        }
        return stations
    }

    private fun getDescriptorFromDrawable(resId: Int, width: Int, height: Int): BitmapDescriptor {
        val drawable = androidx.core.content.ContextCompat.getDrawable(this, resId)
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
