package com.example.tesis

import android.util.Log
import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.Population
import com.example.tesis.core.getPXS
import org.osmdroid.util.GeoPoint
import kotlin.system.measureTimeMillis

object MainTest {
    const val n = 4
}

fun main(entries: MutableList<GeoPoint>): List<Int> {

    val list = mutableListOf<Int>()

    val time = measureTimeMillis {

        val (px1, px2) = getPXS()

        /*for (_i in 0 until 5) {
            val population = Population().createPopulation()
            val populationOX = population.toMutableList()
            val populationPMX =  population.toMutableList()
            val individual = GeneticAlgorithm(populationOX, Pair(px1, px2)).executeOX()
            //GeneticAlgorithm(populationPMX, Pair(px1, px2)).executePMX()
        }*/
        val pop = Population(entries)
        val population = pop.createPopulation()
        val populationOX = population.toMutableList()
        val individual = GeneticAlgorithm(pop, populationOX, Pair(px1, px2)).executeOX()
        list.addAll(individual.list)
    }

    Log.d("time","total time is: $time milisegundos")
    return list
}




