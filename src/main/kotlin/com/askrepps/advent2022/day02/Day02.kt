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

private val winsAgainst = mapOf(
    Move.Rock to Move.Paper,
    Move.Paper to Move.Scissors,
    Move.Scissors to Move.Rock
)

private val losesAgainst = mapOf(
    Move.Rock to Move.Scissors,
    Move.Paper to Move.Rock,
    Move.Scissors to Move.Paper
)

fun Move.getScoreAgainst(other: Move) =
    score + when (this) {
        other -> DRAW_SCORE
        winsAgainst[other] -> WIN_SCORE
        losesAgainst[other] -> LOSE_SCORE
        else -> error("No outcome found for $this and $other")
    }

fun Move.getCounterMoveForOutcome(outcome: Outcome) =
    when (outcome) {
        Outcome.Win -> winsAgainst[this]
        Outcome.Lose -> losesAgainst[this]
        Outcome.Draw -> this
    } ?: error("No counter move found for $this and outcome $outcome")

fun String.toMove() =
    when (this) {
        "A" -> Move.Rock
        "B" -> Move.Paper
        "C" -> Move.Scissors
        "X" -> Move.Rock
        "Y" -> Move.Paper
        "Z" -> Move.Scissors
        else -> error("Unrecognized move code $this")
    }

fun String.toOutcome() =
    when (this) {
        "X" -> Outcome.Lose
        "Y" -> Outcome.Draw
        "Z" -> Outcome.Win
        else -> error("Unrecognized outcome code $this")
    }

fun playGame(lines: List<String>, moveStrategy: (Move, String) -> Move) =
    lines.sumOf { line ->
        val (opponentCode, myCode) = line.split(" ")
        val opponentMove = opponentCode.toMove()
        val myMove = moveStrategy(opponentMove, myCode)
        myMove.getScoreAgainst(opponentMove)
    }

fun getPart1Answer(lines: List<String>) =
    playGame(lines) { _, myCode ->
        myCode.toMove()
    }

fun getPart2Answer(lines: List<String>) =
    playGame(lines) { opponentMove, myCode ->
        opponentMove.getCounterMoveForOutcome(myCode.toOutcome())
    }

fun main() {
    val lines = File("src/main/resources/2022/day02.txt")
        .getInputLines()

    println("The answer to part 1 is ${getPart1Answer(lines)}")
    println("The answer to part 2 is ${getPart2Answer(lines)}")
}
