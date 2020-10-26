package com.example.tesis

import com.example.tesis.core.GeneticAlgorithm
import com.example.tesis.core.Population
import com.example.tesis.core.getPXS
import kotlin.system.measureTimeMillis

object MainTest {
    const val n = 8
}

fun main() {

    val time = measureTimeMillis {

        val (px1, px2) = getPXS()

        for (_i in 0 until 5) {
            val population = Population().createPopulation()
            val populationOX = population.toMutableList()
            val populationPMX =  population.toMutableList()
            GeneticAlgorithm(populationOX, Pair(px1, px2)).executeOX()
            GeneticAlgorithm(populationPMX, Pair(px1, px2)).executePMX()
        }
    }

    print("total time is: $time milisegundos")
}




