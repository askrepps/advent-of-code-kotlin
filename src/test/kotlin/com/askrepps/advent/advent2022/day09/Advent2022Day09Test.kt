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

package com.askrepps.advent.advent2022.day09

import com.askrepps.advent.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test

private const val TEST_DATA_1 = """
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
"""

private const val TEST_DATA_2 = """
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20
"""

class Advent2022Day09Test {
    @Test
    fun test2022Day09() {
        val commands1 = TEST_DATA_1.getInputLines().map { it.toCommand() }
        assertEquals(13, getPart1Answer(commands1))
        assertEquals(1, getPart2Answer(commands1))

        val commands2 = TEST_DATA_2.getInputLines().map { it.toCommand() }
        assertEquals(36, getPart2Answer(commands2))
    }
}
