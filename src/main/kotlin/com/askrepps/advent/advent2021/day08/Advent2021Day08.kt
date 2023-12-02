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

package com.askrepps.advent.advent2021.day08

import com.askrepps.advent.util.getInputLines
import java.io.File

private val SEGMENTS_BY_NUMBER = listOf(
    setOf('a', 'b', 'c', 'e', 'f', 'g'),       // 0
    setOf('c', 'f'),                           // 1
    setOf('a', 'c', 'd', 'e', 'g'),            // 2
    setOf('a', 'c', 'd', 'f', 'g'),            // 3
    setOf('b', 'c', 'd', 'f'),                 // 4
    setOf('a', 'b', 'd', 'f', 'g'),            // 5
    setOf('a', 'b', 'd', 'e', 'f', 'g'),       // 6
    setOf('a', 'c', 'f'),                      // 7
    setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),  // 8
    setOf('a', 'b', 'c', 'd', 'f', 'g'),       // 9
)

private val NUMBER_BY_SEGMENTS =
    SEGMENTS_BY_NUMBER.mapIndexed { value, segments -> segments to value }.toMap()

data class NoteEntry(val signalPatterns: List<String>, val outputDisplays: List<String>)

fun String.toNoteEntry() =
    split(" | ").let { (signalPatterns, outputDisplays) ->
        NoteEntry(signalPatterns.split(" "), outputDisplays.split(" "))
    }

fun getWireMapping(entry: NoteEntry): Map<Char, Char> {
    val allSegments = 'a'..'g'
    val possibleInputWiresBySegment = allSegments.associateWith { allSegments.toMutableSet() }

    // attempt to match input patterns to their value using some values' unique numbers of segments
    val uniqueLengthValuesByLength = mutableMapOf(
        SEGMENTS_BY_NUMBER[1].size to 1,
        SEGMENTS_BY_NUMBER[4].size to 4,
        SEGMENTS_BY_NUMBER[7].size to 7,
        SEGMENTS_BY_NUMBER[8].size to 8
    )
    val patternsByValue = mutableMapOf<Int, Set<Char>>()
    for (pattern in entry.signalPatterns) {
        uniqueLengthValuesByLength[pattern.length]?.let { value ->
            for (outputSegment in SEGMENTS_BY_NUMBER[value]) {
                possibleInputWiresBySegment[outputSegment]?.retainAll(pattern.toSet())
            }
            patternsByValue[value] = pattern.toSet()
        }
    }

    // wire that maps to top of 7 can't be any input wire that maps to the right two segments in a 1
    possibleInputWiresBySegment['a']?.removeAll(patternsByValue[1].orEmpty())

    // single remaining wire that maps to a can't map to any other segments
    for (outputWire in 'b'..'g') {
        possibleInputWiresBySegment[outputWire]?.removeAll(possibleInputWiresBySegment['a'].orEmpty())
    }

    // identical pairs of wires that map to c and f can't map to other segments
    for (outputWire in allSegments - setOf('c', 'f')) {
        possibleInputWiresBySegment[outputWire]?.removeAll(possibleInputWiresBySegment['c'].orEmpty())
    }

    // identical pairs of wires that map to b and d can't map to other segments
    for (outputWire in allSegments - setOf('b', 'd')) {
        possibleInputWiresBySegment[outputWire]?.removeAll(possibleInputWiresBySegment['b'].orEmpty())
    }

    // count appearances of each input wire in the signal patterns
    val inputWiresByCount = allSegments.groupBy { inputWire ->
        entry.signalPatterns.sumOf { pattern -> pattern.count { it == inputWire } }
    }

    // disambiguate b and d (b appears in 6 patterns, d in 7)
    possibleInputWiresBySegment['b']?.retainAll(inputWiresByCount[6].orEmpty().toSet())
    possibleInputWiresBySegment['d']?.retainAll(inputWiresByCount[7].orEmpty().toSet())

    // disambiguate c and f (c appears in 8 patterns, f in 9)
    possibleInputWiresBySegment['c']?.retainAll(inputWiresByCount[8].orEmpty().toSet())
    possibleInputWiresBySegment['f']?.retainAll(inputWiresByCount[9].orEmpty().toSet())

    // disambiguate e and g (e appears in 4 patterns, g in 7)
    possibleInputWiresBySegment['e']?.retainAll(inputWiresByCount[4].orEmpty().toSet())
    possibleInputWiresBySegment['g']?.retainAll(inputWiresByCount[7].orEmpty().toSet())

    // reverse mapping from input wires to output segments for direct lookup during decoding
    return possibleInputWiresBySegment.map { (outputSegment, inputWires) ->
        check(inputWires.size == 1) { "Could not determine input wire for segment $outputSegment" }
        inputWires.first() to outputSegment
    }.toMap()
}

fun decodeOutputValue(entry: NoteEntry, wireMap: Map<Char, Char>) =
    entry.outputDisplays.fold(0) { result, digitDisplay ->
        result * 10 +
            checkNotNull(NUMBER_BY_SEGMENTS[digitDisplay.map { wireMap[it] }.toSet()]) { "Value lookup failed" }
    }

fun getPart1Answer(entries: List<NoteEntry>): Int {
    val uniqueValueLengths = setOf(1, 4, 7, 8).map { SEGMENTS_BY_NUMBER[it].size }.toSet()
    return entries.sumOf { entry -> entry.outputDisplays.count { it.length in uniqueValueLengths } }
}

fun getPart2Answer(entries: List<NoteEntry>) =
    entries.sumOf { decodeOutputValue(it, getWireMapping(it)) }

fun main() {
    val entries = File("src/main/resources/day08.txt")
        .getInputLines().map { it.toNoteEntry() }

    println("The answer to part 1 is ${getPart1Answer(entries)}")
    println("The answer to part 2 is ${getPart2Answer(entries)}")
}
