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

package com.askrepps.advent2021.day10

import com.askrepps.advent2021.util.getInputLines
import java.io.File

private val OPENING_CHARACTERS = setOf('(', '[', '{', '<')

private val MATCHING_CHARACTERS = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<'
)

private val ILLEGAL_CHARACTER_SCORES = mapOf(
    ')' to 3L,
    ']' to 57L,
    '}' to 1197L,
    '>' to 25137L
)

private val COMPLETION_CHARACTER_SCORES = mapOf(
    '(' to 1L,
    '[' to 2L,
    '{' to 3L,
    '<' to 4L
)

data class LineResult(val corruptionScore: Long?, val stackState: List<Char>) {
    val isCorrupt: Boolean
        get() = corruptionScore != null
}

fun String.validateLine(): LineResult {
    val stack = mutableListOf<Char>()
    for (c in this) {
        when (c) {
            in OPENING_CHARACTERS -> stack.add(c)
            else -> {
                if (stack.isEmpty() || stack.removeLast() != MATCHING_CHARACTERS[c]) {
                    val corruptionScore = ILLEGAL_CHARACTER_SCORES[c]
                        ?: throw IllegalStateException("Unexpected char '$c'")
                    return LineResult(corruptionScore, stack)
                }
            }
        }
    }

    return LineResult(corruptionScore = null, stack)
}

fun List<String>.partitionResults() =
    map { it.validateLine() }
        .partition { it.isCorrupt }

fun LineResult.scoreLineCompletion() =
    stackState.foldRight(0L) { c, score ->
        val value = COMPLETION_CHARACTER_SCORES[c]
            ?: throw IllegalStateException("Unexpected stack char '$c'")
        score * 5L + value
    }

fun getPart1Answer(corruptResults: List<LineResult>) =
    corruptResults.sumOf { it.corruptionScore ?: 0L }

fun getPart2Answer(incompleteResults: List<LineResult>) =
    incompleteResults.map { it.scoreLineCompletion() }
        .sorted()
        .let { it[it.size / 2] }

fun main() {
    val (corruptResults, incompleteResults) = File("src/main/resources/day10.txt")
        .getInputLines()
        .partitionResults()

    println("The answer to part 1 is ${getPart1Answer(corruptResults)}")
    println("The answer to part 2 is ${getPart2Answer(incompleteResults)}")
}
