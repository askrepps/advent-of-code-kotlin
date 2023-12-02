/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Andrew Krepps
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

package com.askrepps.advent.advent2022.day01

import java.io.File

fun String.toCalorieLists() =
    split("\n\n").map { group ->
        group.split("\n")
            .mapNotNull { line -> line.toIntOrNull() }
    }

fun getPart1Answer(calorieLists: List<List<Int>>) =
    calorieLists.maxOf { it.sum() }

fun getPart2Answer(calorieLists: List<List<Int>>) =
    calorieLists.map { it.sum() }
        .sortedDescending()
        .take(3)
        .sum()

fun main() {
    val calorieLists = File("src/main/resources/2022/day01.txt")
        .readText().toCalorieLists()

    println("The answer to part 1 is ${getPart1Answer(calorieLists)}")
    println("The answer to part 2 is ${getPart2Answer(calorieLists)}")
}
