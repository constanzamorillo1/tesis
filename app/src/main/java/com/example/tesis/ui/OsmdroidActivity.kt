package com.example.tesis.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tesis.R
import com.example.tesis.domain.RoadManagerObject
import com.example.tesis.databinding.ActivityOsmdroidBinding
import kotlinx.coroutines.*
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("UseCompatLoadingForDrawables")
class OsmdroidActivity : AppCompatActivity(), MapEventsReceiver {

    private lateinit var binding: ActivityOsmdroidBinding
    private lateinit var mapEventsOverlay: MapEventsOverlay
    private lateinit var model: OsmdroidViewModel
    private var count: Int = 0
    private var addresses: Int = 0
    private var myCurrentPosition = false

    companion object {
        private const val ZOOM = 16.0
        private const val WIDTH = 6.5f
        private const val COUNT = "COUNT"
        private const val RESET_STRING = ""
        private const val LABEL_ARRIVED_POINT = "Punto de llegada"
        private const val STEP = "PASO"
        private const val COLOR_ROUTE = "#FFD6614E"
        private const val TEXT_ADDRESS_ERROR = "No se puede encontrar la dirección"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmdroidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getInt(COUNT)?.let {
            model = OsmdroidViewModel(it, applicationContext)
            count = it
        }
        initOSMaps()
    }

    private fun initOSMaps() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setCenter()
        setObservers()
        setMapComponents()
        setViewComponents()
    }

    private fun setObservers() {
        model.point.observe(this@OsmdroidActivity) { response ->
            when (response) {
                is State.ShowLoading -> {
                    binding.viewProgress.visibility = View.VISIBLE
                }
                is State.HideLoading -> {
                    binding.viewProgress.visibility = View.INVISIBLE
                }
                is State.Success -> {
                    if (response.response.isNotEmpty()) {
                        val point = response.response.first()
                        if (point.adminArea5.isNotEmpty()) {
                            longPressHelper(GeoPoint(point.latLng.lat, point.latLng.lng))
                            binding.searchView.run {
                                setQuery(RESET_STRING, false)
                                clearFocus()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                TEXT_ADDRESS_ERROR,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setCenter() {
        disabledWindow(true)
        binding.maps.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.apply {
                val mLocationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(applicationContext), binding.maps)
                mLocationOverlay.enableMyLocation()
                mLocationOverlay.enableFollowLocation()
                binding.maps.overlays.add(mLocationOverlay)
                setZoom(ZOOM)
            }
        }
        setListenerMyLocation()
    }

    private fun setListenerMyLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = LocationListener { location ->
            if (!myCurrentPosition)
                location.let {
                    disabledWindow(false)
                    myCurrentPosition = true
                    model.setMyLocation(GeoPoint(it.latitude, it.longitude))
                    println("ONLOCATIONCHANGED")
                    println(it.latitude)
                    println(it.longitude)
                }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100F, locationListener)
    }

    private fun getAddressResults(address: String) {
        model.getPoints(address)
    }

    private fun setMapComponents() {
        mapEventsOverlay = MapEventsOverlay(this)
        binding.maps.overlays.add(0, mapEventsOverlay)
        InfoWindow.closeAllInfoWindowsOn(binding.maps)
    }

    private fun setViewComponents() {
        binding.calculate.setOnClickListener {
            startCalculate()
        }
        binding.reset.setOnClickListener {
          resetMap()
        }
        binding.searchView.run {
            this.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        getAddressResults(it)
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }


    private fun startCalculate() {
        disabledWindow(true)
        GlobalScope.launch {
            calculateRoute(model.getBestRoute())
            MainScope().launch {
                disabledWindow(false)
            }
        }
        binding.calculate.isEnabled = false
    }

    private fun disabledWindow(isBlock: Boolean) {
        if (isBlock) {
            binding.viewProgress.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            binding.viewProgress.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun resetMap() {
        model.resetPopulation()
        addresses = 0
        myCurrentPosition = false
        binding.run {
            maps.apply {
                overlays.apply {
                    clear()
                    add(0, mapEventsOverlay)
                    invalidate()
                }
            }
            calculate.isEnabled = false
            setCenter()
        }
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        p?.let {
            addresses++
            if (addresses <= count) {
                putMarket(it)
                if (addresses == count && myCurrentPosition)
                    binding.calculate.isEnabled = true
            } else {
                if (myCurrentPosition) {
                    binding.calculate.isEnabled = true
                }
            }
        }
        return true
    }

    private fun putMarket(geoPoint: GeoPoint) {
        model.addAddresses(geoPoint)
        val marker = Marker(binding.maps)
        marker.position = geoPoint
        marker.icon = resources.getDrawable(R.drawable.marker_default, null)
        marker.title = LABEL_ARRIVED_POINT
        binding.maps.overlays.add(marker)
        binding.maps.invalidate()
    }

    private fun putMarketWithNumber(bestRoute: ArrayList<GeoPoint>) {
        binding.maps.apply {
            overlays.apply {
                clear()
                add(0, mapEventsOverlay)
                invalidate()
            }
        }
        bestRoute.forEachIndexed { index, geoPoint ->
            if (index != 0 && index != bestRoute.size - 1) {
                val name = "@drawable/ic_number_${index-1}"
                val drawable = resources.getIdentifier(name, null, packageName)
                val marker = Marker(binding.maps)
                marker.position = geoPoint
                if (drawable > 0) {
                    marker.icon = resources.getDrawable(drawable, null)
                } else {
                    marker.icon = resources.getDrawable(R.drawable.marker_default, null)
                }
                marker.title = "$LABEL_ARRIVED_POINT $index"
                binding.maps.apply {
                    overlays.add(marker)
                    invalidate()
                }
            }
        }
    }

    private fun calculateRoute(bestRoute: ArrayList<GeoPoint>) {
        putMarketWithNumber(bestRoute)
        val road = RoadManagerObject.getRoadManager(applicationContext).getRoad(bestRoute)
        val polyline = RoadManager.buildRoadOverlay(road, Color.parseColor(COLOR_ROUTE), WIDTH)
        binding.maps.overlays.add(polyline)
        road.mNodes.forEachIndexed { index, it ->
            val nodeMarker = Marker(binding.maps)
            nodeMarker.position = it.mLocation
            when (index) {
                0 -> {
                    nodeMarker.icon = resources.getDrawable(R.drawable.ic_start, null)
                    nodeMarker.image = resources.getDrawable(R.drawable.ic_start, null)
                }
                else -> {
                    nodeMarker.icon = resources.getDrawable(R.drawable.ic_middle_marker, null)
                    nodeMarker.image = resources.getDrawable(R.drawable.ic_middle_marker, null)
                }
            }

            nodeMarker.title = "$STEP $index"
            nodeMarker.snippet = it.mInstructions
            nodeMarker.subDescription =
                Road.getLengthDurationText(this, it.mLength, it.mDuration)
            binding.maps.overlays.add(nodeMarker)
        }
        Log.d("time road", road.mDuration.toString())
        binding.maps.invalidate()
    }
}