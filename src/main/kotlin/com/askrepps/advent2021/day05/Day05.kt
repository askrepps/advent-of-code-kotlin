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

data class GraphCoordinates(val x: Int, val y: Int)

enum class Orientation { Horizontal, Vertical, Diagonal }

data class VentLine(val start: GraphCoordinates, val end: GraphCoordinates) {
    val orientation: Orientation
        get() = when {
            start.x == end.x -> Orientation.Vertical
            start.y == end.y -> Orientation.Horizontal
            else -> Orientation.Diagonal
        }

    val points: List<GraphCoordinates> by lazy { generatePoints() }

    private fun generatePoints(): List<GraphCoordinates> {
        val xDistance = abs(start.x - end.x)
        val yDistance = abs(start.y - end.y)
        check (xDistance == 0 || yDistance == 0 || xDistance == yDistance) {
            "Vent lines must be horizontal, vertical, or diagonal at 45 degrees"
        }

        val length = max(xDistance, yDistance) + 1
        val deltaX = getSlope(start.x, end.x)
        val deltaY = getSlope(start.y, end.y)

        return (0 until length).map {
            GraphCoordinates(start.x + it*deltaX, start.y + it*deltaY)
        }
    }

    private fun getSlope(startValue: Int, endValue: Int) =
        when {
            startValue < endValue -> 1
            startValue > endValue -> -1
            else -> 0
        }
}

fun String.toCoordinates() =
    split(",").let { GraphCoordinates(it[0].toInt(), it[1].toInt()) }

fun String.toVentLine() =
    split(" -> ").let { VentLine(it[0].toCoordinates(), it[1].toCoordinates()) }

fun List<VentLine>.countOverlappingPoints(includeDiagonals: Boolean) =
    filter { includeDiagonals || it.orientation != Orientation.Diagonal }
        .flatMap { it.points }
        .groupBy { it }
        .count { it.value.size >= 2 }

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
