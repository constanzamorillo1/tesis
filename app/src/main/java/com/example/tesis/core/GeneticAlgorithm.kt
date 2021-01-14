package com.example.tesis.core

import kotlin.random.Random

class GeneticAlgorithm(
    private val populationManager: PopulationManager
) {
    private val ranking = mutableListOf<Individual>()
    private lateinit var population: MutableList<Individual>
    private val routeSize = populationManager.entries.size

    fun executeOX(): Individual {
        val pxs = getPXS(routeSize)
        population = populationManager.createPopulation()
        val (firstParent, secondParent) = selection()
        println("INDIVIDUOS PADRES OX: ----->")
        println(firstParent.toString())
        println(secondParent.toString())
        var i = 0
        while (i < 50 && population.size > 0) {
            val (parent1, parent2) = selection()
            val (offStringOX1, offStringOX2) = crossoverOX(firstParent, secondParent, pxs.first, pxs.second)
            mutation(offStringOX1)
            mutation(offStringOX2)
            insertRanking(parent1, parent2, offStringOX1, offStringOX2)
            deleteParents(parent1, parent2)
            insertChildren(offStringOX1, offStringOX2)
            i++
        }
        ranking.sort()
        println("EL MEJORCITO ES: ${ranking.first()}")
        return ranking.first()
    }

    private fun insertRanking(
        firstParent: Individual, secondParent: Individual,
        firstChild: Individual, secondChild: Individual
    ) {
        val backUp = listOf(firstParent, secondParent, firstChild, secondChild)
        val insert = backUp.sorted()[0]
        ranking.add(insert)
    }

    private fun selection(): Pair<Individual, Individual> {
        var px1 = Random.nextInt(population.size-1)
        while (px1 == population.size-1)
            px1 = Random.nextInt(population.size-1)
        return Pair(population[px1], population[population.size-1])
    }

    private fun deleteParents(firstParent: Individual, secondParent: Individual) {
        population.remove(firstParent)
        population.remove(secondParent)
    }

    private fun insertChildren(firstChild: Individual, secondChild: Individual) {
        if (!population.contains(firstChild))
            population.add(firstChild)
        if (!population.contains(secondChild))
            population.add(secondChild)
        population.sort()
    }

    private fun crossoverOX(first: Individual, second:Individual, px1: Int, px2: Int): Pair<Individual, Individual> {
        val subStringFirst = arrayListOf<Int>()
        val subStringSecond = arrayListOf<Int>()
        val offSpringFirst = MutableList(routeSize) {0}
        val offSpringSecond = MutableList(routeSize) {0}
        var count = px2 - px1
        var refAdd = px2
        var refInd = px2

        for (i in px1 until px2) {
            subStringFirst.add(first.list[i])
            subStringSecond.add(second.list[i])
            offSpringFirst[i] = first.list[i]
            offSpringSecond[i] = second.list[i]
        }

        while (count < routeSize) {
            if (!subStringFirst.contains(second.list[refInd])) {
                offSpringFirst[refAdd] = second.list[refInd]
                refAdd++
                count++
            }
            refInd++

            // Resear indice al comienzo del cromosoma
            if (refAdd == routeSize) {
                refAdd = 0
            }
            if (refInd == routeSize) {
                refInd = 0
            }
        }

        refAdd = px2
        refInd = px2
        count = px2 - px1
        while (count < routeSize) {
            if (!subStringSecond.contains(first.list[refInd])) {
                offSpringSecond[refAdd] = first.list[refInd]
                refAdd++
                count++
            }
            refInd++

            // Resear indice al comienzo del cromosoma
            if (refAdd == routeSize) {
                refAdd = 0
            }
            if (refInd == routeSize) {
                refInd = 0
            }
        }

        val offStringFirstInd = Individual()
        offStringFirstInd.list = offSpringFirst
        offStringFirstInd.distance = populationManager.calculateDistance(offStringFirstInd)

        val offStringSecondInd = Individual()
        offStringSecondInd.list = offSpringSecond
        offStringSecondInd.distance = populationManager.calculateDistance(offStringSecondInd)

        return Pair(offStringFirstInd, offStringSecondInd)
    }

    private fun mutation(ind: Individual, mutationRate: Double = 0.1) {
        for (i in 0 until ind.list.size) {
            val random = Math.random()
            if (random < mutationRate) {
                val px = Random.nextInt(routeSize-1)
                val addressOrigin = ind.list[i]
                val addressDestination = ind.list[px]
                ind.list[i] = addressDestination
                ind.list[px] = addressOrigin
            }
        }
    }
}


