/*
 * MIT License
 *
 * Copyright (c) 2024 Andrew Krepps
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

package com.askrepps.advent.advent2024.day04

import com.askrepps.advent.util.getInputLines
import java.io.File

private const val SEARCH_WORD = "XMAS"

private const val SEARCH_SPEC_START = 'A'

// possible arrangements
//
// M   S
//   A
// M   S
//
// M   M
//   A
// S   S
//
// S   M
//   A
// S   M
//
// S   S
//   A
// M   M
private val SEARCH_SPECS = listOf(
    listOf(
        "AS" to GridDirection.UpRight,
        "AS" to GridDirection.DownRight,
        "AM" to GridDirection.DownLeft,
        "AM" to GridDirection.UpLeft
    ),
    listOf(
        "AM" to GridDirection.UpRight,
        "AS" to GridDirection.DownRight,
        "AS" to GridDirection.DownLeft,
        "AM" to GridDirection.UpLeft
    ),
    listOf(
        "AM" to GridDirection.UpRight,
        "AM" to GridDirection.DownRight,
        "AS" to GridDirection.DownLeft,
        "AS" to GridDirection.UpLeft
    ),
    listOf(
        "AS" to GridDirection.UpRight,
        "AM" to GridDirection.DownRight,
        "AM" to GridDirection.DownLeft,
        "AS" to GridDirection.UpLeft
    )
)

enum class GridDirection(val deltaRow: Int, val deltaColumn: Int) {
    Up(-1, 0),
    UpRight(-1, 1),
    Right(0, 1),
    DownRight(1, 1),
    Down(1, 0),
    DownLeft(1, -1),
    Left(0, -1),
    UpLeft(-1, -1)
}

fun gridPointStartsWordInDirection(
    grid: List<List<Char>>,
    word: String,
    rowIndex: Int,
    columnIndex: Int,
    direction: GridDirection
): Boolean {
    val maxDistance = word.length - 1
    if (rowIndex + maxDistance * direction.deltaRow !in grid.indices) {
        return false
    }
    if (columnIndex + maxDistance * direction.deltaColumn !in grid[rowIndex].indices) {
        return false
    }
    var currentRow = rowIndex
    var currentColumn = columnIndex
    for (character in word) {
        if (grid[currentRow][currentColumn] != character) {
            return false
        }
        currentRow += direction.deltaRow
        currentColumn += direction.deltaColumn
    }
    return true
}

fun countWordInstancesFromGridPoint(grid: List<List<Char>>, word: String, rowIndex: Int, columnIndex: Int) =
    if (grid[rowIndex][columnIndex] == word.first()) {
        GridDirection.entries.count { gridPointStartsWordInDirection(grid, word, rowIndex, columnIndex, it) }
    } else {
        0
    }

fun getPart1Answer(grid: List<List<Char>>) =
    grid.withIndex().sumOf { (rowIndex, row) ->
        row.indices.sumOf { columnIndex ->
            countWordInstancesFromGridPoint(grid, SEARCH_WORD, rowIndex, columnIndex).toLong()
        }
    }

fun getPart2Answer(grid: List<List<Char>>) =
    grid.withIndex().sumOf { (rowIndex, row) ->
        row.indices.sumOf { columnIndex ->
            when {
                grid[rowIndex][columnIndex] != SEARCH_SPEC_START -> 0L
                SEARCH_SPECS.any { spec ->
                    spec.all { (word, direction) ->
                        gridPointStartsWordInDirection(grid, word, rowIndex, columnIndex, direction)
                    }
                } -> 1L
                else -> 0L
            }
        }
    }

fun main() {
    val grid = File("src/main/resources/2024/input-2024-day04.txt")
        .getInputLines().map { it.toList() }

    println("The answer to part 1 is ${getPart1Answer(grid)}")
    println("The answer to part 2 is ${getPart2Answer(grid)}")
}
