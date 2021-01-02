package com.example.tesis.core

import android.os.StrictMode
import org.osmdroid.util.GeoPoint
import kotlin.random.Random

class PopulationManager(private val entries: MutableList<GeoPoint>) {
    private var list: MutableList<Individual> = mutableListOf()
    private lateinit var matrix: Array<DoubleArray>

    init {
        createDistanceMatrix()
    }

    fun createPopulation(): MutableList<Individual> {
        /**
         * Definir como variable de entrada (tamaño de entrada)
         * Pensar un apartado para definir parametros de algoritmo genetico
         */

        /**
         * Si n (cantidad de direcciones) es mayor a n entonces 500 sino factorial de n es el top
         */
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

        for (pos in 0 until entries.size - 1) {
            val gen = i.list[pos]
            val gen2 = i.list[pos+1]
            i.distance += (matrix[gen][gen2])
        }

        i.distance += (matrix[entries.size-1][i.list[0]])

        return i
    }

    fun calculateDistance(individual: Individual): Double {
        var distance = 0.0
        for (pos in 0 until entries.size - 1) {
            val gen = individual.list[pos]
            val gen2 = individual.list[pos+1]
            distance += (matrix[gen][gen2])
        }

        distance += (matrix[entries.size-1][individual.list[0]])
        return distance
    }

    /**
     * Anexo: explicar porque se uso esta función
     */
    private fun compareIndividuals(individualA: Individual, individualB: Individual): Boolean {
        return individualA.list.zip(individualB.list).all { (eltA, eltB) -> eltA == eltB }
    }

    private fun createDistanceMatrix() {
        matrix = Array(entries.size) {
            DoubleArray(entries.size)
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        entries.forEachIndexed { indexOrigin, geoPointOrigin ->
            entries.forEachIndexed { indexDestination, geoPointDestination ->
                if (indexOrigin != indexDestination) {
                    val distance = RoadManagerObject.getRoadManager()
                        .getRoad(arrayListOf(geoPointOrigin, geoPointDestination))
                        .mLength
                    matrix[indexOrigin][indexDestination] = distance
                }
            }
        }
    }
}