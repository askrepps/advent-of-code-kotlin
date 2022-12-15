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

package com.askrepps.advent2022

import kotlin.system.measureTimeMillis

import com.askrepps.advent2022.day01.main as runDay01
import com.askrepps.advent2022.day02.main as runDay02
import com.askrepps.advent2022.day03.main as runDay03
import com.askrepps.advent2022.day04.main as runDay04
import com.askrepps.advent2022.day05.main as runDay05
import com.askrepps.advent2022.day06.main as runDay06
import com.askrepps.advent2022.day07.main as runDay07
import com.askrepps.advent2022.day08.main as runDay08
import com.askrepps.advent2022.day09.main as runDay09
import com.askrepps.advent2022.day10.main as runDay10
import com.askrepps.advent2022.day11.main as runDay11
import com.askrepps.advent2022.day12.main as runDay12
import com.askrepps.advent2022.day13.main as runDay13
import com.askrepps.advent2022.day14.main as runDay14

private val runners = mapOf(
     1 to ::runDay01,  2 to ::runDay02,  3 to ::runDay03,  4 to ::runDay04,  5 to ::runDay05,
     6 to ::runDay06,  7 to ::runDay07,  8 to ::runDay08,  9 to ::runDay09, 10 to ::runDay10,
    11 to ::runDay11, 12 to ::runDay12, 13 to ::runDay13, 14 to ::runDay14
)

fun runDay(dayNumber: Int): Long {
    println("Day $dayNumber")
    val elapsedTime = measureTimeMillis {
        runners[dayNumber]?.invoke()
            ?: throw IllegalArgumentException("No runner found for day $dayNumber")
    }
    println("Elapsed time: ${elapsedTime.millisecondsToSeconds()} s\n")
    return elapsedTime
}

fun Long.millisecondsToSeconds() =
    toDouble() / 1000.0

fun main(args: Array<String>) {
    val day = args.firstOrNull()
    if (day == null) {
        println("Running all ${runners.size} days\n")
        var totalTime = 0L
        for (dayNumber in runners.keys.sorted()) {
            totalTime += runDay(dayNumber)
        }
        println("Total elapsed time: ${totalTime.millisecondsToSeconds()} s")
    } else {
        val dayNumber = day.toIntOrNull()
            ?: throw IllegalArgumentException("Day must be a valid integer")
        runDay(dayNumber)
    }
}
