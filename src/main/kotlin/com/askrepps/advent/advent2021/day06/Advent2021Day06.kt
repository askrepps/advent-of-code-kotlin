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

package com.askrepps.advent.advent2021.day06

import java.io.File

private const val NORMAL_TIMER_VALUE = 6
private const val FIRST_TIMER_VALUE = NORMAL_TIMER_VALUE + 2

fun <K> MutableMap<K, Long>.incrementAtKeyBy(key: K, value: Long?) =
    put(key, (get(key) ?: 0L) + (value ?: 0L))

fun countFish(initialState: List<Int>, numDays: Int): Long {
    var timerCounts = mutableMapOf<Int, Long>()
    for (timerValue in initialState) {
        timerCounts.incrementAtKeyBy(timerValue, 1L)
    }
    repeat(numDays) {
        val nextTimerCounts = mutableMapOf<Int, Long>()
        for (timerValue in timerCounts.keys) {
            if (timerValue > 0) {
                nextTimerCounts.incrementAtKeyBy(timerValue - 1, timerCounts[timerValue])
            } else {
                nextTimerCounts.incrementAtKeyBy(FIRST_TIMER_VALUE, timerCounts[0])
                nextTimerCounts.incrementAtKeyBy(NORMAL_TIMER_VALUE, timerCounts[0])
            }
        }
        timerCounts = nextTimerCounts
    }
    return timerCounts.values.sum()
}

fun getPart1Answer(initialState: List<Int>) =
    countFish(initialState, numDays = 80)

fun getPart2Answer(initialState: List<Int>) =
    countFish(initialState, numDays = 256)

fun main() {
    val initialState = File("src/main/resources/2021/input-2021-day06.txt")
        .readText().trim().split(",").map { it.toInt() }

    println("The answer to part 1 is ${getPart1Answer(initialState)}")
    println("The answer to part 2 is ${getPart2Answer(initialState)}")
}
