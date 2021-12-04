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

package com.askrepps.advent2021.day04

import com.askrepps.advent2021.util.getInputLines
import java.io.File

data class CellCoordinates(val rowIndex: Int, val colIndex: Int)

data class BingoBoard(val id: Int, val grid: List<List<Int>>) {
    private val coordinateMap = grid.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, value ->
            value to CellCoordinates(rowIndex, columnIndex)
        }
    }.toMap()

    private val markedCells = mutableSetOf<CellCoordinates>()

    fun markValue(value: Int) = coordinateMap[value]?.let { markedCells.add(it) }

    val hasWon: Boolean
        get() = grid.indices.any { rowIndex ->
            grid.indices.all { colIndex -> CellCoordinates(rowIndex, colIndex) in markedCells }
        } || grid.indices.any { colIndex ->
            grid.indices.all { rowIndex -> CellCoordinates(rowIndex, colIndex) in markedCells }
        }

    val unmarkedValues: List<Int>
        get() = grid.flatMapIndexed { rowIndex, row ->
            row.filterIndexed { colIndex, _ ->
                CellCoordinates(rowIndex, colIndex) !in markedCells
            }
        }
}

fun List<String>.parseBoards(): List<BingoBoard> {
    val boards = mutableListOf<BingoBoard>()
    var grid = mutableListOf<List<Int>>()
    for (line in this) {
        if (line.isBlank()) {
            boards.add(BingoBoard(boards.size, grid))
            grid = mutableListOf()
        } else {
            val tokens = line.trim().split("\\s+".toRegex())
            grid.add(tokens.map { it.toInt() })
        }
    }
    boards.add(BingoBoard(boards.size, grid))
    return boards
}

fun findWinningBoardResult(calls: List<Int>, boards: List<BingoBoard>, boardThreshold: Int = 1): Int {
    var result = -1
    val winningBoardIds = mutableSetOf<Int>()

    for (call in calls) {
        for (board in boards) {
            if (board.id in winningBoardIds) {
                continue
            }
            board.markValue(call)
            if (board.hasWon) {
                winningBoardIds.add(board.id)
                result = call * board.unmarkedValues.sum()
                if (boardThreshold > 0 && winningBoardIds.size >= boardThreshold) {
                    return result
                }
            }
        }
    }

    return result
}

fun getPart1Answer(calls: List<Int>, boards: List<BingoBoard>) =
    findWinningBoardResult(calls, boards)

fun getPart2Answer(calls: List<Int>, boards: List<BingoBoard>) =
    findWinningBoardResult(calls, boards, boardThreshold = -1)

fun main() {
    val lines = File("src/main/resources/day04.txt")
        .getInputLines(includeBlanks = true)

    val calls = lines.first().split(",").map { it.toInt() }
    val boards = lines.subList(2, lines.size).parseBoards()

    println("The answer to part 1 is ${getPart1Answer(calls, boards)}")
    println("The answer to part 2 is ${getPart2Answer(calls, boards)}")
}
