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

package com.askrepps.advent.advent2022.day12

import com.askrepps.advent.util.getInputLines
import java.io.File
import java.util.PriorityQueue

enum class Direction(val deltaRow: Int, val deltaColumn: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}

data class GridCoordinates(val row: Int, val column: Int)

data class GridSearchPoint(val coordinates: GridCoordinates, val height: Char, val currentDistance: Int)

data class InputData(val heightMap: List<List<Char>>, val start: GridCoordinates, val end: GridCoordinates)

fun List<String>.toInputData(): InputData {
    var start: GridCoordinates? = null
    var end: GridCoordinates? = null
    val heights = mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, cell ->
            when (cell) {
                'S' -> {
                    check(start == null) { "Multiple starts found" }
                    start = GridCoordinates(rowIndex, columnIndex)
                    'a'
                }
                'E' -> {
                    check(end == null) { "Multiple ends found" }
                    end = GridCoordinates(rowIndex, columnIndex)
                    'z'
                }
                else -> cell
            }
        }
    }
    return InputData(
        heights,
        start ?: error("No start cell found"),
        end ?: error("No end cell found")
    )
}

fun findDistancesToEnd(heightMap: List<List<Char>>, end: GridCoordinates): List<List<Int>> {
    val totalDistances = heightMap.map { row -> MutableList(row.size) { Int.MAX_VALUE } }
    totalDistances[end.row][end.column] = 0

    val searchQueue = PriorityQueue<GridSearchPoint>(compareBy { it.currentDistance })
    searchQueue.add(GridSearchPoint(end, heightMap[end.row][end.column], 0))
    while (searchQueue.isNotEmpty()) {
        val (currentCoordinates, currentHeight, currentDistance) = searchQueue.remove()
        for (direction in Direction.values()) {
            val neighborRow = currentCoordinates.row + direction.deltaRow
            val neighborColumn = currentCoordinates.column + direction.deltaColumn
            val neighborDistance = currentDistance + 1
            heightMap.getOrNull(neighborRow)?.getOrNull(neighborColumn)?.let { neighborHeight ->
                val currentNeighborDistance = totalDistances[neighborRow][neighborColumn]
                if (neighborHeight >= currentHeight - 1 && neighborDistance < currentNeighborDistance) {
                    totalDistances[neighborRow][neighborColumn] = neighborDistance
                    val neighborCoordinates = GridCoordinates(neighborRow, neighborColumn)
                    searchQueue.add(GridSearchPoint(neighborCoordinates, neighborHeight, neighborDistance))
                }
            }
        }
    }

    return totalDistances
}

fun getPart1Answer(distances: List<List<Int>>, start: GridCoordinates) =
    distances[start.row][start.column]

fun getPart2Answer(heightMap: List<List<Char>>, distances: List<List<Int>>) =
    heightMap.flatMapIndexed { rowIndex, row ->
        row.mapIndexedNotNull { columnIndex, height ->
            if (height == 'a') {
                distances[rowIndex][columnIndex]
            } else {
                null
            }
        }
    }.min()

fun main() {
    val (heightMap, start, end) = File("src/main/resources/2022/day12.txt")
        .getInputLines().toInputData()
    val distances = findDistancesToEnd(heightMap, end)

    println("The answer to part 1 is ${getPart1Answer(distances, start)}")
    println("The answer to part 2 is ${getPart2Answer(heightMap, distances)}")
}
