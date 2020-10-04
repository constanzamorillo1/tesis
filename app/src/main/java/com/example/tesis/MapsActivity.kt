package com.example.tesis

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        val cbPitampura = LatLng(28.6969421, 77.1423825)
        val cbDwarka = LatLng(28.5864285, 77.0478732)
        val cbNoida = LatLng(28.5824097, 77.3163259)
        val cbDehradun = LatLng(30.3098438, 78.0211684)

        mMap.addMarker(MarkerOptions().position(cbPitampura).title("Coding Blocks"))
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        cbPitampura,
                        10f
                )
        )

        mMap.addPolyline(
                PolylineOptions()
                        .add(cbPitampura)
                        .add(cbDwarka)
                        .add(cbNoida)
                        .add(cbDehradun)
                        .width(8f)
                        .color(Color.RED)
        )

        val url = URL("https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyCemtupG1RJ4pf1iMkWUoTSubNQrQElV3U")

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        val `is`: InputStream = connection.inputStream
        val rd = BufferedReader(InputStreamReader(`is`))
        var line: String?
        while (rd.readLine().also { line = it } != null) {
            println(line)
        }
        rd.close()

    }
}
