/*
 * MIT License
 *
 * Copyright (c) 2023 Andrew Krepps
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

package com.askrepps.advent.advent2023.day01

import com.askrepps.advent.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test

private const val TEST_DATA_1 = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
"""

private const val TEST_DATA_2 = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
"""

class Advent2023Day01Test {
    @Test
    fun test2023Day01() {
        val lines1 = TEST_DATA_1.getInputLines()
        val lines2 = TEST_DATA_2.getInputLines()

        assertEquals(142, getPart1Answer(lines1))
        assertEquals(281, getPart2Answer(lines2))
    }
}
