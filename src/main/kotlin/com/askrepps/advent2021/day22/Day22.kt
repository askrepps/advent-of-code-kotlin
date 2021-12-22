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

package com.askrepps.advent2021.day22

import com.askrepps.advent2021.util.getInputLines
import java.io.File

enum class Operation { On, Off }

data class GraphCoordinates(val x: Int, val y: Int, val z: Int)

data class CubeRegion(val operation: Operation, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun contains(cube: GraphCoordinates) =
        cube.x in xRange && cube.y in yRange && cube.z in zRange

    fun createCubes() =
        xRange.flatMap { x ->
            yRange.flatMap { y ->
                zRange.map { z ->
                    GraphCoordinates(x, y, z)
                }
            }
        }.toSet()
}

fun String.toRange(): IntRange {
    val (minValue, maxValue) = split("=").last().split("..").map { it.toInt() }
    return minValue..maxValue
}

fun String.toCubeRegion(): CubeRegion {
    val (opString, regionDefinition) = split(" ")
    val operation = when (opString) {
        "on" -> Operation.On
        "off" -> Operation.Off
        else -> throw IllegalArgumentException("Unrecognized operation: $opString")
    }
    val (xRange, yRange, zRange) = regionDefinition.split(",").map { it.toRange() }
    return CubeRegion(operation, xRange, yRange, zRange)
}

fun countActiveCubes(regions: List<CubeRegion>, lowerLimit: Int? = null, upperLimit: Int? = null): Int {
    val regionsOfInterest =
        if (lowerLimit != null && upperLimit != null) {
            regions.filter {
                it.xRange.first >= lowerLimit && it.xRange.last <= upperLimit
                        && it.yRange.first >= lowerLimit && it.yRange.last <= upperLimit
                        && it.zRange.first >= lowerLimit && it.zRange.last <= upperLimit
            }
        } else {
            regions
        }

    val activeCubes = mutableSetOf<GraphCoordinates>()
    for (region in regionsOfInterest) {
        when (region.operation) {
            Operation.On -> activeCubes.addAll(region.createCubes())
            Operation.Off -> activeCubes.removeIf { region.contains(it) }
        }
    }
    return activeCubes.size
}

fun getPart1Answer(regions: List<CubeRegion>) =
    countActiveCubes(regions, lowerLimit = -50, upperLimit = 50)

fun getPart2Answer(regions: List<CubeRegion>) =
    countActiveCubes(regions)

fun main() {
    val regions = File("src/main/resources/day22.txt")
        .getInputLines().map { it.toCubeRegion() }

    println("The answer to part 1 is ${getPart1Answer(regions)}")
//    println("The answer to part 2 is ${getPart2Answer(regions)}")
}
