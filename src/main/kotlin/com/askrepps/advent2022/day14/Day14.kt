/*
 * MIT License
 *
 * Copyright (c) 2022 Andrew Krepps
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

package com.askrepps.advent2022.day14

import com.askrepps.advent2022.util.getInputLines
import java.io.File

enum class Direction(val deltaX: Int, val deltaY: Int) {
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    DOWN_RIGHT(1, 1)
}

data class GridCoordinates(val x: Int, val y: Int) {
    fun move(direction: Direction) =
        GridCoordinates(x + direction.deltaX, y + direction.deltaY)
}

fun String.toCoordinates(): GridCoordinates {
    val (x, y) = split(",")
    return GridCoordinates(x.toInt(), y.toInt())
}

fun List<String>.toRockPoints(): Set<GridCoordinates> {
    val points = mutableSetOf<GridCoordinates>()
    forEach { line ->
        for ((segmentStart, segmentEnd) in line.split(" -> ").zipWithNext()) {
            val start = segmentStart.toCoordinates()
            val end = segmentEnd.toCoordinates()
            if (start.x == end.x) {
                for (y in minOf(start.y, end.y)..maxOf(start.y, end.y)) {
                    points.add(GridCoordinates(start.x, y))
                }
            } else if (start.y == end.y) {
                for (x in minOf(start.x, end.x)..maxOf(start.x, end.x)) {
                    points.add(GridCoordinates(x, start.y))
                }
            } else {
                error("Input segment from $segmentStart -> $segmentEnd is not a straight line")
            }
        }
    }
    return points
}

fun findSandSimulationSteadyState(
    rockPoints: Set<GridCoordinates>,
    sandSource: GridCoordinates,
    hasFloor: Boolean
): Int {
    val settledSand = mutableSetOf<GridCoordinates>()
    val abyssDepth = rockPoints.maxOf { it.y }
    val floorDepth = abyssDepth + 2
    var simulating = true
    while (simulating) {
        simulating = false
        var currentSand = sandSource
        var falling = true
        while (falling) {
            falling = false
            for (direction in Direction.values()) {
                val nextSandPoint = currentSand.move(direction)
                if (nextSandPoint !in rockPoints
                    && nextSandPoint !in settledSand
                    && !(hasFloor && nextSandPoint.y >= floorDepth)
                ) {
                    currentSand = nextSandPoint
                    falling = true
                    break
                }
            }
            if (!hasFloor && currentSand.y >= abyssDepth) {
                simulating = false
                break
            } else if (!falling) {
                settledSand.add(currentSand)
                simulating = !(hasFloor && currentSand == sandSource)
            }
        }
    }
    return settledSand.size
}

fun getPart1Answer(rockPoints: Set<GridCoordinates>) =
    findSandSimulationSteadyState(rockPoints, sandSource = GridCoordinates(500, 0), hasFloor = false)

fun getPart2Answer(rockPoints: Set<GridCoordinates>) =
    findSandSimulationSteadyState(rockPoints, sandSource = GridCoordinates(500, 0), hasFloor = true)

fun main() {
    val rockPoints = File("src/main/resources/2022/day14.txt")
        .getInputLines().toRockPoints()

    println("The answer to part 1 is ${getPart1Answer(rockPoints)}")
    println("The answer to part 2 is ${getPart2Answer(rockPoints)}")
}
