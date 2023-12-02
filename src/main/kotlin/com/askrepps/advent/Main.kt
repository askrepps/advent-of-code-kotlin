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

package com.askrepps.advent

import kotlin.system.measureTimeMillis

import com.askrepps.advent.advent2021.day01.main as run2021Day01
import com.askrepps.advent.advent2021.day02.main as run2021Day02
import com.askrepps.advent.advent2021.day03.main as run2021Day03
import com.askrepps.advent.advent2021.day04.main as run2021Day04
import com.askrepps.advent.advent2021.day05.main as run2021Day05
import com.askrepps.advent.advent2021.day06.main as run2021Day06
import com.askrepps.advent.advent2021.day07.main as run2021Day07
import com.askrepps.advent.advent2021.day08.main as run2021Day08
import com.askrepps.advent.advent2021.day09.main as run2021Day09
import com.askrepps.advent.advent2021.day10.main as run2021Day10
import com.askrepps.advent.advent2021.day11.main as run2021Day11
import com.askrepps.advent.advent2021.day12.main as run2021Day12
import com.askrepps.advent.advent2021.day13.main as run2021Day13
import com.askrepps.advent.advent2021.day14.main as run2021Day14
import com.askrepps.advent.advent2021.day15.main as run2021Day15
import com.askrepps.advent.advent2021.day16.main as run2021Day16
import com.askrepps.advent.advent2021.day17.main as run2021Day17
import com.askrepps.advent.advent2021.day18.main as run2021Day18
import com.askrepps.advent.advent2021.day19.main as run2021Day19
import com.askrepps.advent.advent2021.day20.main as run2021Day20
import com.askrepps.advent.advent2021.day21.main as run2021Day21
import com.askrepps.advent.advent2021.day22.main as run2021Day22
import com.askrepps.advent.advent2021.day23.main as run2021Day23
import com.askrepps.advent.advent2021.day24.main as run2021Day24
import com.askrepps.advent.advent2021.day25.main as run2021Day25
import com.askrepps.advent.advent2022.day01.main as run2022Day01
import com.askrepps.advent.advent2022.day02.main as run2022Day02
import com.askrepps.advent.advent2022.day03.main as run2022Day03
import com.askrepps.advent.advent2022.day04.main as run2022Day04
import com.askrepps.advent.advent2022.day05.main as run2022Day05
import com.askrepps.advent.advent2022.day06.main as run2022Day06
import com.askrepps.advent.advent2022.day07.main as run2022Day07
import com.askrepps.advent.advent2022.day08.main as run2022Day08
import com.askrepps.advent.advent2022.day09.main as run2022Day09
import com.askrepps.advent.advent2022.day10.main as run2022Day10
import com.askrepps.advent.advent2022.day11.main as run2022Day11
import com.askrepps.advent.advent2022.day12.main as run2022Day12
import com.askrepps.advent.advent2022.day13.main as run2022Day13
import com.askrepps.advent.advent2022.day14.main as run2022Day14
import com.askrepps.advent.advent2022.day15.main as run2022Day15
import com.askrepps.advent.advent2023.day01.main as run2023Day01

private val RUNNERS = mapOf(
    "2021-01" to ::run2021Day01, "2021-02" to ::run2021Day02, "2021-03" to ::run2021Day03, "2021-04" to ::run2021Day04,
    "2021-05" to ::run2021Day05, "2021-06" to ::run2021Day06, "2021-07" to ::run2021Day07, "2021-08" to ::run2021Day08,
    "2021-09" to ::run2021Day09, "2021-10" to ::run2021Day10, "2021-11" to ::run2021Day11, "2021-12" to ::run2021Day12,
    "2021-13" to ::run2021Day13, "2021-14" to ::run2021Day14, "2021-15" to ::run2021Day15, "2021-16" to ::run2021Day16,
    "2021-17" to ::run2021Day17, "2021-18" to ::run2021Day18, "2021-19" to ::run2021Day19, "2021-20" to ::run2021Day20,
    "2021-21" to ::run2021Day21, "2021-22" to ::run2021Day22, "2021-23" to ::run2021Day23, "2021-24" to ::run2021Day24,
    "2021-25" to ::run2021Day25, "2022-01" to ::run2022Day01, "2022-02" to ::run2022Day02, "2022-03" to ::run2022Day03,
    "2022-04" to ::run2022Day04, "2022-05" to ::run2022Day05, "2022-06" to ::run2022Day06, "2022-07" to ::run2022Day07,
    "2022-08" to ::run2022Day08, "2022-09" to ::run2022Day09, "2022-10" to ::run2022Day10, "2022-11" to ::run2022Day11,
    "2022-12" to ::run2022Day12, "2022-13" to ::run2022Day13, "2022-14" to ::run2022Day14, "2022-15" to ::run2022Day15,
    "2023-01" to ::run2023Day01
)

fun runMultiple(predicate: (String) -> Boolean) =
    RUNNERS.keys.sorted().filter(predicate)
        .sumOf { run(it) }

fun run(key: String): Long {
    println("Running $key")
    val elapsedTime = measureTimeMillis {
        RUNNERS[key]?.invoke()
            ?: throw IllegalArgumentException("No runner found for key $key")
    }
    println("Elapsed time: ${elapsedTime.millisecondsToSeconds()} s\n")
    return elapsedTime
}

fun Long.millisecondsToSeconds() =
    toDouble() / 1000.0

fun timeMultipleRuns(predicate: (String) -> Boolean = { true }) {
    val totalTime = runMultiple(predicate)
    println("Total elapsed time: ${totalTime.millisecondsToSeconds()} s")
}

fun main(args: Array<String>) {
    val year = args.firstOrNull()
    val day = args.getOrNull(1)
    if (year == null) {
        println("Running all days\n")
        timeMultipleRuns()
    } else if (day == null) {
        println("Running year $year\n")
        timeMultipleRuns { it.startsWith(year.toString()) }
    } else {
        val dayNumber = day.toIntOrNull()
            ?: throw IllegalArgumentException("Day must be a valid integer")
        val key = "$year-${String.format("%02d", dayNumber)}"
        run(key)
    }
}
