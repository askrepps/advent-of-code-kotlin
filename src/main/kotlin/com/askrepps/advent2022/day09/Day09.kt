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

package com.askrepps.advent2022.day09

import com.askrepps.advent2022.util.getInputLines
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Command(val direction: Direction, val count: Int)

data class Coordinates(val x: Int, val y: Int)

fun String.toDirection() =
    when (this) {
        "U" -> Direction.UP
        "D" -> Direction.DOWN
        "L" -> Direction.LEFT
        "R" -> Direction.RIGHT
        else -> error("Unrecognized direction: $this")
    }

fun String.toCommand() =
    split(" ").let { (directionStr, countStr) ->
        Command(directionStr.toDirection(), countStr.toInt())
    }

fun Coordinates.move(direction: Direction): Coordinates {
    val newX =
        when (direction) {
            Direction.UP -> x
            Direction.DOWN -> x
            Direction.LEFT -> x - 1
            Direction.RIGHT -> x + 1
        }
    val newY =
        when (direction) {
            Direction.UP -> y + 1
            Direction.DOWN -> y - 1
            Direction.LEFT -> y
            Direction.RIGHT -> y
        }
    return Coordinates(newX, newY)
}

fun Int.clamp(minValue: Int, maxValue: Int) =
    max(min(this, maxValue), minValue)

fun Coordinates.moveToward(head: Coordinates): Coordinates {
    val dx = head.x - x
    val dy = head.y - y
    if ((dx == 0 && abs(dy) <= 1) || (dy == 0 && abs(dx) <= 1) || (dx != 0 && dy != 0 && abs(dx) + abs(dy) <= 2)) {
        return this
    }
    return Coordinates(x + dx.clamp(-1, 1), y + dy.clamp(-1, 1))
}

fun simulateTailMovement(headCommands: List<Command>, length: Int): Int {
    val rope = MutableList(length) { Coordinates(0, 0) }
    val tailPositions = mutableSetOf(rope.last())
    for (command in headCommands) {
        repeat(command.count) {
            rope[0] = rope[0].move(command.direction)
            for (idx in 1 until rope.size) {
                rope[idx] = rope[idx].moveToward(rope[idx - 1])
            }
            tailPositions.add(rope.last())
        }
    }
    return tailPositions.size
}

fun getPart1Answer(commands: List<Command>) =
    simulateTailMovement(commands, length = 2)

fun getPart2Answer(commands: List<Command>) =
    simulateTailMovement(commands, length = 10)

fun main() {
    val commands = File("src/main/resources/2022/day09.txt")
        .getInputLines().map { it.toCommand() }

    println("The answer to part 1 is ${getPart1Answer(commands)}")
    println("The answer to part 2 is ${getPart2Answer(commands)}")
}
