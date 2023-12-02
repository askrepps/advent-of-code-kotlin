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

package com.askrepps.advent.advent2022.day12

import com.askrepps.advent.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test

private const val TEST_DATA = """
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi
"""

class Day12Test {
    @Test
    fun testDay12() {
        val (heightMap, start, end) = TEST_DATA.getInputLines().toInputData()
        val distances = findDistancesToEnd(heightMap, end)

        assertEquals(31, getPart1Answer(distances, start))
        assertEquals(29, getPart2Answer(heightMap, distances))
    }
}
