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

package com.askrepps.advent2021.day16

import java.io.File

sealed class Packet(val version: Int, val typeId: Int, val length: Int)

class Literal(version: Int, length: Int, val value: Long) : Packet(version, typeId = 4, length)

class Operator(version: Int, typeId: Int, length: Int, val subPackets: List<Packet>) : Packet(version, typeId, length)

fun String.hexToBits() = flatMap {
    val bits = it.digitToInt(16).toString(2).toList()
    List(4 - bits.size) { '0' } + bits
}

val Char.bitValue: Int
    get() = code - '0'.code

val List<Char>.bitValue: Int
    get() = fold(0) { result, bit -> result.shl(1).or(bit.bitValue) }

fun List<Char>.decodePacket(): Packet {
    val version = subList(0, 3).bitValue
    val typeId = subList(3, 6).bitValue
    if (typeId == 4) {
        var valueGroupIndex = 6
        var value = 0L
        do {
            val newPart = subList(valueGroupIndex + 1, valueGroupIndex + 5).bitValue.toLong()
            value = value.shl(4).or(newPart)
            valueGroupIndex += 5
        } while (get(valueGroupIndex - 5).bitValue == 1)
        return Literal(version, valueGroupIndex, value)
    } else {
        val subPacketLengthTypeId = get(6).bitValue
        if (subPacketLengthTypeId == 0) {
            val subPacketLength = subList(7, 22).bitValue
            var subPacketIndex = 22
            val totalLength = subPacketIndex + subPacketLength
            val subPackets = mutableListOf<Packet>()
            while (subPacketIndex < totalLength) {
                val subPacket = subList(subPacketIndex, size).decodePacket()
                subPackets.add(subPacket)
                subPacketIndex += subPacket.length
            }
            return Operator(version, typeId, subPacketIndex, subPackets)
        } else {
            val numSubPackets = subList(7, 18).bitValue
            var subPacketIndex = 18
            val subPackets = mutableListOf<Packet>()
            repeat(numSubPackets) {
                val subPacket = subList(subPacketIndex, size).decodePacket()
                subPackets.add(subPacket)
                subPacketIndex += subPacket.length
            }
            return Operator(version, typeId, subPacketIndex, subPackets)
        }
    }
}

val Packet.versionSum: Long
    get() = version +
        if (this is Operator) {
            subPackets.sumOf { it.versionSum }
        } else {
            0
        }

fun Packet.evaluate(): Long =
    if (this is Literal) {
        value
    } else {
        this as Operator
        when (typeId) {
            0 -> subPackets.sumOf { it.evaluate() }
            1 -> subPackets.fold(1L) { result, packet -> result * packet.evaluate() }
            2 -> subPackets.minOf { it.evaluate() }
            3 -> subPackets.maxOf { it.evaluate() }
            5 -> if (subPackets[0].evaluate() > subPackets[1].evaluate()) 1L else 0L
            6 -> if (subPackets[0].evaluate() < subPackets[1].evaluate()) 1L else 0L
            7 -> if (subPackets[0].evaluate() == subPackets[1].evaluate()) 1L else 0L
            else -> throw IllegalArgumentException("Unrecognized type ID: $typeId")
        }
    }

fun getPart1Answer(topLevelPacket: Packet) =
    topLevelPacket.versionSum

fun getPart2Answer(topLevelPacket: Packet) =
    topLevelPacket.evaluate()

fun main() {
    val topLevelPacket = File("src/main/resources/day16.txt")
        .readText().hexToBits().decodePacket()

    println("The answer to part 1 is ${getPart1Answer(topLevelPacket)}")
    println("The answer to part 2 is ${getPart2Answer(topLevelPacket)}")
}
