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

package com.askrepps.advent.advent2021.day22

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.max

enum class Operation { On, Off }

val IntRange.length: Long
    get() = max(0, last.toLong() - first.toLong() + 1L)

fun IntRange.fullyContains(other: IntRange) =
    other.first >= first && other.last <= last

fun IntRange.intersects(other: IntRange) =
    first <= other.last && last >= other.first

fun IntRange.partitionOverlapWith(other: IntRange): Triple<IntRange, IntRange, IntRange> {
    check(intersects(other)) { "Cannot partition ranges that do not intersect" }
    val points = listOf(first, other.first, last, other.last).sorted()
    val beforeOverlap = points[0] until points[1]
    val duringOverlap = points[1]..points[2]
    val afterOverlap = (points[2] + 1)..points[3]
    return Triple(beforeOverlap, duringOverlap, afterOverlap)
}

fun IntRange.isBefore(other: IntRange) =
    first < other.first

fun IntRange.isAfter(other: IntRange) =
    last > other.last

data class CuboidRegion(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange,
    val operation: Operation = Operation.On
) {
    fun subtractFrom(other: CuboidRegion): Set<CuboidRegion> {
        if (fullyContains(other)) {
            return emptySet()
        } else if (!intersects(other)) {
            return setOf(other)
        }

        val results = mutableSetOf<CuboidRegion>()

        val (beforeOverlapX, duringOverlapX, afterOverlapX) = xRange.partitionOverlapWith(other.xRange)
        if (other.xRange.isBefore(xRange)) {
            check(!beforeOverlapX.isEmpty()) { "X region before overlap must exist" }
            results.add(CuboidRegion(beforeOverlapX, other.yRange, other.zRange))
        }
        if (other.xRange.isAfter(xRange)) {
            check(!afterOverlapX.isEmpty()) { "X region after overlap must exist" }
            results.add(CuboidRegion(afterOverlapX, other.yRange, other.zRange))
        }

        val (beforeOverlapY, duringOverlapY, afterOverlapY) = yRange.partitionOverlapWith(other.yRange)
        if (other.yRange.isBefore(yRange)) {
            check(!beforeOverlapY.isEmpty()) { "Y region before overlap must exist" }
            results.add(CuboidRegion(duringOverlapX, beforeOverlapY, other.zRange))
        }
        if (other.yRange.isAfter(yRange)) {
            check(!afterOverlapY.isEmpty()) { "Y region after overlap must exist" }
            results.add(CuboidRegion(duringOverlapX, afterOverlapY, other.zRange))
        }

        val (beforeOverlapZ, _, afterOverlapZ) = zRange.partitionOverlapWith(other.zRange)
        if (other.zRange.isBefore(zRange)) {
            check(!beforeOverlapZ.isEmpty()) { "Z region before overlap must exist" }
            results.add(CuboidRegion(duringOverlapX, duringOverlapY, beforeOverlapZ))
        }
        if (other.zRange.isAfter(zRange)) {
            check(!afterOverlapZ.isEmpty()) { "Z region after overlap must exist" }
            results.add(CuboidRegion(duringOverlapX, duringOverlapY, afterOverlapZ))
        }

        return results
    }

    val volume: Long
        get() = xRange.length * yRange.length * zRange.length

    fun fullyContains(other: CuboidRegion) =
        xRange.fullyContains(other.xRange) && yRange.fullyContains(other.yRange) && zRange.fullyContains(other.zRange)

    private fun intersects(other: CuboidRegion) =
        xRange.intersects(other.xRange) && yRange.intersects(other.yRange) && zRange.intersects(other.zRange)
}

fun String.toRange(): IntRange {
    val (minValue, maxValue) = split("=").last().split("..").map { it.toInt() }
    return minValue..maxValue
}

fun String.toCuboidRegion(): CuboidRegion {
    val (opString, regionDefinition) = split(" ")
    val operation = when (opString) {
        "on" -> Operation.On
        "off" -> Operation.Off
        else -> throw IllegalArgumentException("Unrecognized operation: $opString")
    }
    val (xRange, yRange, zRange) = regionDefinition.split(",").map { it.toRange() }
    return CuboidRegion(xRange, yRange, zRange, operation)
}

fun List<CuboidRegion>.filterToInitializationRegion(lowerLimit: Int, upperLimit: Int): List<CuboidRegion> {
    val filterRange = lowerLimit..upperLimit
    val filterRegion = CuboidRegion(filterRange, filterRange, filterRange)
    return filter { filterRegion.fullyContains(it) }
}

fun countActiveCubes(regions: List<CuboidRegion>): Long {
    val finalRegions = mutableSetOf<CuboidRegion>()
    for (inputRegion in regions) {
        when (inputRegion.operation) {
            Operation.On -> {
                val uniqueNewRegions = mutableSetOf(inputRegion)
                for (existingRegion in finalRegions) {
                    val choppingBlock = uniqueNewRegions.toSet()
                    uniqueNewRegions.clear()
                    for (newRegion in choppingBlock) {
                        uniqueNewRegions.addAll(existingRegion.subtractFrom(newRegion))
                    }
                }
                finalRegions.addAll(uniqueNewRegions)
            }
            Operation.Off ->  {
                val choppingBlock = finalRegions.toSet()
                finalRegions.clear()
                for (existingRegion in choppingBlock) {
                    finalRegions.addAll(inputRegion.subtractFrom(existingRegion))
                }
            }
        }
    }
    return finalRegions.sumOf { it.volume }
}

fun getPart1Answer(regions: List<CuboidRegion>) =
    countActiveCubes(regions.filterToInitializationRegion(lowerLimit = -50, upperLimit = 50))

fun getPart2Answer(regions: List<CuboidRegion>) =
    countActiveCubes(regions)

fun main() {
    val regions = File("src/main/resources/day22.txt")
        .getInputLines().map { it.toCuboidRegion() }

    println("The answer to part 1 is ${getPart1Answer(regions)}")
    println("The answer to part 2 is ${getPart2Answer(regions)}")
}
