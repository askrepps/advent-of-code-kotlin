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

package com.askrepps.advent.advent2022.day06

import java.io.File

fun getUniqueRunLength(input: String, startIndex: Int, endIndex: Int): Int {
    val encounteredCharacters = mutableSetOf<Char>()
    for (currentCharacter in input.subSequence(startIndex, endIndex)) {
        if (currentCharacter in encounteredCharacters) {
            break
        }
        encounteredCharacters.add(currentCharacter)
    }
    return encounteredCharacters.size
}

fun findPostMarkerIndex(input: String, markerLength: Int) =
    (markerLength until input.length).find { endIndex ->
        getUniqueRunLength(input, endIndex - markerLength, endIndex) == markerLength
    } ?: error("No marker found")

fun getPart1Answer(input: String) =
    findPostMarkerIndex(input, markerLength = 4)

fun getPart2Answer(input: String) =
    findPostMarkerIndex(input, markerLength = 14)

fun main() {
    val input = File("src/main/resources/2022/input-2022-day06.txt")
        .readText()

    println("The answer to part 1 is ${getPart1Answer(input)}")
    println("The answer to part 2 is ${getPart2Answer(input)}")
}
