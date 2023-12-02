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

package com.askrepps.advent.advent2021.day13

import com.askrepps.advent.util.getInputLines
import java.io.File

enum class Dimension { X, Y }

data class GraphCoordinates(val x: Int, val y: Int)

data class FoldCommand(val dimension: Dimension, val value: Int)

fun List<String>.toInstructions(): Pair<Set<GraphCoordinates>, List<FoldCommand>> {
    val dots = mutableSetOf<GraphCoordinates>()
    val folds = mutableListOf<FoldCommand>()

    for (line in this) {
        if (line.startsWith("fold along")) {
            val (dimensionString, valueString) = line.split("=")
            val dimension = when (dimensionString.last()) {
                'x' -> Dimension.X
                'y' -> Dimension.Y
                else -> throw RuntimeException("Unexpected fold dimension: $dimensionString")
            }
            folds.add(FoldCommand(dimension, valueString.toInt()))
        } else {
            val (x, y) = line.split(",")
            dots.add(GraphCoordinates(x.toInt(), y.toInt()))
        }
    }

    return Pair(dots, folds)
}

fun foldDots(dots: Set<GraphCoordinates>, fold: FoldCommand): Set<GraphCoordinates> {
    val newDots = mutableSetOf<GraphCoordinates>()
    for (dot in dots) {
        val willReflect = when (fold.dimension) {
            Dimension.X -> dot.x > fold.value
            Dimension.Y -> dot.y > fold.value
        }
        if (willReflect) {
            val newX = when (fold.dimension) {
                Dimension.X -> fold.value - (dot.x - fold.value)
                Dimension.Y -> dot.x
            }
            val newY = when (fold.dimension) {
                Dimension.X -> dot.y
                Dimension.Y -> fold.value - (dot.y - fold.value)
            }
            newDots.add(GraphCoordinates(newX, newY))
        } else {
            newDots.add(dot)
        }
    }
    return newDots
}

fun Set<GraphCoordinates>.toGraphString(): String {
    val xRange = minOf { it.x }..maxOf { it.x }
    val yRange = minOf { it.y }..maxOf { it.y }

    val sb = StringBuilder()
    for (y in yRange) {
        for (x in xRange) {
            val cell =
                if (GraphCoordinates(x, y) in this) {
                    "#"
                } else {
                    "."
                }
            sb.append(cell)
        }
        if (y < yRange.last) {
            sb.append('\n')
        }
    }
    return sb.toString()
}

fun getPart1Answer(dots: Set<GraphCoordinates>, folds: List<FoldCommand>) =
    foldDots(dots, folds.first()).size

fun getPart2Answer(initialDots: Set<GraphCoordinates>, folds: List<FoldCommand>) =
    folds.fold(initialDots) { dots, fold -> foldDots(dots, fold) }.toGraphString()

fun main() {
    val (dots, folds) = File("src/main/resources/2021/input-2021-day13.txt")
        .getInputLines().toInstructions()

    println("The answer to part 1 is ${getPart1Answer(dots, folds)}")
    println("The answer to part 2 is\n${getPart2Answer(dots, folds)}")
}
