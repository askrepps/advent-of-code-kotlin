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

package com.askrepps.advent.advent2021.day12

import com.askrepps.advent.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test

private const val TEST_DATA_1 = """
start-A
start-b
A-c
A-b
b-d
A-end
b-end
"""

private const val TEST_DATA_2 = """
dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc
"""

private const val TEST_DATA_3 = """
fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW
"""

class Advent2021Day12Test {
    @Test
    fun test2021Day12() {
        runTest(TEST_DATA_1, expectedPart1 = 10, expectedPart2 = 36)
        runTest(TEST_DATA_2, expectedPart1 = 19, expectedPart2 = 103)
        runTest(TEST_DATA_3, expectedPart1 = 226, expectedPart2 = 3509)
    }

    private fun runTest(data: String, expectedPart1: Int, expectedPart2: Int) {
        val caves = data.getInputLines().toCaves()
        assertEquals(expectedPart1, getPart1Answer(caves))
        assertEquals(expectedPart2, getPart2Answer(caves))
    }
}
