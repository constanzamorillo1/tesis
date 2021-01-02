package com.example.tesis.core

import org.osmdroid.bonuspack.routing.MapQuestRoadManager

object RoadManagerObject {
    private const val KEY = "A3qGuyvHi1WfLxj1KKh51zxDspxAfOAq"
    private const val ROUTE_TYPE_REQUEST_OPTION = "routeType=bicycle"
    private const val TIME_TYPE_REQUEST_OPTION = "timeType=1"

    private lateinit var roadManager: MapQuestRoadManager
    private var init = false

    fun getRoadManager(): MapQuestRoadManager {
        if (!init) {
            roadManager = MapQuestRoadManager(KEY)
            roadManager.addRequestOption(ROUTE_TYPE_REQUEST_OPTION)
            roadManager.addRequestOption(TIME_TYPE_REQUEST_OPTION)
            init = true
        }
        return roadManager
    }
}