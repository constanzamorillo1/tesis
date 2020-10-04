package com.example.tesis

import com.example.tesis.core.GeneticAlgorithm
import kotlin.system.measureTimeMillis

object MainTest {
    const val n = 8
}

fun main() {

    val time = measureTimeMillis {
        GeneticAlgorithm.execute()
    }

    print("total time is: $time milisegundos")
}




