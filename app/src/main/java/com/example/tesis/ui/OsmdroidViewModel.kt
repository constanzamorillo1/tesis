package com.example.tesis.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tesis.domain.GeneticAlgorithm
import com.example.tesis.domain.Location
import com.example.tesis.domain.PopulationManager
import com.example.tesis.data.OpenStreetMapRepository
import com.example.tesis.domain.RepositoryResult
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

class OsmdroidViewModel(count: Int): ViewModel() {

    private var populationManager = PopulationManager(count)
    private val bestRoute = arrayListOf<GeoPoint>()
    private val repository = OpenStreetMapRepository()
    private var pointMutableLiveData = MutableLiveData<State>()

    val point: LiveData<State>
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
        pointMutableLiveData.postValue(State.ShowLoading)
        repository.getAddressPoint(address) {
            Log.d("results", it.toString())
            if (it is RepositoryResult.Success) {
               pointMutableLiveData.value = State.Success(it.value.results[0].locations)
            }
            pointMutableLiveData.postValue(State.HideLoading)
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
        bestRoute.add(bestRoute.first())
        Log.d("time","total time is: $time milisegundos")
        return bestRoute
    }
}

sealed class State {
    object HideLoading : State()
    object ShowLoading : State()
    class Success(val response: List<Location>): State()
}