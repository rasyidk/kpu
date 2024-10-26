package com.example.kpu.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kpu.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import android.location.LocationManager
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import android.location.Geocoder
import java.util.Locale


class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var selectedLocation: GeoPoint? = null
    private var marker: Marker? = null // Keep track of the marker
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var selectedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.map)
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Set default location (Jakarta)
//        val defaultLocation = GeoPoint(-6.2088, 106.8456)
//        mapView.controller.setZoom(10.0)
//        mapView.controller.setCenter(defaultLocation)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geocoder = Geocoder(this, Locale.getDefault())
        setDefaultLocation()
        // Button to confirm location
        val btnConfirm = findViewById<Button>(R.id.btnConfirmLocation)
        btnConfirm.setOnClickListener {
            selectedLocation?.let {
                // Return selected location to FormEntryActivity
                val resultIntent = Intent().apply {
                    putExtra("selected_location", selectedAddress)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }



        // Set map click listener
        mapView.setOnClickListener {
            // Remove previous marker if it exists
            marker?.let {
                mapView.overlays.remove(it)
                marker = null // Reset marker reference
            }
            selectedLocation = null // Clear selected location
        }

        // Set map touch listener to get clicked location
        mapView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                // Get the clicked position and convert to GeoPoint
                val clickedLocation = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
                // Convert IGeoPoint to GeoPoint
                val geoPoint = GeoPoint(clickedLocation.latitude, clickedLocation.longitude)
                addMarker(geoPoint)
                getLocationName(geoPoint)
            }
            false
        }


    }


    private fun setDefaultLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location> { location ->
            if (location != null) {
                val currentLocation = GeoPoint(location.latitude, location.longitude)
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(currentLocation)
                addMarker(currentLocation) // Optional: add marker to current location
                getLocationName(currentLocation)
            } else {
                val fallbackLocation = GeoPoint(-6.2088, 106.8456)
                mapView.controller.setZoom(10.0)
                mapView.controller.setCenter(fallbackLocation)
                addMarker(fallbackLocation)
                getLocationName(fallbackLocation)
            }
        })
    }
    private fun addMarker(location: GeoPoint) {
        // Clear previous markers
        mapView.overlays.clear()

        // Create and add a new marker
        marker = Marker(mapView).apply {
            position = location
            title = "Selected Location"
        }

        mapView.overlays.add(marker!!)
        selectedLocation = location
        mapView.invalidate()
    }

    private fun getLocationName(location: GeoPoint) {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses != null) {
                val address = addresses[0]?.getAddressLine(0) // Get the full address
                selectedAddress = addresses[0]?.getAddressLine(0)
                marker?.title = address // Set the address as the marker title
                marker?.showInfoWindow() // Show the info window
                Toast.makeText(this, "Location: $address", Toast.LENGTH_SHORT).show() // Show a toast with the address
        }else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Resume the map view
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Pause the map view
    }
}
