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

package com.askrepps.advent.advent2021.day07

import java.io.File
import kotlin.math.abs
import kotlin.math.min

fun findOptimalAlignmentCost(startingPositions: List<Int>, calcFuelCost: (Int, Int) -> Int): Int {
    val minPosition = startingPositions.minOrNull() ?: throw RuntimeException("Can't find min")
    val maxPosition = startingPositions.maxOrNull() ?: throw RuntimeException("Can't find max")
    return (minPosition..maxPosition).fold(Int.MAX_VALUE) { optimalFuel, toPosition ->
        min(optimalFuel, startingPositions.sumOf { fromPosition -> calcFuelCost(fromPosition, toPosition) })
    }
}

fun getPart1Answer(startingPositions: List<Int>) =
    findOptimalAlignmentCost(startingPositions) { from, to -> abs(from - to) }

fun getPart2Answer(startingPositions: List<Int>) =
    findOptimalAlignmentCost(startingPositions) { from, to ->
        val distance = abs(from - to)
        distance * (distance + 1) / 2
    }

fun main() {
    val startingPositions = File("src/main/resources/2021/input-2021-day07.txt")
        .readText().split(",").map { it.toInt() }

    println("The answer to part 1 is ${getPart1Answer(startingPositions)}")
    println("The answer to part 2 is ${getPart2Answer(startingPositions)}")
}
