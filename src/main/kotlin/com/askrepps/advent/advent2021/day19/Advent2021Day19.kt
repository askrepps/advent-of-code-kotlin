/*
 * MIT License
 *
 * Copyright (c) 2021-2023 Andrew Krepps
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

package com.askrepps.advent.advent2021.day19

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.abs

typealias Rotation = (GraphCoordinates) -> GraphCoordinates

private const val OVERLAP_THRESHOLD = 12

private val MEASUREMENT_ROTATIONS = listOf<Rotation>(
    // scanner +z is facing world +z
    { (x, y, z) -> GraphCoordinates( x,  y,  z) },
    { (x, y, z) -> GraphCoordinates( y, -x,  z) },
    { (x, y, z) -> GraphCoordinates(-x, -y,  z) },
    { (x, y, z) -> GraphCoordinates(-y,  x,  z) },

    // scanner +z is facing world -z
    { (x, y, z) -> GraphCoordinates( x, -y, -z) },
    { (x, y, z) -> GraphCoordinates( y,  x, -z) },
    { (x, y, z) -> GraphCoordinates(-x,  y, -z) },
    { (x, y, z) -> GraphCoordinates(-y, -x, -z) },

    // scanner +z is facing world +x
    { (x, y, z) -> GraphCoordinates( y,  z,  x) },
    { (x, y, z) -> GraphCoordinates(-z,  y,  x) },
    { (x, y, z) -> GraphCoordinates(-y, -z,  x) },
    { (x, y, z) -> GraphCoordinates( z, -y,  x) },

    // scanner +z is facing world -x
    { (x, y, z) -> GraphCoordinates( y, -z, -x) },
    { (x, y, z) -> GraphCoordinates(-z, -y, -x) },
    { (x, y, z) -> GraphCoordinates(-y,  z, -x) },
    { (x, y, z) -> GraphCoordinates( z,  y, -x) },

    // scanner +z is facing world +y
    { (x, y, z) -> GraphCoordinates( x, -z,  y) },
    { (x, y, z) -> GraphCoordinates(-z, -x,  y) },
    { (x, y, z) -> GraphCoordinates(-x,  z,  y) },
    { (x, y, z) -> GraphCoordinates( z,  x,  y) },

    // scanner +z is facing world -y
    { (x, y, z) -> GraphCoordinates( x,  z, -y) },
    { (x, y, z) -> GraphCoordinates(-z,  x, -y) },
    { (x, y, z) -> GraphCoordinates(-x, -z, -y) },
    { (x, y, z) -> GraphCoordinates( z, -x, -y) }
)

data class GraphCoordinates(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: GraphCoordinates) =
        GraphCoordinates(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: GraphCoordinates) =
        GraphCoordinates(x - other.x, y - other.y, z - other.z)

    fun manhattanDistanceTo(other: GraphCoordinates) =
        abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
}

data class ScannerReport(val id: Int, val beaconMeasurements: Set<GraphCoordinates>)

data class ScannerState(
    val position: GraphCoordinates,
    val rotation: Rotation,
    val transformedMeasurements: Set<GraphCoordinates>
)

fun List<String>.toScannerReports(): Map<Int, ScannerReport> {
    val reports = mutableMapOf<Int, ScannerReport>()
    var id: Int = -1
    val measurements = mutableSetOf<GraphCoordinates>()
    for (line in this) {
        if (line.startsWith("---")) {
            if (measurements.isNotEmpty()) {
                reports[id] = ScannerReport(id, measurements.toSet())
                measurements.clear()
            }
            id = line.substring(line.indexOfFirst { it.isDigit() }, line.indexOfLast { it.isWhitespace() }).toInt()
        } else {
            val (x, y, z) = line.split(",").map { it.toInt() }
            measurements.add(GraphCoordinates(x, y, z))
        }
    }
    reports[id] = ScannerReport(id, measurements.toSet())
    return reports
}

fun checkOverlap(
    baseScannerState: ScannerState,
    newScanner: ScannerReport,
    rotationCache: MutableMap<Pair<Int, Rotation>, Set<GraphCoordinates>>
): ScannerState? {
    val baseMeasurements = baseScannerState.transformedMeasurements
    for (rotation in MEASUREMENT_ROTATIONS) {
        val rotatedMeasurements = rotationCache.computeIfAbsent(Pair(newScanner.id, rotation)) {
            newScanner.beaconMeasurements.map(rotation).toSet()
        }
        for (newMeasurement in rotatedMeasurements) {
            for (baseMeasurement in baseMeasurements) {
                val position = baseMeasurement - newMeasurement
                val transformedMeasurements = rotatedMeasurements.map { it + position }.toSet()
                val overlap = baseMeasurements.intersect(transformedMeasurements)
                if (overlap.size >= OVERLAP_THRESHOLD) {
                    return ScannerState(position, rotation, transformedMeasurements)
                }
            }
        }
    }
    return null
}

fun findScannerStates(scannerReports: Map<Int, ScannerReport>): Map<Int, ScannerState> {
    val state0 = ScannerState(
        GraphCoordinates(0, 0, 0),
        MEASUREMENT_ROTATIONS[0],
        scannerReports[0]?.beaconMeasurements.orEmpty()
    )
    val scannerStates = mutableMapOf(0 to state0)
    val scannersToMatch = scannerReports.keys.associateWith { s1 ->
        scannerReports.keys.filter { s2 -> s1 != s2 }.toMutableList()
    }.toMutableMap()
    scannersToMatch.remove(0)
    val rotationCache = mutableMapOf<Pair<Int, Rotation>, Set<GraphCoordinates>>()
    while (scannersToMatch.isNotEmpty()) {
        for ((newId, newScanner) in scannerReports) {
            if (newId !in scannersToMatch.keys) {
                continue
            }
            if (scannersToMatch[newId].isNullOrEmpty()) {
                throw IllegalStateException("Could not determine state of scanner $newId")
            }
            for ((baseScannerId, baseScannerState) in scannerStates) {
                if (baseScannerId !in scannersToMatch[newId].orEmpty()) {
                    continue
                }
                val newState = checkOverlap(baseScannerState, newScanner, rotationCache)
                if (newState != null) {
                    scannerStates[newId] = newState
                    scannersToMatch.remove(newId)
                    break
                } else {
                    scannersToMatch[newId]?.remove(baseScannerId)
                }
            }
        }
    }
    return scannerStates
}

fun getPart1Answer(scannerStates: Map<Int, ScannerState>) =
    scannerStates.values.flatMap { state -> state.transformedMeasurements }.toSet().size

fun getPart2Answer(scannerStates: Map<Int, ScannerState>): Int =
    scannerStates.maxOf { (id1, state1) ->
        scannerStates.maxOf { (id2, state2) ->
            if (id1 == id2) {
                Int.MIN_VALUE
            } else {
                state1.position.manhattanDistanceTo(state2.position)
            }
        }
    }

fun main() {
    val scannerReports = File("src/main/resources/day19.txt")
        .getInputLines().toScannerReports()
    val scannerStates = findScannerStates(scannerReports)

    println("The answer to part 1 is ${getPart1Answer(scannerStates)}")
    println("The answer to part 2 is ${getPart2Answer(scannerStates)}")
}
