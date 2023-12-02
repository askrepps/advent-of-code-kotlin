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

package com.askrepps.advent.advent2021.day16

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Advent2021Day16Test {
    @Test
    fun test2021Day16() {
        runPart1Test("8A004A801A8002F478", expectedPart1 = 16L)
        runPart1Test("620080001611562C8802118E34", expectedPart1 = 12L)
        runPart1Test("C0015000016115A2E0802F182340", expectedPart1 = 23L)
        runPart1Test("A0016C880162017C3686B18A3D4780", expectedPart1 = 31L)

        runPart2Test("C200B40A82", expectedPart2 = 3L)
        runPart2Test("04005AC33890", expectedPart2 = 54L)
        runPart2Test("880086C3E88112", expectedPart2 = 7L)
        runPart2Test("CE00C43D881120", expectedPart2 = 9L)
        runPart2Test("D8005AC2A8F0", expectedPart2 = 1L)
        runPart2Test("F600BC2D8F", expectedPart2 = 0L)
        runPart2Test("9C005AC2F8F0", expectedPart2 = 0L)
        runPart2Test("9C0141080250320F1802104A08", expectedPart2 = 1L)
    }

    private fun runPart1Test(data: String, expectedPart1: Long) {
        val packet = data.hexToBits().decodePacket()
        assertEquals(expectedPart1, getPart1Answer(packet))
    }

    private fun runPart2Test(data: String, expectedPart2: Long) {
        val packet = data.hexToBits().decodePacket()
        assertEquals(expectedPart2, getPart2Answer(packet))
    }

    @Test
    fun testLiteralPacket() {
        val packet = "D2FE28".hexToBits().decodePacket()
        val expectedPacket = Literal(version = 6, length = 21, value = 2021L)
        verifyLiteralPacket(expectedPacket, packet)
    }

    @Test
    fun testOperatorPacket1() {
        val packet = "38006F45291200".hexToBits().decodePacket()
        val expectedPacket = Operator(
            version = 1,
            typeId = 6,
            length = 49,
            subPackets = listOf(
                Literal(version = 6, length = 11, value = 10L),
                Literal(version = 2, length = 16, value = 20L)
            )
        )
        verifyOperatorPacket(expectedPacket, packet)
    }

    @Test
    fun testOperatorPacket2() {
        val packet = "EE00D40C823060".hexToBits().decodePacket()
        val expectedPacket = Operator(
            version = 7,
            typeId = 3,
            length = 51,
            subPackets = listOf(
                Literal(version = 2, length = 11, value = 1L),
                Literal(version = 4, length = 11, value = 2L),
                Literal(version = 1, length = 11, value = 3L)
            )
        )
        verifyOperatorPacket(expectedPacket, packet)
    }

    private fun verifyLiteralPacket(expected: Literal, actual: Packet) {
        assertTrue(actual is Literal)
        assertEquals(expected.version, actual.version)
        assertEquals(expected.typeId, actual.typeId)
        assertEquals(expected.length, actual.length)
        assertEquals(expected.value, actual.value)
    }

    private fun verifyOperatorPacket(expected: Operator, actual: Packet) {
        assertTrue(actual is Operator)
        assertEquals(expected.version, actual.version)
        assertEquals(expected.typeId, actual.typeId)
        assertEquals(expected.length, actual.length)
        for ((expectedSub, actualSub) in expected.subPackets.zip(actual.subPackets)) {
            if (expectedSub is Literal) {
                verifyLiteralPacket(expectedSub, actualSub)
            } else {
                check(expectedSub is Operator)
                verifyOperatorPacket(expectedSub, actualSub)
            }
        }
    }

    @Test
    fun testHexToBits() {
        val bits = "D2FE28".hexToBits()
        assertEquals(
            listOf(
                '1', '1', '0', '1',
                '0', '0', '1', '0',
                '1', '1', '1', '1',
                '1', '1', '1' ,'0',
                '0', '0', '1', '0',
                '1', '0', '0', '0'
            ),
            bits
        )
    }

    @Test
    fun testBitValue() {
        assertEquals(0, listOf('0', '0', '0', '0').bitValue)
        assertEquals(1, listOf('0', '0', '0', '1').bitValue)
        assertEquals(2, listOf('0', '0', '1', '0').bitValue)
        assertEquals(3, listOf('0', '0', '1', '1').bitValue)
        assertEquals(4, listOf('0', '1', '0', '0').bitValue)
        assertEquals(5, listOf('0', '1', '0', '1').bitValue)
        assertEquals(6, listOf('0', '1', '1', '0').bitValue)
        assertEquals(7, listOf('0', '1', '1', '1').bitValue)
        assertEquals(8, listOf('1', '0', '0', '0').bitValue)
        assertEquals(9, listOf('1', '0', '0', '1').bitValue)
        assertEquals(10, listOf('1', '0', '1', '0').bitValue)
        assertEquals(11, listOf('1', '0', '1', '1').bitValue)
        assertEquals(12, listOf('1', '1', '0', '0').bitValue)
        assertEquals(13, listOf('1', '1', '0', '1').bitValue)
        assertEquals(14, listOf('1', '1', '1', '0').bitValue)
        assertEquals(15, listOf('1', '1', '1', '1').bitValue)
    }
}
