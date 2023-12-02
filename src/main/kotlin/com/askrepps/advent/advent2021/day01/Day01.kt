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

package com.askrepps.advent.advent2021.day01

import com.askrepps.advent.util.getInputLines
import java.io.File

fun List<Int>.countIncreases() =
    (1 until size).count { this[it] > this[it - 1] }

fun List<Int>.getWindowSums(windowSize: Int) =
    (windowSize..size).map { subList(it - windowSize, it).sum() }

fun getPart1Answer(depths: List<Int>) =
    depths.countIncreases()

fun getPart2Answer(depths: List<Int>) =
    depths.getWindowSums(windowSize = 3).countIncreases()

fun main() {
    val depths = File("src/main/resources/day01.txt")
        .getInputLines().map { it.toInt() }

    println("The answer to part 1 is ${getPart1Answer(depths)}")
    println("The answer to part 2 is ${getPart2Answer(depths)}")
}
