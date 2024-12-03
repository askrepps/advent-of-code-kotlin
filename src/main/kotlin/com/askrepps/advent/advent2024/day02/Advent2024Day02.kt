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

package com.askrepps.advent.advent2024.day02

import com.askrepps.advent.util.getInputLines
import java.io.File

private val INCREASING_SAFE_DELTA_RANGE = 1..3
private val DECREASING_SAVE_DELTA_RANGE = -3..-1

fun String.toReport() = split(" ").map { it.toLong() }

fun List<Long>.isSafe(hiddenIndex: Int? = null): Boolean {
    val processedList = hiddenIndex?.let { toMutableList().apply { removeAt(it) } } ?: this
    require(processedList.size > 1) { "Report must have multiple entries" }
    val (first, second) = processedList
    val safeDeltaRange = when {
        first < second -> INCREASING_SAFE_DELTA_RANGE
        first > second -> DECREASING_SAVE_DELTA_RANGE
        else -> null
    } ?: return false

    return processedList.withIndex().drop(1).all { (index, value) ->
        value - processedList[index - 1] in safeDeltaRange
    }
}

fun getPart1Answer(reports: List<List<Long>>) = reports.count { it.isSafe() }

fun getPart2Answer(reports: List<List<Long>>) = reports.count { report ->
    report.isSafe() || report.indices.any { report.isSafe(hiddenIndex = it) }
}

fun main() {
    val reports = File("src/main/resources/2024/input-2024-day02.txt")
        .getInputLines().map { it.toReport() }

    println("The answer to part 1 is ${getPart1Answer(reports)}")
    println("The answer to part 2 is ${getPart2Answer(reports)}")
}
