/*
 * MIT License
 *
 * Copyright (c) 2021-2023 Andrew Krepps
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

package com.askrepps.advent.advent2021.day25

import com.askrepps.advent.util.getInputLines
import java.io.File

enum class Direction { Right, Down }

data class GridCoordinates(val row: Int, val col: Int)

data class CucumberState(val rows: Int, val cols: Int, val cucumbers: Map<GridCoordinates, Direction>)

fun List<String>.toCucumberState(): CucumberState {
    val cucumbers = mutableMapOf<GridCoordinates, Direction>()
    for ((row, line) in this.withIndex()) {
        for ((col, c) in line.withIndex()) {
            val direction = when (c) {
                '>' -> Direction.Right
                'v' -> Direction.Down
                else -> null
            }
            if (direction != null) {
                cucumbers[GridCoordinates(row, col)] = direction
            }
        }
    }
    return CucumberState(size, first().length, cucumbers)
}

fun CucumberState.getNextState(moveDirection: Direction): CucumberState {
    val newCucumbers = mutableMapOf<GridCoordinates, Direction>()
    for ((coordinates, direction) in cucumbers) {
        if (direction == moveDirection) {
            val proposedMove = when (direction) {
                Direction.Right -> GridCoordinates(coordinates.row, (coordinates.col + 1) % cols)
                Direction.Down -> GridCoordinates((coordinates.row + 1) % rows, coordinates.col)
            }
            if (cucumbers.containsKey(proposedMove)) {
                newCucumbers[coordinates] = direction
            } else {
                newCucumbers[proposedMove] = direction
            }
        } else {
            newCucumbers[coordinates] = direction
        }
    }
    return CucumberState(rows, cols, newCucumbers)
}

fun awaitCucumberStillness(initialState: CucumberState): Long {
    var step = 0L
    var currentState = initialState
    while (true) {
        val previousState = currentState
        currentState = currentState.getNextState(Direction.Right)
        currentState = currentState.getNextState(Direction.Down)
        step++
        if (previousState == currentState) {
            return step
        }
    }
}

fun getPart1Answer(initialState: CucumberState) =
    awaitCucumberStillness(initialState)

fun main() {
    val initialState = File("src/main/resources/2021/input-2021-day25.txt")
        .getInputLines().toCucumberState()

    println("The answer to part 1 is ${getPart1Answer(initialState)}")
    println("There is no part 2. Go back to enjoying the holidays!")
}
