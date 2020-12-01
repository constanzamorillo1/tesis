package com.example.tesis

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_osmdroid.*
import org.apache.commons.lang3.mutable.Mutable
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

@SuppressLint("UseCompatLoadingForDrawables")
class OsmdroidActivity : AppCompatActivity(), MapEventsReceiver {

    private var list = arrayListOf<GeoPoint>()
    private lateinit var galist: List<Int>
    private lateinit var roadManager: MapQuestRoadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osmdroid)
        initOSMaps()
    }

    private fun initOSMaps() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        maps.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setBuiltInZoomControls(true)
            controller.apply {
                setCenter(GeoPoint(-33.292991, -66.336391))
                setZoom(16.5)
            }
        }
        roadManager = MapQuestRoadManager("A3qGuyvHi1WfLxj1KKh51zxDspxAfOAq")
        roadManager.addRequestOption("routeType=bicycle")
        roadManager.addRequestOption("timeType=1")

        /*val overItem = OverlayItem("Hello Office", "my office", GeoPoint(-33.292991, -66.336391))
        val overItem2 = OverlayItem("Hello Office", "my office", GeoPoint(-33.294758, -66.336007))
        overItem.getMarker(0)
        val items = arrayListOf(overItem, overItem2)
        val mOverlay = ItemizedOverlayWithFocus(
            applicationContext,
            items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?) = true

                override fun onItemLongPress(index: Int, item: OverlayItem?) = false

            })

        mOverlay.setFocusItemsOnTap(true)
        maps.overlays.add(mOverlay)*/

        val mapEventsOverlay = MapEventsOverlay(this)
        maps.overlays.add(0, mapEventsOverlay)
        InfoWindow.closeAllInfoWindowsOn(maps)
        //calculateRoute()
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
        val marker = Marker(maps)
        marker.position = geoPoint
        marker.icon = resources.getDrawable(R.drawable.marker_default, null)
        maps.overlays.add(marker)
        maps.invalidate()
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
        maps.overlays.clear()

        val listBis = arrayListOf<GeoPoint>()
        galist.forEach {
            listBis.add(list[it])
        }

        val road = roadManager.getRoad(listBis)
        val polyline = RoadManager.buildRoadOverlay(road, Color.MAGENTA, 5.0f)
        maps.overlays.add(polyline)
        road.mNodes.forEachIndexed { index, it ->
            if (index == 0 || index == road.mNodes.size - 1) {
                val nodeMarker = Marker(maps)
                nodeMarker.position = it.mLocation
                if (index == 0)
                    nodeMarker.icon = resources.getDrawable(R.drawable.marker_default, null)
                else
                    nodeMarker.icon = resources.getDrawable(R.drawable.marker_default_focused_base, null)
                nodeMarker.title = "Step $index"
                nodeMarker.snippet = it.mInstructions
                nodeMarker.subDescription =
                    Road.getLengthDurationText(this, it.mLength, it.mDuration)
                nodeMarker.image = resources.getDrawable(R.drawable.ic_menu_offline, null)
                maps.overlays.add(nodeMarker)
            }
        }
        listBis.forEach{
            putMarket(it)
        }
        Log.d("time road", road.mDuration.toString())

        /*val road2 = roadManager.getRoad(list)
        val polyline2 = RoadManager.buildRoadOverlay(road2, Color.BLACK, 10.0f)
        maps.overlays.add(polyline2)
        road2.mNodes.forEachIndexed { index, it ->
            val nodeMarker = Marker(maps)
            nodeMarker.position = it.mLocation
            nodeMarker.icon = resources.getDrawable(R.drawable.marker_default, null)
            nodeMarker.title = "Step $index"
            nodeMarker.snippet = it.mInstructions
            nodeMarker.subDescription = Road.getLengthDurationText(this, it.mLength, it.mDuration)
            nodeMarker.image = resources.getDrawable(R.drawable.ic_menu_offline, null)
            maps.overlays.add(nodeMarker)
        }*/

        maps.invalidate()
    }
}