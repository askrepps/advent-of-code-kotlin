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
import java.util.*

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

fun List<String>.scoreCorruptedLines() =
    sumOf { line ->
        val stack = Stack<Char>()
        for (c in line) {
            when (c) {
                '(', '[', '{', '<' -> stack.push(c)
                ')' -> {
                    if (stack.isEmpty() || stack.peek() != '(') {
                        return@sumOf ILLEGAL_CHARACTER_SCORES[c] ?: 0L
                    }
                    stack.pop()
                }
                ']' -> {
                    if (stack.isEmpty() || stack.peek() != '[') {
                        return@sumOf ILLEGAL_CHARACTER_SCORES[c] ?: 0L
                    }
                    stack.pop()
                }
                '}' -> {
                    if (stack.isEmpty() || stack.peek() != '{') {
                        return@sumOf ILLEGAL_CHARACTER_SCORES[c] ?: 0L
                    }
                    stack.pop()
                }
                '>' -> {
                    if (stack.isEmpty() || stack.peek() != '<') {
                        return@sumOf ILLEGAL_CHARACTER_SCORES[c] ?: 0L
                    }
                    stack.pop()
                }
            }
        }

        return@sumOf 0L
    }

fun String.isCorrupted(): Boolean {
    val stack = Stack<Char>()
    for (c in this) {
        when (c) {
            '(', '[', '{', '<' -> stack.push(c)
            ')' -> {
                if (stack.isEmpty() || stack.peek() != '(') {
                    return true
                }
                stack.pop()
            }
            ']' -> {
                if (stack.isEmpty() || stack.peek() != '[') {
                    return true
                }
                stack.pop()
            }
            '}' -> {
                if (stack.isEmpty() || stack.peek() != '{') {
                    return true
                }
                stack.pop()
            }
            '>' -> {
                if (stack.isEmpty() || stack.peek() != '<') {
                    return true
                }
                stack.pop()
            }
        }
    }

    return false
}

fun String.scoreLineCompletion(): Long {
    val stack = Stack<Char>()
    for (c in this) {
        when (c) {
            '(', '[', '{', '<' -> stack.push(c)
            ')' -> {
                if (stack.isEmpty() || stack.peek() != '(') {
                    throw IllegalStateException("Corrupt line")
                }
                stack.pop()
            }
            ']' -> {
                if (stack.isEmpty() || stack.peek() != '[') {
                    throw IllegalStateException("Corrupt line")
                }
                stack.pop()
            }
            '}' -> {
                if (stack.isEmpty() || stack.peek() != '{') {
                    throw IllegalStateException("Corrupt line")
                }
                stack.pop()
            }
            '>' -> {
                if (stack.isEmpty() || stack.peek() != '<') {
                    throw IllegalStateException("Corrupt line")
                }
                stack.pop()
            }
        }
    }

    var score = 0L
    while (stack.isNotEmpty()) {
        val c = stack.pop()
        score = score * 5L +
                (COMPLETION_CHARACTER_SCORES[c] ?: throw IllegalStateException("Unexpected stack char '$c'"))
    }

    return score
}

fun getPart1Answer(lines: List<String>) = lines.scoreCorruptedLines()

fun getPart2Answer(lines: List<String>) =
    lines.filter { !it.isCorrupted() }
        .map { it.scoreLineCompletion() }
        .sorted()
        .let { it[it.size / 2] }

fun main() {
    val lines = File("src/main/resources/day10.txt")
        .getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
