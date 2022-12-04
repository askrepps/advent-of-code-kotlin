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

package com.askrepps.advent2022.day04

import com.askrepps.advent2022.util.getInputLines
import java.io.File

fun String.toRange(): IntRange {
    val (minVal, maxVal) = split("-")
    return IntRange(minVal.toInt(), maxVal.toInt())
}

fun String.toRangePairs(): Pair<IntRange, IntRange> {
    val (rangeText1, rangeText2) = split(",")
    return rangeText1.toRange() to rangeText2.toRange()
}

fun IntRange.fullyContains(other: IntRange) =
    first <= other.first && last >= other.last

fun IntRange.overlaps(other: IntRange) =
    last >= other.first && first <= other.last

fun getPart1Answer(pairs: List<Pair<IntRange, IntRange>>) =
    pairs.count { it.first.fullyContains(it.second) || it.second.fullyContains(it.first) }

fun getPart2Answer(pairs: List<Pair<IntRange, IntRange>>) =
    pairs.count { it.first.overlaps(it.second) }

fun main() {
    val lines = File("src/main/resources/2022/day04.txt")
        .getInputLines().map { it.toRangePairs() }

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
