package com.example.tesis.core

import com.example.tesis.MainTest
import kotlin.random.Random

class GeneticAlgorithm(private val population: MutableList<Individual>, private val pxs: Pair<Int, Int>) {
    private val ranking = mutableListOf<Model>()

    fun executeOX() {

        val (ind1, ind2) = selection()
        println("INDIVIDUOS PADRES OX: ----->")
        println(ind1.toString())
        println(ind2.toString())

        for (i in 0 until 1000) {
            val (px1, px2) = pxs
            val (ind1, ind2) = selection()
            val (offStringOX1, offStringOX2) = crossoverOX(ind1, ind2, px1, px2)
            insertRanking(ind1, ind2, offStringOX1, offStringOX2, "OX")
            deleteParents()
        }
        ranking.sort()
        println()
        println("EL MEJORCITO ES: ${ranking[0].individual} - CROSSOVER: ${ranking[0].crossover}")
        println()
    }

    fun executePMX() {
        val (ind1, ind2) = selection()
        println("INDIVIDUOS PADRES PMX: ----->")
        println(ind1.toString())
        println(ind2.toString())

        for (i in 0 until 125) {
            val (px1, px2) = pxs

            val (ind1, ind2) = selection()

            val (offStringPMX1, offStringPMX2) = crossoverPMX(ind1, ind2, px1, px2)
            insertRanking(ind1, ind2, offStringPMX1, offStringPMX2, "PMX")
            deleteParents()
        }
        ranking.sort()
        println()
        println("EL MEJORCITO ES: ${ranking[0].individual} - CROSSOVER: ${ranking[0].crossover}")
        println()
    }

    private fun insertRanking(
        parent1: Individual, parent2: Individual,
        child1: Individual, child2: Individual,
        crossover: String
    ) {
        val backUp = listOf(
            Model(parent1, "PARENT"),
            Model(parent2, "PARENT"),
            Model(child1, crossover),
            Model(child2, crossover))

        val insert = backUp.sorted()[0]
        ranking.add(insert)
    }

    private fun selection(): Pair<Individual, Individual> {
        return Pair(population[population.size-2], population[population.size-1])
    }

    private fun deleteParents() {
        population.removeAt(population.size-2)
        population.removeAt(population.size-1)
    }

    private fun insertChildren(child1: Individual, child2: Individual) {
        population.add(child1)
        population.add(child2)
        population.sort()
    }

    private fun crossoverPMX(ind1:Individual, ind2:Individual, px1: Int, px2: Int): Pair<Individual, Individual> {
        //println("CROSSOVER PMX:  -----> ")

        val offSpringFirst = MutableList(MainTest.n) {0}
        val offSpringSecond = MutableList(MainTest.n) {0}
        val hashMapFirst = hashMapOf<Int, Int>()
        val hashMapSecond = hashMapOf<Int, Int>()

        for (i in px1 until px2) {
            offSpringFirst[i] = ind2.list[i]
            offSpringSecond[i] = ind1.list[i]
            /**
             * Armar las mapeos para saber correspondencia dentro de los puntos de corte
             */
            hashMapSecond[ind1.list[i]] = ind2.list[i]
            hashMapFirst[ind2.list[i]] = ind1.list[i]
        }

        for(i in 0 until px1) {
            var value = ind1.list[i]
            while (hashMapFirst[value] != null) {
                hashMapFirst[value]?.let {
                    value = it
                }
            }
            offSpringFirst[i] = value
        }

        for (i in px2 until MainTest.n) {
            var value = ind1.list[i]
            while (hashMapFirst[value] != null) {
                hashMapFirst[value]?.let {
                    value = it
                }
            }
            offSpringFirst[i] = value
        }

        for(i in 0 until px1) {
            var value = ind2.list[i]
            while (hashMapSecond[value] != null) {
                hashMapSecond[value]?.let {
                    value = it
                }
            }
            offSpringSecond[i] = value
        }

        for (i in px2 until MainTest.n) {
            var value = ind2.list[i]
            while (hashMapSecond[value] != null) {
                hashMapSecond[value]?.let {
                    value = it
                }
            }
            offSpringSecond[i] = value
        }

        val ind3 = Individual()
        ind3.list = offSpringFirst
        ind3.distance = Population().calculateDistance(offSpringFirst).toFloat()

        val ind4 = Individual()
        ind4.list = offSpringSecond
        ind4.distance = Population().calculateDistance(offSpringSecond).toFloat()

        return Pair(ind3, ind4)
    }

