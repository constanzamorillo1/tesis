package com.example.tesis.core

import android.util.Log
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.Population
import com.example.tesis.core.getPXS
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

object MainTest {
    const val n = 5
}

fun main(entries: MutableList<GeoPoint>): List<Int> {

    val list = mutableListOf<Int>()

    val time = measureTimeMillis {

        val (px1, px2) = getPXS()
        val pop = Population(entries)
        val population = pop.createPopulation()
        val populationOX = population.toMutableList()
        val individual = GeneticAlgorithm(pop, populationOX, Pair(px1, px2)).executeOX()
        list.addAll(individual.list)
    }

    Log.d("time","total time is: $time milisegundos")
    return list
}




