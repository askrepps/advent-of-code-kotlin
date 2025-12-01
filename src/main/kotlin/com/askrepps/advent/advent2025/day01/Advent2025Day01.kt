/*
 * MIT License
 *
 * Copyright (c) 2025 Andrew Krepps
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

package com.askrepps.advent.advent2025.day01

import com.askrepps.advent.util.getInputLines
import java.io.File

private const val INITIAL_VALUE = 50L
private const val DIAL_SIZE = 100L

enum class Direction(val factor: Long) {
    Left(-1L),
    Right(1L)
}

data class Rotation(val distance: Long, val direction: Direction)

fun String.toRotation(): Rotation {
    val direction = when (val c = get(0)) {
        'L' -> Direction.Left
        'R' -> Direction.Right
        else -> error("Unknown direction '$c'")
    }
    val distance = substring(1).toLong()
    return Rotation(distance, direction)
}

fun Long.countIfZero() = if (this == 0L) 1L else 0L

fun getPart1Answer(rotations: List<Rotation>): Long {
    var value = INITIAL_VALUE
    return rotations.sumOf { rotation ->
        value = (value + rotation.distance * rotation.direction.factor) % DIAL_SIZE
        value.countIfZero()
    }
}

fun getPart2Answer(rotations: List<Rotation>): Long {
    var value = INITIAL_VALUE
    return rotations.sumOf { rotation ->
        (0L until rotation.distance).sumOf {
            value = (value + rotation.direction.factor) % DIAL_SIZE
            value.countIfZero()
        }
    }
}

fun main() {
    val rotations = File("src/main/resources/2025/input-2025-day01.txt")
        .getInputLines().map { it.toRotation() }

    println("The answer to part 1 is ${getPart1Answer(rotations)}")
    println("The answer to part 2 is ${getPart2Answer(rotations)}")
}
