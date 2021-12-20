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

package com.askrepps.advent2021.day20

import com.askrepps.advent2021.util.getInputLines
import java.io.File

private val KERNEL_DIRECTIONS = listOf(
    Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
    Pair(0, -1),  Pair(0, 0),  Pair(0, 1),
    Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
)

data class GridCoordinates(val row: Int, val col: Int)

fun List<String>.toImageData(): Pair<List<Char>, Set<GridCoordinates>> {
    val enhancementData = get(0).toList()
    check(enhancementData.first() == '.' || enhancementData.last() == '.') {
        "Enhanced image will have infinite lit pixels"
    }

    val image = (1 until size).flatMap { lineIndex ->
        val row = lineIndex - 1
        get(lineIndex).mapIndexedNotNull { col, pixel ->
            if (pixel == '#') {
                GridCoordinates(row, col)
            } else {
                null
            }
        }
    }.toSet()

    return Pair(enhancementData, image)
}

fun GridCoordinates.getKernelPixels() =
    KERNEL_DIRECTIONS.map { (deltaRow, deltaCol) ->
        GridCoordinates(row + deltaRow, col + deltaCol)
    }

fun Set<GridCoordinates>.getOutputPixels() =
    flatMap { it.getKernelPixels() }.toSet()

fun GridCoordinates.checkOutputPixel(
    enhancementData: List<Char>,
    inputPixels: Set<GridCoordinates>,
    inputFlipped: Boolean,
    flipOutput: Boolean
): Boolean {
    val enhancementIndex = getKernelPixels().fold(0) { result, pixel ->
        result.shl(1) + if (inputFlipped.xor(pixel in inputPixels)) 1 else 0
    }
    return flipOutput.xor(enhancementData[enhancementIndex] == '#')
}

fun Set<GridCoordinates>.enhanceOnce(enhancementData: List<Char>, inputFlipped: Boolean, flipOutput: Boolean) =
    getOutputPixels().filter {
        it.checkOutputPixel(enhancementData, this, inputFlipped, flipOutput)
    }.toSet()

fun Set<GridCoordinates>.enhance(enhancementData: List<Char>, iterations: Int): Set<GridCoordinates> {
    val flippingRequired = enhancementData.first() == '#'
    check (!flippingRequired || iterations % 2 == 0) {
        "Enhancement data that requires flipping needs a even number of iterations"
    }
    return (0 until iterations).fold(this) { image, step ->
        val inputFlipped = flippingRequired && step % 2 == 1
        val flipOutput = flippingRequired && !inputFlipped
        image.enhanceOnce(enhancementData, inputFlipped, flipOutput)
    }
}

fun getPart1Answer(enhancementData: List<Char>, image: Set<GridCoordinates>) =
    image.enhance(enhancementData, iterations = 2).size

fun getPart2Answer(enhancementData: List<Char>, image: Set<GridCoordinates>) =
    image.enhance(enhancementData, iterations = 50).size

fun main() {
    val (enhancementData, image) = File("src/main/resources/day20.txt")
        .getInputLines().toImageData()

    println("The answer to part 1 is ${getPart1Answer(enhancementData, image)}")
    println("The answer to part 2 is ${getPart2Answer(enhancementData, image)}")
}
