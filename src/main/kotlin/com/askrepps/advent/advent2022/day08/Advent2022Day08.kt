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

package com.askrepps.advent.advent2022.day08

import com.askrepps.advent.util.getInputLines
import java.io.File

fun List<String>.toHeightMap() = map { row ->
    row.map { it.digitToInt() }
}

fun isTreeVisible(heightMap: List<List<Int>>, treeRow: Int, treeColumn: Int): Boolean {
    val treeHeight = heightMap[treeRow][treeColumn]

    fun testRows(rows: IntProgression) = rows.all { heightMap[it][treeColumn] < treeHeight }

    fun testColumns(columns: IntProgression) = columns.all { heightMap[treeRow][it] < treeHeight }

    return testRows(0 until treeRow)
        || testRows(treeRow + 1 until heightMap.size)
        || testColumns(0 until treeColumn)
        || testColumns(treeColumn + 1 until heightMap[treeRow].size)
}

inline fun <T> Iterable<T>.countUntil(predicate: (T) -> Boolean): Int {
    var count = 0
    for (value in this) {
        count++
        if (predicate(value)) {
            break
        }
    }
    return count
}

fun getTreeScenicScore(heightMap: List<List<Int>>, treeRow: Int, treeColumn: Int): Int {
    val treeHeight = heightMap[treeRow][treeColumn]

    fun countRows(rows: IntProgression) = rows.countUntil { heightMap[it][treeColumn] >= treeHeight }

    fun countColumns(columns: IntProgression) = columns.countUntil { heightMap[treeRow][it] >= treeHeight }

    return countRows(treeRow - 1 downTo 0) *
        countRows(treeRow + 1 until heightMap.size) *
        countColumns(treeColumn - 1 downTo 0) *
        countColumns(treeColumn + 1 until heightMap[treeRow].size)
}

fun getPart1Answer(heightMap: List<List<Int>>) =
    heightMap.indices.sumOf { row ->
        heightMap[row].indices.count { column ->
            isTreeVisible(heightMap, row, column)
        }
    }

fun getPart2Answer(heightMap: List<List<Int>>) =
    heightMap.indices.maxOf { row ->
        heightMap[row].indices.maxOf { column ->
            getTreeScenicScore(heightMap, row, column)
        }
    }

fun main() {
    val heightMap = File("src/main/resources/2022/input-2022-day08.txt")
        .getInputLines().toHeightMap()

    println("The answer to part 1 is ${getPart1Answer(heightMap)}")
    println("The answer to part 2 is ${getPart2Answer(heightMap)}")
}
