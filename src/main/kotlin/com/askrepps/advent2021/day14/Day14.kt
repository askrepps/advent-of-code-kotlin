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

package com.askrepps.advent2021.day14

import com.askrepps.advent2021.util.getInputLines
import java.io.File

data class InsertionRule(val inputPair: String, val newElement: String, val outputPairs: List<String>)

fun <K> MutableMap<K, Long>.incrementAtKeyBy(key: K, value: Long?) =
    put(key, (get(key) ?: 0L) + (value ?: 0L))

fun List<String>.toPolymerDescription(): Pair<MutableMap<String, Long>, List<InsertionRule>> {
    val polymer = get(0)

    val rules = (1 until size).map { index ->
        val (input, result) = get(index).split(" -> ")
        InsertionRule(input, result, listOf("${input[0]}$result", "$result${input[1]}"))
    }

    val initialState = mutableMapOf<String, Long>()
    polymer.forEachIndexed { index, c ->
        val element = c.toString()
        initialState.incrementAtKeyBy(element, 1L)
        if (index > 0) {
            val pair = "${polymer[index - 1]}$c"
            initialState.incrementAtKeyBy(pair, 1L)
        }
    }

    return Pair(initialState, rules)
}

fun List<InsertionRule>.applyTo(polymerState: MutableMap<String, Long>) {
    val changes = mutableMapOf<String, Long>()
    for (rule in this) {
        val matchingPairs = polymerState[rule.inputPair] ?: 0L
        changes.incrementAtKeyBy(rule.newElement, matchingPairs)
        for (output in rule.outputPairs) {
            changes.incrementAtKeyBy(output, matchingPairs)
        }
        changes.incrementAtKeyBy(rule.inputPair, -matchingPairs)
    }
    for ((key, change) in changes) {
        polymerState.incrementAtKeyBy(key, change)
    }
}

fun getResult(polymerState: MutableMap<String, Long>, rules: List<InsertionRule>, iterations: Int): Long {
    repeat(iterations) {
        rules.applyTo(polymerState)
    }
    val elementCounts = polymerState.filterKeys { it.length == 1 }.values
    val minCount = elementCounts.minOrNull() ?: throw RuntimeException("Could not determine min count")
    val maxCount = elementCounts.maxOrNull() ?: throw RuntimeException("Could not determine max count")
    return maxCount - minCount
}

fun getPart1Answer(polymerState: MutableMap<String, Long>, rules: List<InsertionRule>) =
    getResult(polymerState, rules, iterations = 10)

fun getPart2Answer(polymerState: MutableMap<String, Long>, rules: List<InsertionRule>) =
    getResult(polymerState, rules, iterations = 30)  // additional iterations

fun main() {
    val (polymerState, rules) = File("src/main/resources/day14.txt")
        .getInputLines().toPolymerDescription()

    println("The answer to part 1 is ${getPart1Answer(polymerState, rules)}")
    println("The answer to part 2 is ${getPart2Answer(polymerState, rules)}")
}
