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

package com.askrepps.advent2021

import kotlin.system.measureTimeMillis
import com.askrepps.advent2021.day01.main as runDay01
import com.askrepps.advent2021.day02.main as runDay02
import com.askrepps.advent2021.day03.main as runDay03
import com.askrepps.advent2021.day04.main as runDay04
import com.askrepps.advent2021.day05.main as runDay05
import com.askrepps.advent2021.day06.main as runDay06
import com.askrepps.advent2021.day07.main as runDay07
import com.askrepps.advent2021.day08.main as runDay08
import com.askrepps.advent2021.day09.main as runDay09
import com.askrepps.advent2021.day10.main as runDay10
import com.askrepps.advent2021.day11.main as runDay11
import com.askrepps.advent2021.day12.main as runDay12
import com.askrepps.advent2021.day13.main as runDay13
import com.askrepps.advent2021.day14.main as runDay14
import com.askrepps.advent2021.day15.main as runDay15
import com.askrepps.advent2021.day16.main as runDay16
import com.askrepps.advent2021.day17.main as runDay17
import com.askrepps.advent2021.day18.main as runDay18
import com.askrepps.advent2021.day19.main as runDay19
import com.askrepps.advent2021.day20.main as runDay20
import com.askrepps.advent2021.day21.main as runDay21
import com.askrepps.advent2021.day22.main as runDay22
import com.askrepps.advent2021.day23.main as runDay23
import com.askrepps.advent2021.day24.main as runDay24
import com.askrepps.advent2021.day25.main as runDay25

private val runners = listOf(
    ::runDay01, ::runDay02, ::runDay03, ::runDay04, ::runDay05,
    ::runDay06, ::runDay07, ::runDay08, ::runDay09, ::runDay10,
    ::runDay11, ::runDay12, ::runDay13, ::runDay14, ::runDay15,
    ::runDay16, ::runDay17, ::runDay18, ::runDay19, ::runDay20,
    ::runDay21, ::runDay22, ::runDay23, ::runDay24, ::runDay25
)

fun runDay(dayNumber: Int) {
    println("Day $dayNumber")
    val elapsedTime = measureTimeMillis {
        runners.getOrNull(dayNumber - 1)?.invoke()
            ?: throw IllegalArgumentException("No runner found for day $dayNumber")
    }
    println("Elapsed time: ${elapsedTime.millisecondsToSeconds()} s\n")
}

fun Long.millisecondsToSeconds() =
    toDouble() / 1000.0

fun main(args: Array<String>) {
    val day = args.firstOrNull()
    if (day == null) {
        println("Running all ${runners.size} days\n")
        val elapsedTime = measureTimeMillis {
            for (dayNumber in 1..runners.size) {
                runDay(dayNumber)
            }
        }
        println("Total elapsed time: ${elapsedTime.millisecondsToSeconds()} s")
    } else {
        val dayNumber = day.toIntOrNull()
            ?: throw IllegalArgumentException("Day must be a valid integer")
        runDay(dayNumber)
    }
}
