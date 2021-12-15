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

package com.askrepps.advent2021.day15

import com.askrepps.advent2021.util.getInputLines
import java.io.File
import java.util.PriorityQueue

private val NEIGHBOR_DIRECTIONS = listOf(
    Pair(-1, 0),
    Pair(1, 0),
    Pair(0, -1),
    Pair(0, 1)
)

data class GridCoordinates(val row: Int, val col: Int)

fun String.toRiskValues() =
    map { it.code - '0'.code }

fun findMinimumPathRisk(individualRiskMap: List<List<Int>>): Int {
    val totalPathRiskMap = individualRiskMap.map { row -> MutableList(row.size) { Int.MAX_VALUE } }
    totalPathRiskMap[0][0] = 0

    val pointQueue = PriorityQueue<Pair<GridCoordinates, Int>>(compareBy { it.second })
    pointQueue.add(GridCoordinates(0, 0) to 0)
    while (pointQueue.isNotEmpty()) {
        val (currentPoint, _) = pointQueue.remove()
        for ((deltaRow, deltaCol) in NEIGHBOR_DIRECTIONS) {
            val neighborPoint = GridCoordinates(currentPoint.row + deltaRow, currentPoint.col + deltaCol)
            individualRiskMap.getOrNull(neighborPoint.row)?.getOrNull(neighborPoint.col)?.let { neighborRisk ->
                val possibleRisk = totalPathRiskMap[currentPoint.row][currentPoint.col] + neighborRisk
                if (possibleRisk < totalPathRiskMap[neighborPoint.row][neighborPoint.col]) {
                    totalPathRiskMap[neighborPoint.row][neighborPoint.col] = possibleRisk
                    pointQueue.add(neighborPoint to possibleRisk)
                }
            }
        }
    }

    return totalPathRiskMap.last().last()
}

fun List<List<Int>>.expandMapBy(expansionFactor: Int) =
    List(size * expansionFactor) { newRowIndex ->
        val oldRowIndex = newRowIndex % size
        val oldRow = get(oldRowIndex)
        val verticalTileDistance = newRowIndex / size

        List(oldRow.size * expansionFactor) { newColIndex ->
            val oldColIndex = newColIndex % oldRow.size
            val horizontalTileDistance = newColIndex / oldRow.size
            val totalTileDistance = verticalTileDistance + horizontalTileDistance
            ((oldRow[oldColIndex] + totalTileDistance - 1) % 9) + 1
        }
    }

fun getPart1Answer(riskMap: List<List<Int>>) =
    findMinimumPathRisk(riskMap)

fun getPart2Answer(riskMap: List<List<Int>>) =
    findMinimumPathRisk(riskMap.expandMapBy(5))

fun main() {
    val riskMap = File("src/main/resources/day15.txt")
        .getInputLines().map { it.toRiskValues() }

    println("The answer to part 1 is ${getPart1Answer(riskMap)}")
    println("The answer to part 2 is ${getPart2Answer(riskMap)}")
}
