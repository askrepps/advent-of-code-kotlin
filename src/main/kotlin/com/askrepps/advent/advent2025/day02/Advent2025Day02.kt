/*
 * MIT License
 *
 * Copyright (c) 2025 Andrew Krepps
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

package com.askrepps.advent.advent2025.day02

import java.io.File

fun String.toRanges() = trim().split(',').map {
    val (first, last) = it.trim().split('-')
    LongRange(first.toLong(), last.toLong())
}

fun String.isInvalidId(numRepeats: Int): Boolean {
    if (length % numRepeats != 0) {
        return false
    }
    val patternLength = length / numRepeats
    for (pattern in 0 until numRepeats - 1) {
        val firstStart = patternLength * pattern
        val secondStart = firstStart + patternLength
        for (offset in 0 until patternLength) {
            if (get(firstStart + offset) != get(secondStart + offset)) {
                return false
            }
        }
    }
    return true
}

fun getPart1Answer(ranges: List<LongRange>): Long {
    return ranges.sumOf { range ->
        range.sumOf { value ->
            val id = value.toString()
            if (id.isInvalidId(numRepeats = 2)) value else 0L
        }
    }
}

fun getPart2Answer(ranges: List<LongRange>): Long {
    return ranges.sumOf { range ->
        range.sumOf { value ->
            val id = value.toString()
            var result = 0L
            for (repeats in 2..id.length) {
                if (id.isInvalidId(repeats)) {
                    result = value
                    break
                }
            }
            result
        }
    }
}

fun main() {
    val ranges = File("src/main/resources/2025/input-2025-day02.txt")
        .readText().toRanges()

    println("The answer to part 1 is ${getPart1Answer(ranges)}")
    println("The answer to part 2 is ${getPart2Answer(ranges)}")
}
