/*
 * MIT License
 *
 * Copyright (c) 2021 Andrew Krepps
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

package com.askrepps.advent2021.day19

import com.askrepps.advent2021.util.getInputLines
import java.io.File
import kotlin.math.abs

private const val OVERLAP_THRESHOLD = 12

private val MEASUREMENT_ROTATIONS = listOf<(GraphCoordinates) -> GraphCoordinates>(
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

data class ScannerReport(val id: Int, val beaconMeasurements: Set<GraphCoordinates>) {
    fun transformMeasurements(state: ScannerState) =
        beaconMeasurements.map { measurement -> state.rotation(measurement) + state.position }.toSet()
}

data class ScannerState(val position: GraphCoordinates, val rotation: (GraphCoordinates) -> GraphCoordinates)

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

fun checkOverlap(baseScanner: ScannerReport, baseScannerState: ScannerState, newScanner: ScannerReport): ScannerState? {
    val baseMeasurements = baseScanner.transformMeasurements(baseScannerState)
    for (rotation in MEASUREMENT_ROTATIONS) {
        val rotatedMeasurements = newScanner.beaconMeasurements.map(rotation)
        for (newMeasurement in rotatedMeasurements) {
            for (baseMeasurement in baseMeasurements) {
                val position = baseMeasurement - newMeasurement
                val candidateState = ScannerState(position, rotation)
                val overlap = baseMeasurements.intersect(newScanner.transformMeasurements(candidateState))
                if (overlap.size >= OVERLAP_THRESHOLD) {
                    return candidateState
                }
            }
        }
    }
    return null
}

fun findScannerStates(scannerReports: Map<Int, ScannerReport>): Map<Int, ScannerState> {
    val scannerStates = mutableMapOf(0 to ScannerState(GraphCoordinates(0, 0, 0), MEASUREMENT_ROTATIONS[0]))
    val scannersToMatch = scannerReports.keys.associateWith { s1 ->
        scannerReports.keys.filter { s2 -> s1 != s2 }.toMutableList()
    }.toMutableMap()
    scannersToMatch.remove(0)
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
                val baseScanner = scannerReports[baseScannerId]
                    ?: throw IllegalStateException("No report for scanner $baseScannerId")
                val newState = checkOverlap(baseScanner, baseScannerState, newScanner)
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

fun getPart1Answer(scannerReports: Map<Int, ScannerReport>, scannerStates: Map<Int, ScannerState>) =
    scannerReports.flatMap { (id, scanner) ->
        val state = scannerStates[id]
            ?: throw IllegalStateException("Could not determine state for scanner $id")
        scanner.transformMeasurements(state)
    }.toSet().size

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

    println("The answer to part 1 is ${getPart1Answer(scannerReports, scannerStates)}")
    println("The answer to part 2 is ${getPart2Answer(scannerStates)}")
}
