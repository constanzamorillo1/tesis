package com.example.tesis

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_osmdroid.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem

class OsmdroidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osmdroid)

        Configuration.getInstance().load(applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext))

        maps.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setBuiltInZoomControls(true)
            controller.apply {
                setCenter(GeoPoint(-33.292991, -66.336391))
                setZoom(17.0)
            }
        }

        val overItem = OverlayItem("Hello Office", "my office", GeoPoint(-33.292991, -66.336391))
        val overItem2 = OverlayItem("Hello Office", "my office", GeoPoint(-33.294758,-66.336007))
        overItem.getMarker(0)
        val items = arrayListOf(overItem, overItem2)
        val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(applicationContext, items, object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?) = false

            override fun onItemLongPress(index: Int, item: OverlayItem?) = false

        })

        mOverlay.setFocusItemsOnTap(true)
        maps.overlays.add(mOverlay)
    }

}