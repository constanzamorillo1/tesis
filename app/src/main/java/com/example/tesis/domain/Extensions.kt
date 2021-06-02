package com.example.tesis.domain

import kotlin.random.Random

fun getPXS(routeSize: Int): Pair<Int, Int> {
    var px1 = Random.nextInt(routeSize-1)
    var px2 = Random.nextInt(routeSize-1)

    while (px1 == px2 || px1 == (routeSize-1) || px1 == 0
        || px2 == (routeSize-1) || px2 == 0) {
        px1 = Random.nextInt(routeSize-1)
        px2 = Random.nextInt(routeSize-1)
    }

    val extraPx = px1
    if (px1 > px2) {
        px1 = px2
        px2 = extraPx
    }
    return Pair(px1, px2)
}