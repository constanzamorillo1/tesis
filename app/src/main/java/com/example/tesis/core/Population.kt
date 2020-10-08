package com.example.tesis.core

import com.example.tesis.MainTest
import kotlin.random.Random

class Population {
    private var list: MutableList<Individual> = mutableListOf()
    private val matrix = createDistanceMatrix()

    fun createPopulation(): MutableList<Individual> {
        /**
         * Definir como variable de entrada (tamaño de entrada)
         * Pensar un apartado para definir parametros de algoritmo genetico
         */

        /**
         * Si n (cantidad de direcciones) es mayor a n entonces 500 sino factorial de n es el top
         */
        val top: Long = 5000

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
        while (i.list.size < MainTest.n) {
            val randomA = Random.nextInt(0, MainTest.n)
            if (!i.list.contains(randomA)) {
                i.list.add(randomA)
            }
        }

        for (pos in 0 until MainTest.n - 1) {
            val gen = i.list[pos]
            val gen2 = i.list[pos+1]
            i.distance += (matrix[gen][gen2])
        }

        i.distance += (matrix[MainTest.n-1][i.list[0]])

        return i
    }

    fun calculateDistance(list: MutableList<Int>): Int {
        var distance = 0
        for (pos in 0 until MainTest.n - 1) {
            val gen = list[pos]
            val gen2 = list[pos+1]
            distance += (matrix[gen][gen2])
        }

        distance += (matrix[MainTest.n-1][list[0]])
        return distance
    }

    /**
     * Anexo: explicar porque se uso esta función
     */
    private fun compareIndividuals(individualA: Individual, individualB: Individual): Boolean {
        return individualA.list.zip(individualB.list).all { (eltA, eltB) -> eltA == eltB }
    }

    private fun createDistanceMatrix(): Array<IntArray> {
        val matrix = Array(MainTest.n) {IntArray(MainTest.n)}
        matrix[0][1] = 500
        matrix[0][2] = 1600
        matrix[0][3] = 2500
        matrix[0][4] = 1800
        matrix[0][5] = 100
        matrix[0][6] = 200
        matrix[0][7] = 300
        matrix[1][0] = 850
        matrix[1][2] = 1600
        matrix[1][3] = 2700
        matrix[1][4] = 1300
        matrix[1][5] = 200
        matrix[1][6] = 300
        matrix[1][7] = 400
        matrix[2][0] = 2100
        matrix[2][1] = 1600
        matrix[2][3] = 1300
        matrix[2][4] = 500
        matrix[2][5] = 300
        matrix[2][6] = 400
        matrix[2][7] = 500
        matrix[3][0] = 1900
        matrix[3][1] = 1900
        matrix[3][2] = 1800
        matrix[3][4] = 1300
        matrix[3][5] = 400
        matrix[3][6] = 500
        matrix[3][7] = 600
        matrix[4][0] = 1800
        matrix[4][1] = 1300
        matrix[4][2] = 500
        matrix[4][3] = 1300
        matrix[4][4] = 0
        matrix[4][5] = 500
        matrix[4][6] = 600
        matrix[4][7] = 700
        matrix[5][0] = 100
        matrix[5][1] = 500
        matrix[5][2] = 1600
        matrix[5][3] = 2500
        matrix[5][4] = 1800
        matrix[5][6] = 200
        matrix[5][7] = 300
        matrix[6][0] = 850
        matrix[6][1] = 200
        matrix[6][2] = 1600
        matrix[6][3] = 2700
        matrix[6][4] = 1300
        matrix[6][5] = 200
        matrix[6][7] = 400
        matrix[7][0] = 2100
        matrix[7][1] = 1600
        matrix[7][2] = 300
        matrix[7][3] = 1300
        matrix[7][4] = 500
        matrix[7][5] = 300
        matrix[7][6] = 400

        return matrix
    }

    fun showMatrix() {
        matrix.forEach {
            it.forEach {address ->
                print("| $address |")
            }
            println()
        }
    }

    fun showPopulation() {
        list.sort()
        list.forEach {
            print("4 ")
            for (i in it.list) {
                print("$i ")
            }
            print("La distancia es: ${it.distance} metros")
            println()
        }
    }
}