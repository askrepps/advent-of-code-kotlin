/*
 * MIT License
 *
 * Copyright (c) 2023 Andrew Krepps
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

package com.askrepps.advent.advent2023.day02

import com.askrepps.advent.util.getInputLines
import java.io.File
import kotlin.math.max

data class Game(
    val id: Int,
    val maxRedCubes: Int,
    val maxGreenCubes: Int,
    val maxBlueCubes: Int
)

fun String.toGame(): Game {
    val (idComponent, mainComponent) = split(": ")
    val id = idComponent.split(" ").last().toInt()

    var maxRed = 0
    var maxGreen = 0
    var maxBlue = 0
    val rounds = mainComponent.split("; ")
    for (round in rounds) {
        val pulls = round.split(", ")
        for (pull in pulls) {
            val (valueString, color) = pull.split(" ")
            val value = valueString.toInt()
            when (color) {
                "red" -> maxRed = max(maxRed, value)
                "green" -> maxGreen = max(maxGreen, value)
                "blue" -> maxBlue = max(maxBlue, value)
                else -> error("Unrecognized color: $color")
            }
        }
    }

    return Game(id, maxRed, maxGreen, maxBlue)
}

fun getPart1Answer(games: List<Game>) =
    games.filter { it.maxRedCubes <= 12 && it.maxGreenCubes <= 13 && it.maxBlueCubes <= 14 }
        .sumOf { it.id }

fun getPart2Answer(games: List<Game>) =
    games.fold(0) { total, game ->
        total + game.maxRedCubes * game.maxGreenCubes * game.maxBlueCubes
    }

fun main() {
    val games = File("src/main/resources/2023/input-2023-day02.txt")
        .getInputLines().map { it.toGame() }

    println("The answer to part 1 is ${getPart1Answer(games)}")
    println("The answer to part 2 is ${getPart2Answer(games)}")
}
