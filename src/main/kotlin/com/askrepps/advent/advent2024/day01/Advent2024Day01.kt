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

package com.askrepps.advent.advent2024.day01

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.abs

fun List<String>.toLocationLists(): Pair<List<Long>, List<Long>> {
    val lists = mutableListOf<Long>() to mutableListOf<Long>()
    for (line in this) {
        val (str1, str2) = line.split("   ")
        lists.first.add(str1.toLong())
        lists.second.add(str2.toLong())
    }
    return lists
}

fun getPart1Answer(firstList: List<Long>, secondList: List<Long>): Long {
    return firstList.sorted().zip(secondList.sorted()).sumOf { (first, second) -> abs(first - second) }
}

fun getPart2Answer(firstList: List<Long>, secondList: List<Long>): Long {
    val counts = mutableMapOf<Long, Long>()
    for (location in secondList) {
        counts[location] = counts.getOrDefault(location, 0L) + 1L
    }
    return firstList.sumOf { it * counts.getOrDefault(it, 0L) }
}

fun main() {
    val (firstList, secondList) = File("src/main/resources/2024/input-2024-day01.txt")
        .getInputLines().toLocationLists()

    println("The answer to part 1 is ${getPart1Answer(firstList, secondList)}")
    println("The answer to part 2 is ${getPart2Answer(firstList, secondList)}")
}
