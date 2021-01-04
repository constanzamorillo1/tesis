package com.example.tesis.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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

    private var addresses = arrayListOf<GeoPoint>()
    private lateinit var binding: ActivityOsmdroidBinding
    private lateinit var mapEventsOverlay: MapEventsOverlay
    private lateinit var model: OsmdroidViewModel

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
        binding.maps.overlays.clear()
        binding.maps.overlays.add(0, mapEventsOverlay)
        binding.maps.invalidate()
        addresses.clear()
        model.resetPopulation()
        setCenter()
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        p?.let {
            putMarket(it)
            addresses.add(it)
        }
        return true
    }

    private fun putMarket(geoPoint: GeoPoint) {
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