    private fun crossoverOX(ind1:Individual, ind2:Individual, px1: Int, px2: Int): Pair<Individual, Individual> {
       // println("CROSSOVER OX:  -----> ")
        val subStringFirst = arrayListOf<Int>()
        val subStringSecond = arrayListOf<Int>()
        val offSpringFirst = MutableList(MainTest.n) {0}
        val offSpringSecond = MutableList(MainTest.n) {0}
        var count = px2 - px1
        var refAdd = px2
        var refInd = px2

        for (i in px1 until px2) {
            subStringFirst.add(ind1.list[i])
            subStringSecond.add(ind2.list[i])
            offSpringFirst[i] = ind1.list[i]
            offSpringSecond[i] = ind2.list[i]
        }

        while (count < MainTest.n) {
            if (!subStringFirst.contains(ind2.list[refInd])) {
                offSpringFirst[refAdd] = ind2.list[refInd]
                refAdd++
                count++
            }
            refInd++

            // Resear indice al comienzo del cromosoma
            if (refAdd == MainTest.n) {
                refAdd = 0
            }
            if (refInd == MainTest.n) {
                refInd = 0
            }
        }

        refAdd = px2
        refInd = px2
        count = px2 - px1
        while (count < MainTest.n) {
            if (!subStringSecond.contains(ind1.list[refInd])) {
                offSpringSecond[refAdd] = ind1.list[refInd]
                refAdd++
                count++
            }
            refInd++

            // Resear indice al comienzo del cromosoma
            if (refAdd == MainTest.n) {
                refAdd = 0
            }
            if (refInd == MainTest.n) {
                refInd = 0
            }
        }

        val ind3 = Individual()
        ind3.list = offSpringFirst
        ind3.distance = Population().calculateDistance(offSpringFirst).toFloat()

        val ind4 = Individual()
        ind4.list = offSpringSecond
        ind4.distance = Population().calculateDistance(offSpringSecond).toFloat()

        return Pair(ind3, ind4)
    }

    private fun mutation(ind: Individual, mutationRate: Double = 0.1): Individual{
        for (i in 0 until ind.list.size) {
            val random = Math.random()
            if (random < mutationRate) {
                val swapWith = (random * ind.list.size).toInt()
                val city1 = ind.list[i]
                val city2 = ind.list[swapWith]

                ind.list[i] = city2
                ind.list[swapWith] = city1
            }
        }
        return ind
    }
}


fun getPXS(): Pair<Int, Int> {
    var px1 = Random.nextInt(MainTest.n-1)
    var px2 = Random.nextInt(MainTest.n-1)

    while (px1 == px2 || px1 == (MainTest.n-1) || px1 == 0
        || px2 == (MainTest.n-1) || px2 == 0) {
        px1 = Random.nextInt(MainTest.n-1)
        px2 = Random.nextInt(MainTest.n-1)
    }

    val extraPx = px1
    if (px1 > px2) {
        px1 = px2
        px2 = extraPx
    }
    return Pair(px1, px2)
}

fun List<Int>. print() {
    this.forEach {
        print(" $it")
    }
}

class Model(val individual: Individual, val crossover: String): Comparable<Model> {
    override fun compareTo(other: Model): Int {
        return this.individual.compareTo(other.individual)
    }
}

