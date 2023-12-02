/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Andrew Krepps
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

package com.askrepps.advent.advent2022.day09

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.abs
import kotlin.math.sign

enum class Direction(val deltaX: Int, val deltaY: Int) {
    Up(0, 1),
    Down(0, -1),
    Left(-1, 0),
    Right(1, 0)
}

data class Command(val direction: Direction, val count: Int)

data class Coordinates(val x: Int, val y: Int)

fun String.toDirection() =
    when (this) {
        "U" -> Direction.Up
        "D" -> Direction.Down
        "L" -> Direction.Left
        "R" -> Direction.Right
        else -> error("Unrecognized direction: $this")
    }

fun String.toCommand() =
    split(" ").let { (directionStr, countStr) ->
        Command(directionStr.toDirection(), countStr.toInt())
    }

fun Coordinates.move(direction: Direction) =
    Coordinates(x + direction.deltaX, y + direction.deltaY)

fun Coordinates.moveToward(head: Coordinates): Coordinates {
    val deltaX = head.x - x
    val deltaY = head.y - y
    val manhattanDistance = abs(deltaX) + abs(deltaY)

    // tail is already orthogonally adjacent (or overlapping)
    if (manhattanDistance <= 1) {
        return this
    }

    // tail is already diagonally adjacent
    if (deltaX != 0 && deltaY != 0 && manhattanDistance <= 2) {
        return this
    }

    // restrict to king's move
    return Coordinates(x + deltaX.sign, y + deltaY.sign)
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
    val commands = File("src/main/resources/2022/input-2022-day09.txt")
        .getInputLines().map { it.toCommand() }

    println("The answer to part 1 is ${getPart1Answer(commands)}")
    println("The answer to part 2 is ${getPart2Answer(commands)}")
}
