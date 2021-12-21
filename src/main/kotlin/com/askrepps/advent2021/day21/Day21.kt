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

package com.askrepps.advent2021.day21

import com.askrepps.advent2021.util.getInputLines
import java.io.File
import kotlin.math.max

private const val ROLLS_PER_TURN = 3
private const val NUM_SPACES = 10

private const val DETERMINISTIC_DIE_SIDES = 100
private const val DETERMINISTIC_SCORE_LIMIT = 1000

private const val DIRAC_DIE_SIDES = 3
private const val DIRAC_SCORE_LIMIT = 21

data class DiracGameState(
    val player1Position: Int,
    val player2Position: Int,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val currentPlayer: Int = 1,
    val previousRollTotal: Int = 0,
    val rollsLeftInTurn: Int = ROLLS_PER_TURN
)

data class DiracGameResult(val player1Wins: Long, val player2Wins: Long) {
    operator fun plus(other: DiracGameResult) =
        DiracGameResult(player1Wins + other.player1Wins, player2Wins + other.player2Wins)
}

fun String.toStartingPosition() =
    split(": ").last().toInt()

fun getNextPosition(currentPosition: Int, roll: Int) =
    ((currentPosition + roll - 1) % NUM_SPACES) + 1

fun playDeterministicGame(startingPositions: List<Int>): Pair<List<Int>, Int> {
    var dieValue = 0
    var numRolls = 0
    fun rollDie() = ((dieValue % DETERMINISTIC_DIE_SIDES) + 1).also {
        dieValue = it
        numRolls++
    }

    val currentPositions = startingPositions.toMutableList()
    val scores = MutableList(startingPositions.size) { 0 }
    var currentPlayer = 0
    while ((scores.maxOrNull() ?: 0) < DETERMINISTIC_SCORE_LIMIT) {
        val rollTotal = (1..ROLLS_PER_TURN).sumOf { rollDie() }
        val newPosition = getNextPosition(currentPositions[currentPlayer], rollTotal)
        currentPositions[currentPlayer] = newPosition
        scores[currentPlayer] += newPosition
        currentPlayer = (currentPlayer + 1) % scores.size
    }
    return Pair(scores, numRolls)
}

fun DiracGameState.getNextState(roll: Int): DiracGameState {
    var nextPlayer1Position = player1Position
    var nextPlayer2Position = player2Position
    var nextPlayer1Score = player1Score
    var nextPlayer2Score = player2Score
    var nextPlayer = currentPlayer
    val nextRollTotal: Int
    val nextRollsLeft: Int

    val currentRollTotal = previousRollTotal + roll

    if (rollsLeftInTurn > 1) {
        nextRollsLeft = rollsLeftInTurn - 1
        nextRollTotal = currentRollTotal
    } else {
        if (currentPlayer == 1) {
            val nextPosition = getNextPosition(player1Position, currentRollTotal)
            nextPlayer1Position = nextPosition
            nextPlayer1Score = player1Score + nextPosition
        } else {
            val nextPosition = getNextPosition(player2Position, currentRollTotal)
            nextPlayer2Position = nextPosition
            nextPlayer2Score = player2Score + nextPosition
        }

        nextPlayer = (currentPlayer % 2) + 1
        nextRollsLeft = ROLLS_PER_TURN
        nextRollTotal = 0
    }

    return DiracGameState(
        player1Position = nextPlayer1Position,
        player2Position = nextPlayer2Position,
        player1Score = nextPlayer1Score,
        player2Score = nextPlayer2Score,
        currentPlayer = nextPlayer,
        previousRollTotal = nextRollTotal,
        rollsLeftInTurn = nextRollsLeft
    )
}

fun playDiracGame(
    currentState: DiracGameState,
    resultCache: MutableMap<DiracGameState, DiracGameResult> = mutableMapOf()
): DiracGameResult {
    val result = resultCache[currentState] ?:
        if (currentState.player1Score >= DIRAC_SCORE_LIMIT) {
            DiracGameResult(1L, 0L)
        } else if (currentState.player2Score >= DIRAC_SCORE_LIMIT) {
            DiracGameResult(0L, 1L)
        } else {
            (1..DIRAC_DIE_SIDES).fold(DiracGameResult(0L, 0L)) { result, roll ->
                result + playDiracGame(currentState.getNextState(roll), resultCache)
            }
        }
    resultCache[currentState] = result
    return result
}

fun getPart1Answer(startingPositions: List<Int>): Int {
    val (scores, numRolls) = playDeterministicGame(startingPositions)
    return (scores.minOrNull() ?: 0) * numRolls
}

fun getPart2Answer(startingPositions: List<Int>): Long {
    val initialState = DiracGameState(startingPositions.first(), startingPositions.last())
    val result = playDiracGame(initialState)
    return max(result.player1Wins, result.player2Wins)
}

fun main() {
    val startingPositions = File("src/main/resources/day21.txt")
        .getInputLines().map { it.toStartingPosition() }

    println("The answer to part 1 is ${getPart1Answer(startingPositions)}")
    println("The answer to part 2 is ${getPart2Answer(startingPositions)}")
}
