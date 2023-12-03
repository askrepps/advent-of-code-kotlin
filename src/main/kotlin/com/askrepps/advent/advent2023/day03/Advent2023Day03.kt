/*
 * MIT License
 *
 * Copyright (c) 2023 Andrew Krepps
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

package com.askrepps.advent.advent2023.day03

import com.askrepps.advent.util.getInputLines
import java.io.File

data class GridCoordinates(val row: Int, val column: Int)

data class GridNumber(val row: Int, val startColumn: Int, val endColumn: Int, val value: Long) {

    private var _surroundingCoordinates: Set<GridCoordinates>? = null
    val surroundingCoordinates: Set<GridCoordinates>
        get() = _surroundingCoordinates ?: calculateSurroundingCoordinates().also {
            _surroundingCoordinates = it
        }

    private fun calculateSurroundingCoordinates() =
        mutableSetOf<GridCoordinates>().apply {
            for (column in (startColumn - 1)..(endColumn + 1)) {
                add(GridCoordinates(row - 1, column))
                add(GridCoordinates(row + 1, column))
            }
            add(GridCoordinates(row, startColumn - 1))
            add(GridCoordinates(row, endColumn + 1))
        }
}

data class GridSymbol(val coordinates: GridCoordinates, val value: Char)

fun parseNumbers(lines: List<String>): List<GridNumber> =
    mutableListOf<GridNumber>().apply {
        var accumulator: Long? = null
        var startColumn: Int? = null

        fun finalizeAccumulatedNumber(row: Int, endColumn: Int) {
            accumulator?.let { value ->
                startColumn?.let { start ->
                    add(GridNumber(row, start, endColumn, value))
                } ?: error("Accumulated value with no start column")
            } ?: error("Missing accumulated value")
            accumulator = null
            startColumn = null
        }

        lines.forEachIndexed { row, line ->
            line.forEachIndexed { column, token ->
                when {
                    token.isDigit() -> {
                        accumulator = 10L * (accumulator ?: 0L) + token.digitToInt()
                        startColumn = startColumn ?: column
                    }
                    accumulator != null -> {
                        finalizeAccumulatedNumber(row, column - 1)
                    }
                }
            }
            if (accumulator != null) {
                finalizeAccumulatedNumber(row, line.length - 1)
            }
        }
    }

fun parseSymbols(lines: List<String>) =
    lines.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { column, token ->
            if (token != '.' && !token.isDigit()) {
                GridSymbol(GridCoordinates(row, column), token)
            } else {
                null
            }
        }
    }

fun getPart1Answer(numbers: List<GridNumber>, symbols: List<GridSymbol>): Long {
    val symbolCoordinates = symbols.map { it.coordinates }.toSet()
    return numbers.filter { it.surroundingCoordinates.intersect(symbolCoordinates).isNotEmpty() }
        .sumOf { it.value }
}

fun getPart2Answer(numbers: List<GridNumber>, symbols: List<GridSymbol>) =
    symbols.filter { it.value == '*' }
        .map { gear -> numbers.filter { it.surroundingCoordinates.contains(gear.coordinates) } }
        .filter { it.size == 2 }
        .sumOf { (first, last) -> first.value * last.value }

fun main() {
    val lines = File("src/main/resources/2023/input-2023-day03.txt").getInputLines()
    val numbers = parseNumbers(lines)
    val symbols = parseSymbols(lines)

    println("The answer to part 1 is ${getPart1Answer(numbers, symbols)}")
    println("The answer to part 2 is ${getPart2Answer(numbers, symbols)}")
}
