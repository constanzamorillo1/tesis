package com.example.tesis.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.PopulationManager
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OmsdroidViewModel: ViewModel() {

    fun getBestRoute(addresses: MutableList<GeoPoint>): ArrayList<GeoPoint> {
        val population = PopulationManager(addresses)
        val output = arrayListOf<GeoPoint>()
        val time = measureTimeMillis {
            val individual = GeneticAlgorithm(addresses.size, population).executeOX()
            individual.list.forEach { address ->
                output.add(addresses[address])
            }
        }
        Log.d("time","total time is: $time milisegundos")
        return output
    }
}