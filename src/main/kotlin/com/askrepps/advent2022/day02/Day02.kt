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

package com.askrepps.advent2022.day02

import com.askrepps.advent2022.util.getInputLines
import java.io.File

private const val ROCK_SCORE = 1
private const val PAPER_SCORE = 2
private const val SCISSORS_SCORE = 3

private const val WIN_SCORE = 6
private const val DRAW_SCORE = 3
private const val LOSE_SCORE = 0

enum class Move(val score: Int) {
    Rock(ROCK_SCORE),
    Paper(PAPER_SCORE),
    Scissors(SCISSORS_SCORE)
}

enum class Outcome { Win, Lose, Draw }

fun Move.getScoreAgainst(other: Move) =
    score + when (this) {
        Move.Rock -> when (other) {
            Move.Rock -> DRAW_SCORE
            Move.Paper -> LOSE_SCORE
            Move.Scissors -> WIN_SCORE
        }
        Move.Paper -> when (other) {
            Move.Rock -> WIN_SCORE
            Move.Paper -> DRAW_SCORE
            Move.Scissors -> LOSE_SCORE
        }
        Move.Scissors -> when (other) {
            Move.Rock -> LOSE_SCORE
            Move.Paper -> WIN_SCORE
            Move.Scissors -> DRAW_SCORE
        }
    }

fun Move.getCounterMoveForOutcome(outcome: Outcome) =
    when (this) {
        Move.Rock -> when (outcome) {
            Outcome.Win -> Move.Paper
            Outcome.Lose -> Move.Scissors
            Outcome.Draw -> Move.Rock
        }
        Move.Paper -> when (outcome) {
            Outcome.Win -> Move.Scissors
            Outcome.Lose -> Move.Rock
            Outcome.Draw -> Move.Paper
        }
        Move.Scissors -> when (outcome) {
            Outcome.Win -> Move.Rock
            Outcome.Lose -> Move.Paper
            Outcome.Draw -> Move.Scissors
        }
    }

private val moveMapping = mapOf(
    "A" to Move.Rock,
    "B" to Move.Paper,
    "C" to Move.Scissors,
    "X" to Move.Rock,
    "Y" to Move.Paper,
    "Z" to Move.Scissors
)

private val outcomeMapping = mapOf(
    "X" to Outcome.Lose,
    "Y" to Outcome.Draw,
    "Z" to Outcome.Win
)

fun String.parseComponents() = split(" ")

fun getPart1Answer(lines: List<String>) =
    lines.sumOf { line ->
        val (opponent, me) = line.parseComponents()
        val opponentMove = requireNotNull(moveMapping[opponent])
        val myMove = requireNotNull(moveMapping[me])
        myMove.getScoreAgainst(opponentMove)
    }

fun getPart2Answer(lines: List<String>) =
    lines.sumOf { line ->
        val (opponent, me) = line.parseComponents()
        val opponentMove = requireNotNull(moveMapping[opponent])
        val myMove = opponentMove.getCounterMoveForOutcome(requireNotNull(outcomeMapping[me]))
        myMove.getScoreAgainst(opponentMove)
    }

fun main() {
    val lines = File("src/main/resources/2022/day02.txt")
        .getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
