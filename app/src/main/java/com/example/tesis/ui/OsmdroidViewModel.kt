package com.example.tesis.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tesis.core.Address
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.Location
import com.example.tesis.core.PopulationManager
import com.example.tesis.domain.OpenStreetMapRepository
import com.example.tesis.domain.RepositoryResult
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OsmdroidViewModel(count: Int): ViewModel() {

    private var populationManager = PopulationManager(count)
    private val bestRoute = arrayListOf<GeoPoint>()
    private val repository = OpenStreetMapRepository()
    private val pointMutableLiveData = MutableLiveData<List<Location>>()

    val point: LiveData<List<Location>>
        get() = pointMutableLiveData

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

    fun getPoints(address: String){
        repository.getAddressPoint(address) {
            Log.d("results", it.toString())
           if (it is RepositoryResult.Success) {
               pointMutableLiveData.postValue(it.value.results[0].locations)
            }
        }
    }

    fun getBestRoute(): ArrayList<GeoPoint> {
        val time = measureTimeMillis {
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