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

package com.askrepps.advent.advent2021.day02

import com.askrepps.advent.util.getInputLines
import java.io.File

enum class Direction { Forward, Up, Down }

data class Position(val x: Int, val depth: Int, val aim: Int = 0)

data class MovementCommand(val direction: Direction, val amount: Int) {
    val deltaX: Int
        get() = if (direction == Direction.Forward) {
            amount
        } else {
            0
        }

    val deltaDepth: Int
        get() = when(direction) {
            Direction.Up -> -amount
            Direction.Down -> amount
            else -> 0
        }
}

fun String.toMovementCommand(): MovementCommand {
    val (directionString, amountString) = split(" ")
    val direction = when (directionString) {
        "forward" -> Direction.Forward
        "up" -> Direction.Up
        "down" -> Direction.Down
        else -> throw IllegalArgumentException("Unrecognized direction: $directionString")
    }
    return MovementCommand(direction, amountString.toInt())
}

fun plotCourse(commands: List<MovementCommand>, makeMove: (Position, MovementCommand) -> Position) =
    commands.fold(initial = Position(x = 0, depth = 0), makeMove)

fun getResult(commands: List<MovementCommand>, makeMove: (Position, MovementCommand) -> Position) =
    plotCourse(commands, makeMove).let { finalPosition -> finalPosition.x * finalPosition.depth }

fun getPart1Answer(commands: List<MovementCommand>) =
    getResult(commands) { position, command ->
        Position(position.x + command.deltaX, position.depth + command.deltaDepth)
    }

fun getPart2Answer(commands: List<MovementCommand>) =
    getResult(commands) { position, command ->
        val newX = position.x + command.deltaX
        if (command.direction == Direction.Forward) {
            val newDepth = position.depth + position.aim * command.deltaX
            Position(newX, newDepth, position.aim)
        } else {
            val newAim = position.aim + command.deltaDepth
            Position(newX, position.depth, newAim)
        }
    }

fun main() {
    val commands = File("src/main/resources/day02.txt")
        .getInputLines().map { it.toMovementCommand() }

    println("The answer to part 1 is ${getPart1Answer(commands)}")
    println("The answer to part 2 is ${getPart2Answer(commands)}")
}
