package com.example.tesis.core

import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import kotlin.random.Random

class PopulationManager(private val count: Int) {
    private val matrix: Array<Array<Model>> = Array(count) {
        Array(count) {
            Model(0.0)
        }
    }
    private val arrayDistanceOrigin = arrayListOf<GoAndGoBackDistance>()
    private lateinit var myLocation: GeoPoint
    val entries = mutableListOf<GeoPoint>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    fun setMyLocation(location: GeoPoint) {
        myLocation = location
        arrayDistanceOrigin.add(GoAndGoBackDistance(0.0, 0.0))
    }

    fun createPopulation(): MutableList<Individual> {
        /**
         * Si n (cantidad de direcciones) es mayor a n entonces 500 sino factorial de n es el top
         */
        val list = mutableListOf<Individual>()
        var top: Long = factorial(entries.size) / 2
        if (top > 500)
            top = 500

        while (list.size < top){
            val i = createIndividual()
            if (list.isEmpty()) list.add(i)
            var indice = 0
            var isDifferent = true
            while (indice < list.size && isDifferent) {
                if (compareIndividuals(list[indice], i)) {
                    isDifferent = false
                }
                indice++
            }
            if (isDifferent) list.add(i)
        }
        list.sort()
        return list
    }

    private fun factorial(num: Int): Long {
        var result = 1L
        for (i in 2 .. num) result *= i
        return result
    }

    private fun createIndividual(): Individual {
        val i = Individual()
        while (i.list.size < entries.size) {
            val randomA = Random.nextInt(0, entries.size)
            if (!i.list.contains(randomA)) {
                i.list.add(randomA)
            }
        }
        i.distance = calculateDistance(i)
        return i
    }

    fun calculateDistance(individual: Individual): Double {
        var distance = 0.0
        for (pos in 0 until entries.size - 1) {
            val gen = individual.list[pos]
            val gen2 = individual.list[pos+1]
            distance += (matrix[gen][gen2]).distance
        }

        distance += (matrix[entries.size-1][individual.list.first()]).distance
        distance += arrayDistanceOrigin[individual.list.first()].distanceGo
        distance += arrayDistanceOrigin[individual.list.first()].distanceGoBack
        return distance
    }

    /**
     * Anexo: explicar porque se uso esta función
     */
    private fun compareIndividuals(individualA: Individual, individualB: Individual): Boolean {
        return individualA.list.zip(individualB.list).all { (eltA, eltB) -> eltA == eltB }
    }

    fun addAddressMatrix(point: GeoPoint) {
        entries.add(point)
        job?.cancel()
        job = coroutineScope.launch {
            calculateDistanceOrigin(point)
            calculateMatrix()
            delay(DELAY)
        }
    }

    private fun calculateDistanceOrigin(point: GeoPoint) {
        val distanceGo = RoadManagerObject.getRoadManager()
            .getRoad(arrayListOf(myLocation, point))
            .mLength
        val distanceGoBack = RoadManagerObject.getRoadManager()
            .getRoad(arrayListOf(point, myLocation))
            .mLength
        arrayDistanceOrigin.add(GoAndGoBackDistance(distanceGo, distanceGoBack))
    }

    fun calculateMatrix() {
        copy().forEachIndexed { indexOrigin, geoPointOrigin ->
            copy().forEachIndexed { indexDestination, geoPointDestination ->
                if (indexOrigin != indexDestination) {
                    if (!matrix[indexOrigin][indexDestination].inicialize) {
                        val distance = RoadManagerObject.getRoadManager()
                            .getRoad(arrayListOf(geoPointOrigin, geoPointDestination))
                            .mLength
                        matrix[indexOrigin][indexDestination] = Model(distance,true)
                    }
                }
            }
        }
    }

    fun reset() {
        entries.clear()
        for (i in matrix[0].indices)
            for (j in matrix[0].indices) {
                matrix[i][j] = Model(0.0)
            }
    }

    fun toStringMatrix() {
        for (i in matrix[0].indices) {
            for (j in matrix[0].indices)
                print("${matrix[i][j]} - ")
            println()
        }

    }

    private fun copy(): MutableList<GeoPoint> {
        val list = mutableListOf<GeoPoint>()
        entries.forEach {
            list.add(it)
        }
        return list
    }

    inner class Model(val distance: Double, var inicialize: Boolean = false) {
        override fun toString(): String{
            return "Distance : $distance and Inicialize: $inicialize"
        }
    }

    inner class GoAndGoBackDistance(val distanceGo: Double, val distanceGoBack: Double)

    companion object {
        private const val DELAY = 1000L
    }
}