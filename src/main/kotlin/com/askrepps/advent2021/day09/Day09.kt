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

package com.askrepps.advent2021.day09

import com.askrepps.advent2021.util.getInputLines
import java.io.File

data class Point(val rowIndex: Int, val colIndex: Int, val height: Int)

fun List<String>.toHeightMap() = map { line -> line.map { it.toString().toInt() } }

fun getNeighbors(rowIndex: Int, colIndex: Int, heightMap: List<List<Int>>, numRows: Int, numColumns: Int): List<Point> {
    val neighbors = mutableListOf<Point>()
    if (rowIndex > 0) {
        neighbors.add(Point(rowIndex - 1, colIndex, heightMap[rowIndex - 1][colIndex]))
    }
    if (rowIndex < numRows - 1) {
        neighbors.add(Point(rowIndex + 1, colIndex, heightMap[rowIndex + 1][colIndex]))
    }
    if (colIndex > 0) {
        neighbors.add(Point(rowIndex, colIndex - 1, heightMap[rowIndex][colIndex - 1]))
    }
    if (colIndex < numColumns - 1) {
        neighbors.add(Point(rowIndex, colIndex + 1, heightMap[rowIndex][colIndex + 1]))
    }
    return neighbors
}

fun getNeighbors(point: Point, heightMap: List<List<Int>>, numRows: Int, numColumns: Int) =
    getNeighbors(point.rowIndex, point.colIndex, heightMap, numRows, numColumns)

fun getLowPoints(heightMap: List<List<Int>>): List<Point> {
    val numRows = heightMap.size
    val numColumns = heightMap.first().size
    check(heightMap.all { it.size == numColumns }) { "grid is not rectangular "}

    val lowPoints = mutableListOf<Point>()
    for (rowIndex in 0 until numRows) {
        for (colIndex in 0 until numColumns) {
            val height = heightMap[rowIndex][colIndex]
            if (getNeighbors(rowIndex, colIndex, heightMap, numRows, numColumns).all { it.height > height }) {
                lowPoints.add(Point(rowIndex, colIndex, height))
            }
        }
    }
    return lowPoints
}

fun getBasin(heightMap: List<List<Int>>, lowPoint: Point): Set<Point> {
    val numRows = heightMap.size
    val numColumns = heightMap.first().size
    check(heightMap.all { it.size == numColumns }) { "grid is not rectangular "}

    val basin = mutableSetOf(lowPoint)
    val queue = mutableListOf(lowPoint)
    while (queue.isNotEmpty()) {
        val searchPoint = queue.removeFirst()
        for (neighbor in getNeighbors(searchPoint, heightMap, numRows, numColumns)) {
            if (neighbor !in basin && neighbor.height > searchPoint.height && neighbor.height < 9) {
                basin.add(neighbor)
                queue.add(neighbor)
            }
        }
    }
    return basin
}

fun getPart1Answer(lowPoints: List<Point>) =
    lowPoints.sumOf { it.height + 1 }

fun getPart2Answer(heightMap: List<List<Int>>, lowPoints: List<Point>) =
    lowPoints.map { getBasin(heightMap, it).size }
        .sorted()
        .reversed()
        .take(3)
        .fold(1) { result, basinSize -> result * basinSize }

fun main() {
    val heightMap = File("src/main/resources/day09.txt")
        .getInputLines().toHeightMap()
    val lowPoints = getLowPoints(heightMap)

    println("The answer to part 1 is ${getPart1Answer(lowPoints)}")
    println("The answer to part 2 is ${getPart2Answer(heightMap, lowPoints)}")
}
