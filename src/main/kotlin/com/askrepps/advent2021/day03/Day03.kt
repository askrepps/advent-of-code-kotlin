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

package com.askrepps.advent2021.day03

import com.askrepps.advent2021.util.getInputLines
import java.io.File

fun List<String>.getBitCounts(numBits: Int): Pair<List<Int>, List<Int>> {
    val zeroCounts = MutableList(numBits) { 0 }
    for (bitString in this) {
        bitString.forEachIndexed { index, c ->
            if (c == '0') {
                zeroCounts[index]++
            }
        }
    }

    val numStrings = size
    val oneCounts = zeroCounts.map { numStrings - it }
    return Pair(zeroCounts, oneCounts)
}

fun List<String>.getGammaRate(numBits: Int): Int {
    val (zeroCounts, oneCounts) = getBitCounts(numBits)
    var gammaRate = 0
    var bit = 1
    repeat(numBits) {
        // bit 0 is MSB of value
        val bitIndex = numBits - it - 1
        if (oneCounts[bitIndex] > zeroCounts[bitIndex]) {
            gammaRate = gammaRate.or(bit)
        }
        bit = bit.shl(1)
    }
    return gammaRate
}

fun getEpsilonRate(gammaRate: Int, numBits: Int): Int {
    var mask = 0
    var bit = 1
    repeat(numBits) {
        mask = mask.or(bit)
        bit = bit.shl(1)
    }
    return gammaRate.xor(mask)
}

fun List<String>.getFilteredValue(numBits: Int, mostCommonBitStays: Boolean): Int {
    val (zeroCounts, oneCounts) = getBitCounts(numBits).let {
        Pair(it.first.toMutableList(), it.second.toMutableList())
    }
    val remainingBitStrings = toMutableList()
    var filterBitIndex = 0
    while (remainingBitStrings.size > 1 && filterBitIndex < numBits) {
        val mostCommonBit =
            if (zeroCounts[filterBitIndex] > oneCounts[filterBitIndex]) {
                '0'
            } else {
                '1'
            }
        remainingBitStrings.removeAll { bitString ->
            val isRemoved = (bitString[filterBitIndex] == mostCommonBit).xor(mostCommonBitStays)
            if (isRemoved) {
                bitString.forEachIndexed { index, c ->
                    if (c == '0') {
                        zeroCounts[index]--
                    } else {
                        oneCounts[index]--
                    }
                }
            }
            return@removeAll isRemoved
        }
        filterBitIndex++
    }

    check(remainingBitStrings.size == 1) { "Filtering process did not result in one value" }
    return remainingBitStrings.first().toInt(radix = 2)
}

fun getPart1Answer(bitStrings: List<String>, numBits: Int): Int {
    val gammaRate = bitStrings.getGammaRate(numBits)
    val epsilonRate = getEpsilonRate(gammaRate, numBits)
    return gammaRate * epsilonRate
}

fun getPart2Answer(bitStrings: List<String>, numBits: Int): Int {
    val oxygenGeneratorRating = bitStrings.getFilteredValue(numBits, mostCommonBitStays = true)
    val carbonDioxideScrubberRating = bitStrings.getFilteredValue(numBits, mostCommonBitStays = false)
    return oxygenGeneratorRating * carbonDioxideScrubberRating
}

fun main() {
    val bitStrings = File("src/main/resources/day03.txt").getInputLines()
    val numBits = bitStrings.first().length

    println("The answer to part 1 is ${getPart1Answer(bitStrings, numBits)}")
    println("The answer to part 2 is ${getPart2Answer(bitStrings, numBits)}")
}
