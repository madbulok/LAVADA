package com.uzlov.dating.lavada.domain.logic

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

fun distance(lat11: Double, lon1: Double, lat22: Double, lon2: Double): Double {
    var lat1 = lat11
    var lat2 = lat22
    var dist: Double
    var theta: Double = lon1 - lon2
    lat1 = Math.toRadians(lat1)
    lat2 = Math.toRadians(lat2)
    theta = Math.toRadians(theta)
    dist = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(theta)
    dist = acos(dist)
    dist *= 6370.693486f
    return dist
}