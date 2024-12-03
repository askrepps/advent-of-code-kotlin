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

package com.askrepps.advent.advent2024.day03

import com.askrepps.advent.util.getInputLines
import java.io.File

private val MULTIPLY_INSTRUCTION_PATTERN = "(?<mulInstruction>mul)\\((?<firstArg>\\d+),(?<secondArg>\\d+)\\)".toRegex()
private val SWITCH_INSTRUCTION_PATTERN = "(?<switchInstruction>do(n't)?)\\(\\)".toRegex()

private val COMBINED_INSTRUCTION_PATTERN = "$MULTIPLY_INSTRUCTION_PATTERN|$SWITCH_INSTRUCTION_PATTERN".toRegex()

fun List<String>.extractMultiplications(processSwitches: Boolean = false): Iterable<Pair<Long, Long>> {
    var isActive = true
    return flatMap { line ->
        COMBINED_INSTRUCTION_PATTERN.findAll(line).mapNotNull { result ->
            val instruction = result.groups["mulInstruction"]?.value ?: result.groups["switchInstruction"]?.value
            when (instruction) {
                "mul" -> {
                    val firstArg = checkNotNull(result.groups["firstArg"]?.value) { "No firstArg" }
                    val secondArg = checkNotNull(result.groups["secondArg"]?.value) { "No secondArg" }
                    Pair(firstArg.toLong(), secondArg.toLong())
                }
                "do" -> {
                    isActive = true
                    null
                }
                "don't" -> {
                    isActive = !processSwitches
                    null
                }
                else -> null
            }.takeIf {
                isActive
            }
        }
    }
}

fun addMultiplicationResults(lines: List<String>, processSwitches: Boolean) =
    lines.extractMultiplications(processSwitches).sumOf { it.first * it.second }

fun getPart1Answer(lines: List<String>) = addMultiplicationResults(lines, processSwitches = false)

fun getPart2Answer(lines: List<String>) = addMultiplicationResults(lines, processSwitches = true)

fun main() {
    val lines = File("src/main/resources/2024/input-2024-day03.txt")
        .getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
