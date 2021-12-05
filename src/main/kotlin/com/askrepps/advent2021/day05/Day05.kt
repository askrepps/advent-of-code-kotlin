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

package com.askrepps.advent2021.day05

import com.askrepps.advent2021.util.getInputLines
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class GraphCoordinates(val x: Int, val y: Int)

data class VentLine(val start: GraphCoordinates, val end: GraphCoordinates) {
    fun getPoints(includeDiagonals: Boolean): List<GraphCoordinates> =
        if (start.x == end.x) {
            (min(start.y, end.y)..max(start.y, end.y)).map { GraphCoordinates(start.x, it) }
        } else if (start.y == end.y) {
            (min(start.x, end.x)..max(start.x, end.x)).map { GraphCoordinates(it, start.y) }
        } else if (includeDiagonals) {
            val distance = abs(start.x - end.x)
            check (distance == abs(start.y - end.y)) { "Vent line must be at a 45 degree diagonal" }

            val length = distance + 1  // include endpoints
            val deltaX = if (start.x < end.x) 1 else -1
            val deltaY = if (start.y < end.y) 1 else -1

            (0 until length).map { GraphCoordinates(start.x + it*deltaX, start.y + it*deltaY) }
        } else {
            emptyList()
        }
}

fun String.toCoordinates(): GraphCoordinates {
    val tokens = split(",")
    return GraphCoordinates(tokens[0].toInt(), tokens[1].toInt())
}

fun String.toVentLine(): VentLine {
    val coordinateStrings = split(" -> ")
    return VentLine(coordinateStrings[0].toCoordinates(), coordinateStrings[1].toCoordinates())
}

fun List<VentLine>.countOverlappingPoints(includeDiagonals: Boolean): Int {
    val pointCounts = mutableMapOf<GraphCoordinates, Int>()
    val allPoints = flatMap { it.getPoints(includeDiagonals) }
    for (point in allPoints) {
        pointCounts[point] = (pointCounts[point] ?: 0) + 1
    }
    return pointCounts.values.count { it >= 2 }
}

fun getPart1Answer(ventLines: List<VentLine>) =
    ventLines.countOverlappingPoints(includeDiagonals = false)

fun getPart2Answer(ventLines: List<VentLine>) =
    ventLines.countOverlappingPoints(includeDiagonals = true)

fun main() {
    val ventLines = File("src/main/resources/day05.txt")
        .getInputLines().map { it.toVentLine() }

    println("The answer to part 1 is ${getPart1Answer(ventLines)}")
    println("The answer to part 2 is ${getPart2Answer(ventLines)}")
}
