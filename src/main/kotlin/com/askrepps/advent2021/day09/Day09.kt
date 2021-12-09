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

private const val MAX_BASIN_HEIGHT = 8

data class Point(val rowIndex: Int, val colIndex: Int, val height: Int)

class HeightMap(heightData: List<List<Int>>) {
    private val points: List<List<Point>>
    private val numRows: Int
    private val numColumns: Int

    init {
        check(heightData.isNotEmpty()) { "Height map must have data" }

        numRows = heightData.size
        numColumns = heightData.first().size

        check(heightData.all { it.size == numColumns }) { "Map grid is not rectangular" }

        points = heightData.mapIndexed { rowIndex, rowData ->
            rowData.mapIndexed { colIndex, height -> Point(rowIndex, colIndex, height) }
        }
    }

    val lowPoints: List<Point> by lazy { calcLowPoints() }

    private fun getNeighbors(point: Point) =
        listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
            .mapNotNull { (deltaRow, deltaCol) ->
                points.getOrNull(point.rowIndex + deltaRow)?.getOrNull(point.colIndex + deltaCol)
            }

    private fun calcLowPoints() =
        points.flatMap { row ->
            row.filter { point -> getNeighbors(point).all { it.height > point.height } }
        }

    fun getBasinForPoint(lowPoint: Point): Set<Point> {
        val basin = mutableSetOf(lowPoint)
        val searchStack = mutableListOf(lowPoint)
        while (searchStack.isNotEmpty()) {
            val currentPoint = searchStack.removeLast()
            getNeighbors(currentPoint)
                .filter { it !in basin && it.height > currentPoint.height && it.height <= MAX_BASIN_HEIGHT }
                .forEach {
                    basin.add(it)
                    searchStack.add(it)
                }
        }
        return basin
    }
}

fun List<String>.toHeightMap() =
    HeightMap(heightData = map { line -> line.map { it.code - '0'.code } })

fun getPart1Answer(heightMap: HeightMap) =
    heightMap.lowPoints.sumOf { it.height + 1 }

fun getPart2Answer(heightMap: HeightMap) =
    heightMap.lowPoints.map { heightMap.getBasinForPoint(it).size }
        .sortedDescending()
        .take(3)
        .fold(1, Int::times)

fun main() {
    val heightMap = File("src/main/resources/day09.txt")
        .getInputLines().toHeightMap()

    println("The answer to part 1 is ${getPart1Answer(heightMap)}")
    println("The answer to part 2 is ${getPart2Answer(heightMap)}")
}
