package com.example.tesis.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.PopulationManager
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OsmdroidViewModel(count: Int): ViewModel() {

    private var populationManager = PopulationManager(count)

    fun resetPopulation() {
        populationManager.reset()
    }

    fun addAddresses(point: GeoPoint) {
        populationManager.addAddressMatrix(point)
    }

    fun getBestRoute(): ArrayList<GeoPoint> {
        val output = arrayListOf<GeoPoint>()
        val time = measureTimeMillis {
            populationManager.calculateMatrix()
            val individual = GeneticAlgorithm(populationManager).executeOX()
            individual.list.forEach { address ->
                output.add(populationManager.entries[address])
            }
        }
        Log.d("time","total time is: $time milisegundos")
        return output
    }
}