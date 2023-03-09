package com.example.tesis.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesis.domain.GeneticAlgorithm
import com.example.tesis.domain.Location
import com.example.tesis.domain.PopulationManager
import com.example.tesis.data.repository.OpenStreetMapRepository
import com.example.tesis.utils.Response
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OsmdroidViewModel(count: Int, context: Context): ViewModel() {

    private var populationManager = PopulationManager(count, context)
    private val bestRoute = arrayListOf<GeoPoint>()
    private val repository = OpenStreetMapRepository()
    private var pointMutableLiveData = MutableLiveData<Response<List<Location>>>()

    val point: LiveData<Response<List<Location>>>
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
        repository.getAddressPoint(address).onEach {
            if (it is Response.Success) {
                pointMutableLiveData.value = Response.Success(it.value)
            } else {
                pointMutableLiveData.value = it
            }
        }.launchIn(viewModelScope)
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
        bestRoute.add(bestRoute.first())
        Log.d("time","total time is: $time milisegundos")
        return bestRoute
    }
}