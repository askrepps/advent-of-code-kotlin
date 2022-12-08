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

package com.askrepps.advent2022.day08

import com.askrepps.advent2022.util.getInputLines
import java.io.File

fun List<String>.toHeightMap() = map { row ->
    row.map { it.digitToInt() }
}

fun isTreeVisible(heightMap: List<List<Int>>, treeRow: Int, treeColumn: Int): Boolean {
    val treeHeight = heightMap[treeRow][treeColumn]

    if ((0 until treeRow).all { heightMap[it][treeColumn] < treeHeight }) {
        return true
    }
    if ((treeRow + 1 until heightMap.size).all { heightMap[it][treeColumn] < treeHeight }) {
        return true
    }
    if ((0 until treeColumn).all { heightMap[treeRow][it] < treeHeight }) {
        return true
    }
    if ((treeColumn + 1 until heightMap[treeRow].size).all { heightMap[treeRow][it] < treeHeight }) {
        return true
    }

    return false
}

fun getTreeScenicScore(heightMap: List<List<Int>>, treeRow: Int, treeColumn: Int): Int {
    val treeHeight = heightMap[treeRow][treeColumn]

    var upperCount = 0
    for (upperRow in treeRow - 1 downTo 0) {
        upperCount++
        if (heightMap[upperRow][treeColumn] >= treeHeight) {
            break
        }
    }

    var lowerCount = 0
    for (lowerRow in treeRow + 1 until heightMap.size) {
        lowerCount++
        if (heightMap[lowerRow][treeColumn] >= treeHeight) {
            break
        }
    }

    var leftCount = 0
    for (leftColumn in treeColumn - 1 downTo 0) {
        leftCount++
        if (heightMap[treeRow][leftColumn] >= treeHeight) {
            break
        }
    }

    var rightCount = 0
    for (rightColumn in treeColumn + 1 until heightMap[treeRow].size) {
        rightCount++
        if (heightMap[treeRow][rightColumn] >= treeHeight) {
            break
        }
    }

    return upperCount * lowerCount * leftCount * rightCount
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
    val lines = File("src/main/resources/2022/day08.txt")
        .getInputLines().toHeightMap()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
