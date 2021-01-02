package com.example.tesis.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tesis.R
import com.example.tesis.databinding.ActivityOsmdroidBinding
import com.example.tesis.core.main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
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

    private var list = arrayListOf<GeoPoint>()
    private lateinit var galist: List<Int>
    private lateinit var roadManager: MapQuestRoadManager
    private lateinit var binding: ActivityOsmdroidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmdroidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOSMaps()
    }

    private fun initOSMaps() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding.maps.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.apply {
                val mLocationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(applicationContext), binding.maps)
                mLocationOverlay.enableMyLocation()
                mLocationOverlay.enableFollowLocation()
                binding.maps.overlays.add(mLocationOverlay)
                setZoom(15.0)
            }
        }
        roadManager = MapQuestRoadManager("A3qGuyvHi1WfLxj1KKh51zxDspxAfOAq")
        roadManager.addRequestOption("routeType=bicycle")
        roadManager.addRequestOption("timeType=1")

        val mapEventsOverlay = MapEventsOverlay(this)
        binding.maps.overlays.add(0, mapEventsOverlay)
        InfoWindow.closeAllInfoWindowsOn(binding.maps)

        list.forEach {
            putMarket(it)
        }

        binding.calculate.setOnClickListener {
            GlobalScope.launch {
                galist = main(list)
                calculateRoute()
            }
        }
        binding.reset.setOnClickListener {
            binding.maps.overlays.clear()
            binding.maps.overlays.add(0, mapEventsOverlay)
            binding.maps.invalidate()
            list.clear()
        }
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        p?.let {
            putMarket(it)
            list.add(it)
            if (list.size == 4) {
                //calculateDistance()
                galist = main(list)
                calculateRoute()
            }
        }
        return true
    }

    private fun putMarket(geoPoint: GeoPoint) {
        val marker = Marker(binding.maps)
        marker.position = geoPoint
        marker.icon = resources.getDrawable(R.drawable.marker_default, null)
        binding.maps.overlays.add(marker)
        binding.maps.invalidate()
    }

    private fun calculateDistance() {
        val origin = list.first()
        list.forEach{ destination ->
            val distance = roadManager.getRoad(arrayListOf(origin, destination)).mLength
            Log.d("distance: ", distance.toString())
            Log.d("Point: ", destination.toString())
        }
    }

    private fun calculateRoute() {
        binding.maps.overlays.clear()

        val listBis = arrayListOf<GeoPoint>()
        galist.forEach {
            listBis.add(list[it])
        }

        val road = roadManager.getRoad(listBis)
        val polyline = RoadManager.buildRoadOverlay(road, Color.MAGENTA, 5.0f)
        binding.maps.overlays.add(polyline)
        road.mNodes.forEachIndexed { index, it ->
            val nodeMarker = Marker(binding.maps)
            nodeMarker.position = it.mLocation
            if (index == 0)
                nodeMarker.icon = resources.getDrawable(R.drawable.marker_default, null)
            else
                nodeMarker.icon = resources.getDrawable(
                    R.drawable.marker_default_focused_base,
                    null
                )
            nodeMarker.title = "Step $index"
            nodeMarker.snippet = it.mInstructions
            nodeMarker.subDescription =
                Road.getLengthDurationText(this, it.mLength, it.mDuration)
            nodeMarker.image = resources.getDrawable(R.drawable.ic_menu_offline, null)
            binding.maps.overlays.add(nodeMarker)
        }
        Log.d("time road", road.mDuration.toString())

        binding.maps.invalidate()
    }
}