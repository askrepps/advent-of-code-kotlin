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

private val runners = listOf<() -> Unit>(

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
