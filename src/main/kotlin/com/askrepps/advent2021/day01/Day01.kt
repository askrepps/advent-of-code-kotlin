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

package com.askrepps.advent2021.day01

import com.askrepps.advent2021.util.getInputLines
import java.io.File

fun getPart1Answer(lines: List<String>): Int {
    var increasingCount = 0
    val depths = lines.map { it.toInt() }
    for (i in 1 until depths.size) {
        if (depths[i] > depths[i - 1]) {
            increasingCount++
        }
    }
    return increasingCount
}

fun getPart2Answer(lines: List<String>): Int {
    var increasingCount = 0
    var lastSum = Int.MAX_VALUE
    val depths = lines.map { it.toInt() }
    for (i in 2 until depths.size) {
        val sum = depths[i] + depths[i - 1] + depths[i - 2]
        if (sum > lastSum) {
            increasingCount++
        }
        lastSum = sum
    }
    return increasingCount
}

fun main() {
    val lines = File("src/main/resources/day01.txt").getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
