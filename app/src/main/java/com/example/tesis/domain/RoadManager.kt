package com.example.tesis.domain

import android.content.Context
import android.os.StrictMode
import org.osmdroid.bonuspack.routing.OSRMRoadManager

object RoadManagerObject {
    private const val ROUTE_TYPE_REQUEST_OPTION = "routeType=bicycle"
    private const val TIME_TYPE_REQUEST_OPTION = "timeType=1"

    private lateinit var roadManager: OSRMRoadManager
    private var init = false

    fun getRoadManager(context: Context): OSRMRoadManager {
        if (!init) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            roadManager = OSRMRoadManager(context, "constanza")
            roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
            init = true
        }
        return roadManager
    }
}