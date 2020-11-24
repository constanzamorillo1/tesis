package com.example.tesis

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_osmdroid.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osmdroid)
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
                setZoom(17.0)
            }
        }

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
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        p?.let {
            putMarket(it)
            list.add(it)
            if (list.size > 1) {
                calculeRoute()
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

    private fun calculeRoute() {
        maps.overlays.clear()
        val roadManager = MapQuestRoadManager("A3qGuyvHi1WfLxj1KKh51zxDspxAfOAq")
        roadManager.addRequestOption("routeType=bicycle")
        roadManager.addRequestOption("timeType=1")
        val road = roadManager.getRoad(list)
        val polyline = RoadManager.buildRoadOverlay(road, Color.MAGENTA, 5.0f)
        maps.overlays.add(polyline)
        maps.invalidate()
        road.mNodes.forEachIndexed { index, it ->
            val nodeMarker = Marker(maps)
            nodeMarker.position = it.mLocation
            nodeMarker.icon = resources.getDrawable(R.drawable.marker_default, null)
            nodeMarker.title = "Step $index"
            nodeMarker.snippet = it.mInstructions
            nodeMarker.subDescription = Road.getLengthDurationText(this, it.mLength, it.mDuration)
            nodeMarker.image = resources.getDrawable(R.drawable.ic_menu_offline, null)
            maps.overlays.add(nodeMarker)
        }
        maps.invalidate()
    }
}