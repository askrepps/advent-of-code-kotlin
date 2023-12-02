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

package com.askrepps.advent.advent2021.day11

import com.askrepps.advent.util.getInputLines
import java.io.File
import java.util.ArrayDeque
import java.util.Deque

private const val FLASH_THRESHOLD = 9

private val NEIGHBOR_DIRECTIONS = listOf(
    Pair(-1, 0),
    Pair(1, 0),
    Pair(0, -1),
    Pair(0, 1),
    Pair(-1, -1),
    Pair(1, -1),
    Pair(-1, 1),
    Pair(1, 1)
)

data class GridCoordinates(val row: Int, val col: Int)

data class Octopus(val coordinates: GridCoordinates, var energy: Int)

fun List<String>.toOctopi() =
    mapIndexed { row, rowData ->
        rowData.mapIndexed { col, value ->
            Octopus(GridCoordinates(row, col), value.code - '0'.code)
        }
    }

fun List<List<Octopus>>.deepCopy() =
    map { octoRow -> octoRow.map { it.copy() } }

fun simulateStep(octopi: List<List<Octopus>>): Int {
    // set must store immutable data to behave properly
    val allFlashCoordinates = mutableSetOf<GridCoordinates>()
    val flashQueue: Deque<Octopus> = ArrayDeque()

    fun Octopus.increaseEnergy() {
        energy++
        if (energy > FLASH_THRESHOLD && coordinates !in allFlashCoordinates) {
            flashQueue.add(this)
            allFlashCoordinates.add(coordinates)
        }
    }

    for (octoRow in octopi) {
        for (octopus in octoRow) {
            octopus.increaseEnergy()
        }
    }

    while (flashQueue.isNotEmpty()) {
        val flashedOctopus = flashQueue.removeFirst()

        NEIGHBOR_DIRECTIONS.mapNotNull { (deltaRow, deltaCol) ->
            octopi.getOrNull(flashedOctopus.coordinates.row + deltaRow)
                ?.getOrNull(flashedOctopus.coordinates.col + deltaCol)
        }.forEach { octopus ->
            octopus.increaseEnergy()
        }
    }

    for ((row, col) in allFlashCoordinates) {
        octopi[row][col].energy = 0
    }

    return allFlashCoordinates.size
}

fun getPart1Answer(octopi: List<List<Octopus>>): Int {
    val octoCopy = octopi.deepCopy()
    return (1..100).sumOf { simulateStep(octoCopy) }
}

fun getPart2Answer(octopi: List<List<Octopus>>): Int {
    val octoCopy = octopi.deepCopy()
    val totalOctopi = octopi.sumOf { it.size }

    var step = 1
    while (true) {
        val flashCount = simulateStep(octoCopy)
        if (flashCount == totalOctopi) {
            return step
        }
        step++
    }
}

fun main() {
    val octopi = File("src/main/resources/day11.txt")
        .getInputLines().toOctopi()

    println("The answer to part 1 is ${getPart1Answer(octopi)}")
    println("The answer to part 2 is ${getPart2Answer(octopi)}")
}
