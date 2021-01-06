package com.example.tesis.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.PopulationManager
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OsmdroidViewModel(count: Int): ViewModel() {

    private var populationManager = PopulationManager(count)
    private val bestRoute = arrayListOf<GeoPoint>()

    fun resetPopulation() {
        bestRoute.clear()
        populationManager.reset()
    }

    fun addAddresses(point: GeoPoint) {
        populationManager.addAddressMatrix(point)
    }

    fun setMyLocation(location: GeoPoint) {
        populationManager.setMyLocation(location)
        bestRoute.add(location)
    }

    fun getBestRoute(): ArrayList<GeoPoint> {
        val time = measureTimeMillis {
            populationManager.calculateDistanceOrigin()
            populationManager.calculateMatrix()
            populationManager.toStringMatrix()
            val individual = GeneticAlgorithm(populationManager).executeOX()
            individual.list.forEach { address ->
                bestRoute.add(populationManager.entries[address])
            }
        }
        Log.d("time","total time is: $time milisegundos")
        return bestRoute
    }
}