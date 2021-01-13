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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tesis.R
import com.example.tesis.core.RoadManagerObject
import com.example.tesis.databinding.ActivityOsmdroidBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

    companion object {
        private const val ZOOM = 16.0
        private const val WIDTH = 5.0f
        private const val COUNT = "COUNT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmdroidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getString(COUNT)?.let {
            model = OsmdroidViewModel(it.toInt())
            count = it.toInt()
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
        setMapComponents()
        setViewComponents()
    }

    private fun setCenter() {
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
        val locationListener = object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                location?.let {
                    putMarket(GeoPoint(it.latitude, it.longitude), false)
                    model.setMyLocation(GeoPoint(it.latitude, it.longitude))
                    println("ONLOCATIONCHANGED")
                    println(it.latitude)
                    println(it.longitude)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                //NOTHING HERE
            }

            override fun onProviderEnabled(provider: String?) {
                //NOTHING HERE
            }

            override fun onProviderDisabled(provider: String?) {
                //NOTHING HERE
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100F, locationListener)
    }

    private fun getAddressResults(address: String) {
        model.getPoints(address)
        model.point.observe(this@OsmdroidActivity,{ point ->
            //putMarket(point)
        })
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
        binding.run {
            maps.apply {
                overlays.apply {
                    clear()
                    add(0, mapEventsOverlay)
                    invalidate()
                }
            }
            calculate.isEnabled = false
        }
        setCenter()
        addresses = 0
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        p?.let {
            addresses++
            if (addresses <= count) {
                putMarket(it)
                if (addresses == count)
                    binding.calculate.isEnabled = true
            }
        }
        return true
    }

    private fun putMarket(geoPoint: GeoPoint, add: Boolean = true) {
        if (add)
            model.addAddresses(geoPoint)
        val marker = Marker(binding.maps)
        marker.position = geoPoint
        marker.icon = resources.getDrawable(R.drawable.marker_default, null)
        binding.maps.overlays.add(marker)
        binding.maps.invalidate()
    }

    private fun calculateRoute(bestRoute: ArrayList<GeoPoint>) {
        val road = RoadManagerObject.getRoadManager().getRoad(bestRoute)
        val polyline = RoadManager.buildRoadOverlay(road, Color.MAGENTA, WIDTH)
        binding.maps.overlays.add(polyline)
        road.mNodes.forEachIndexed { index, it ->
            if (index == 0 || index == road.mNodes.size - 1) {
                val nodeMarker = Marker(binding.maps)
                nodeMarker.position = it.mLocation
                when (index) {
                    0 -> {
                        nodeMarker.icon = resources.getDrawable(R.drawable.ic_start, null)
                    }
                    road.mNodes.size - 1 -> {
                        nodeMarker.icon = resources.getDrawable(R.drawable.ic_finish, null)
                    }
                }

                nodeMarker.title = "Step $index"
                nodeMarker.snippet = it.mInstructions
                nodeMarker.subDescription =
                    Road.getLengthDurationText(this, it.mLength, it.mDuration)
                nodeMarker.image = resources.getDrawable(R.drawable.ic_menu_offline, null)
                binding.maps.overlays.add(nodeMarker)
            }
        }
        Log.d("time road", road.mDuration.toString())

        binding.maps.invalidate()
    }
}