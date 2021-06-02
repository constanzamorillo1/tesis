package com.example.tesis.domain

import android.os.StrictMode
import com.example.tesis.domain.AbstractRepository.Companion.KEY
import org.osmdroid.bonuspack.routing.MapQuestRoadManager

object RoadManagerObject {
    private const val ROUTE_TYPE_REQUEST_OPTION = "routeType=bicycle"
    private const val TIME_TYPE_REQUEST_OPTION = "timeType=1"

    private lateinit var roadManager: MapQuestRoadManager
    private var init = false

    fun getRoadManager(): MapQuestRoadManager {
        if (!init) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            roadManager = MapQuestRoadManager(KEY)
            roadManager.addRequestOption(ROUTE_TYPE_REQUEST_OPTION)
            roadManager.addRequestOption(TIME_TYPE_REQUEST_OPTION)
            init = true
        }
        return roadManager
    }
}