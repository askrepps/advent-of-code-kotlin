/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Andrew Krepps
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.askrepps.advent.advent2022.day15

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.abs

data class GridCoordinates(val x: Int, val y: Int) {
    fun manhattanDistanceTo(other: GridCoordinates) =
        abs(x - other.x) + abs(y - other.y)
}

data class SensorReport(val sensor: GridCoordinates, val beacon: GridCoordinates) {
    val closestBeaconDistance = sensor.manhattanDistanceTo(beacon)
}

fun String.toCoordinates(): GridCoordinates {
    val (xPart, yPart) = split(", ")
    return GridCoordinates(
        xPart.substringAfter("=").toInt(),
        yPart.substringAfter("=").toInt()
    )
}

fun String.toSensorReport(): SensorReport {
    val (sensorPart, beaconPart) = split(": ")
    return SensorReport(
        sensorPart.substringAfter("at ").toCoordinates(),
        beaconPart.substringAfter("at ").toCoordinates()
    )
}

fun countBeaconExclusionPointsInLine(sensorReports: List<SensorReport>, searchY: Int): Int {
    val maxDistanceMagnitude = sensorReports.maxOf { it.closestBeaconDistance }
    val leftmostSearchX = sensorReports.minOf { it.sensor.x } - maxDistanceMagnitude
    val rightMostSearchX = sensorReports.maxOf { it.sensor.x } + maxDistanceMagnitude
    val searchXRange = leftmostSearchX..rightMostSearchX
    return searchXRange.count { searchX ->
        val testPoint = GridCoordinates(searchX, searchY)
        cannotContainNewBeacon(testPoint, sensorReports)
    }
}

fun cannotContainNewBeacon(point: GridCoordinates, sensorReports: List<SensorReport>) =
    sensorReports.any {
        point != it.beacon && point.manhattanDistanceTo(it.sensor) <= it.closestBeaconDistance
    }

fun getSensorPerimeterPoints(sensorReport: SensorReport): Set<GridCoordinates> {
    // find points that are 1 distance farther out than the distance to the closest beacon
    val perimeterPoints = mutableSetOf<GridCoordinates>()
    val (sensorX, sensorY) = sensorReport.sensor
    val minSearchX = sensorX - sensorReport.closestBeaconDistance - 1
    val maxSearchX = sensorX + sensorReport.closestBeaconDistance + 1
    for (x in minSearchX..maxSearchX) {
        val offset = abs(x - sensorReport.sensor.x) - sensorReport.closestBeaconDistance - 1
        perimeterPoints.add(GridCoordinates(x, sensorY + offset))
        perimeterPoints.add(GridCoordinates(x, sensorY - offset))
    }
    return perimeterPoints
}

fun getPart1Answer(sensorReports: List<SensorReport>, searchY: Int) =
    countBeaconExclusionPointsInLine(sensorReports, searchY)

fun getPart2Answer(sensorReports: List<SensorReport>, searchRange: IntRange): Long {
    // if we can assume there is only one valid position, it must lie on one of the diamonds
    // that form the perimeter just outside each sensor's distance to its closest beacon
    val beacons = sensorReports.map { it.beacon }.toSet()
    for (sensor in sensorReports) {
        for (point in getSensorPerimeterPoints(sensor)) {
            if (point.x in searchRange && point.y in searchRange
                && point !in beacons && !cannotContainNewBeacon(point, sensorReports)
            ) {
                return point.x * 4000000L + point.y
            }
        }
    }
    error("Beacon position not found")
}

fun main() {
    val sensorReports = File("src/main/resources/2022/day15.txt")
        .getInputLines().map { it.toSensorReport() }

    println("The answer to part 1 is ${getPart1Answer(sensorReports, searchY = 2000000)}")
    println("The answer to part 2 is ${getPart2Answer(sensorReports, searchRange = 0..4000000)}")
}
