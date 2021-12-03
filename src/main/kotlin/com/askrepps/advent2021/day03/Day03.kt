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

fun getBitCounts(bitStrings: List<String>, numBits: Int): Pair<List<Int>, List<Int>> {
    val oneCounts = MutableList(numBits) { 0 }
    val zeroCounts = MutableList(numBits) { 0 }
    for (bitString in bitStrings) {
        bitString.forEachIndexed { index, c ->
            if (c == '0') {
                zeroCounts[index]++
            } else {
                oneCounts[index]++
            }
        }
    }

    return Pair(zeroCounts, oneCounts)
}

fun getGammaRate(bitStrings: List<String>, numBits: Int): Int {
    val (zeroCounts, oneCounts) = getBitCounts(bitStrings, numBits)
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

fun getPart1Answer(bitStrings: List<String>, numBits: Int): Int {
    val gammaRate = getGammaRate(bitStrings, numBits)
    val epsilonRate = getEpsilonRate(gammaRate, numBits)
    return gammaRate * epsilonRate
}

fun getPart2Answer(bitStrings: List<String>, numBits: Int): Int {
    var bitIndex = 0
    val oxygenBitStrings = bitStrings.toMutableList()
    while (oxygenBitStrings.size > 1 && bitIndex < numBits) {
        val (zeroCounts, oneCounts) = getBitCounts(oxygenBitStrings, numBits)
        if (zeroCounts[bitIndex] > oneCounts[bitIndex]) {
            oxygenBitStrings.removeAll { it[bitIndex] != '0' }
        } else {
            oxygenBitStrings.removeAll { it[bitIndex] != '1' }
        }
        bitIndex++
    }

    check(oxygenBitStrings.size == 1)
    val oxygenValue = oxygenBitStrings.first().toInt(2)

    bitIndex = 0
    val carbonBitStrings = bitStrings.toMutableList()
    while (carbonBitStrings.size > 1 && bitIndex < numBits) {
        val (zeroCounts, oneCounts) = getBitCounts(carbonBitStrings, numBits)
        if (zeroCounts[bitIndex] > oneCounts[bitIndex]) {
            carbonBitStrings.removeAll { it[bitIndex] == '0' }
        } else {
            carbonBitStrings.removeAll { it[bitIndex] == '1' }
        }
        bitIndex++
    }

    check(carbonBitStrings.size == 1)
    val carbonValue = carbonBitStrings.first().toInt(2)

    return oxygenValue * carbonValue
}

fun main() {
    val bitStrings = File("src/main/resources/day03.txt").getInputLines()
    val numBits = bitStrings.first().length

    println("The answer to part 1 is ${getPart1Answer(bitStrings, numBits)}")
    println("The answer to part 2 is ${getPart2Answer(bitStrings, numBits)}")
}
