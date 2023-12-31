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

package com.askrepps.advent.advent2022.day06

import kotlin.test.assertEquals
import kotlin.test.Test

class Advent2022Day06Test {
    @Test
    fun test2022Day06() {
        assertEquals(5, getPart1Answer("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        assertEquals(6, getPart1Answer("nppdvjthqldpwncqszvftbrmjlhg"))
        assertEquals(10, getPart1Answer("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        assertEquals(11, getPart1Answer("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))

        assertEquals(19, getPart2Answer("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        assertEquals(23, getPart2Answer("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        assertEquals(23, getPart2Answer("nppdvjthqldpwncqszvftbrmjlhg"))
        assertEquals(29, getPart2Answer("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        assertEquals(26, getPart2Answer("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
    }
}
