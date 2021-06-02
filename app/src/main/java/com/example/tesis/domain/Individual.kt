package com.example.tesis.domain

data class Individual (
    var list: MutableList<Int> = mutableListOf(),
    var distance: Double = 0.0
) : Comparable<Individual> {
    override fun compareTo(other: Individual): Int {
        return distance.compareTo(other.distance)
    }
}