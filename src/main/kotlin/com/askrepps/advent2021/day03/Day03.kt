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

data class BitCounts(val zeroCounts: List<Int>, val oneCounts: List<Int>) {
    constructor(zeroCounts: List<Int>, numStrings: Int) : this(zeroCounts, zeroCounts.map { numStrings - it })

    init {
        check(zeroCounts.size == oneCounts.size) { "counts must have the same length" }
    }

    val numBits: Int
        get() = zeroCounts.size
}

fun List<String>.getBitCounts(): BitCounts {
    val numBits = first().length
    val zeroCounts = MutableList(numBits) { 0 }
    for (bitString in this) {
        bitString.forEachIndexed { index, c ->
            if (c == '0') {
                zeroCounts[index]++
            }
        }
    }
    return BitCounts(zeroCounts, numStrings = size)
}

fun getGammaRate(bitCounts: BitCounts): Int {
    val (zeroCounts, oneCounts) = bitCounts
    val numBits = bitCounts.numBits

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

// flip each bit of the gamma rate (ensure mask is left-padded with 0s)
fun getEpsilonRate(gammaRate: Int, numBits: Int) =
    gammaRate.xor((-1).ushr(Int.SIZE_BITS - numBits))

fun List<String>.getFilteredValue(bitCounts: BitCounts, mostCommonBitStays: Boolean): Int {
    val zeroCounts = bitCounts.zeroCounts.toMutableList()
    val oneCounts = bitCounts.oneCounts.toMutableList()
    val numBits = bitCounts.numBits

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

fun getPart1Answer(bitCounts: BitCounts): Int {
    val gammaRate = getGammaRate(bitCounts)
    val epsilonRate = getEpsilonRate(gammaRate, bitCounts.numBits)
    return gammaRate * epsilonRate
}

fun getPart2Answer(bitStrings: List<String>, bitCounts: BitCounts): Int {
    val oxygenGeneratorRating = bitStrings.getFilteredValue(bitCounts, mostCommonBitStays = true)
    val carbonDioxideScrubberRating = bitStrings.getFilteredValue(bitCounts, mostCommonBitStays = false)
    return oxygenGeneratorRating * carbonDioxideScrubberRating
}

fun main() {
    val bitStrings = File("src/main/resources/day03.txt").getInputLines()
    val bitCounts = bitStrings.getBitCounts()

    println("The answer to part 1 is ${getPart1Answer(bitCounts)}")
    println("The answer to part 2 is ${getPart2Answer(bitStrings, bitCounts)}")
}
