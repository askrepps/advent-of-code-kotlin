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

package com.askrepps.advent.advent2023.day01

import com.askrepps.advent.util.getInputLines
import java.io.File

private val WORD_REPLACEMENTS = mapOf(
    "one" to "o1e",
    "two" to "t2o",
    "three" to "t3e",
    "four" to "f4r",
    "five" to "f5e",
    "six" to "s6x",
    "seven" to "s7n",
    "eight" to "e8t",
    "nine" to "n9e"
)

fun String.toCalibrationValue() =
    mapNotNull { it.digitToIntOrNull() }
        .let { 10 * it.first() + it.last() }

fun String.convertDigitWords() =
    WORD_REPLACEMENTS.entries.fold(this) { current, (digit, value) ->
        current.replace(digit, value)
    }

fun getPart1Answer(lines: List<String>) =
    lines.sumOf { it.toCalibrationValue() }

fun getPart2Answer(lines: List<String>) =
    lines.sumOf { it.convertDigitWords().toCalibrationValue() }

fun main() {
    val lines = File("src/main/resources/2023/input-2023-day01.txt")
        .getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
