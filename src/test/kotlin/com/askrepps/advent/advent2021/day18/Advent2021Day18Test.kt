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

package com.askrepps.advent.advent2021.day18

import com.askrepps.advent.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue

private const val TEST_DATA = """
[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
"""

class Advent2021Day18Test {
    @Test
    fun test2021Day18() {
        val numbers = TEST_DATA.getInputLines().map { it.toSnailfishNumber() }
        assertEquals(4140L, getPart1Answer(numbers))
        assertEquals(3993L, getPart2Answer(numbers))
    }

    @Test
    fun explodeTests() {
        runExplodeActionTest(inputData = "[[[[[9,8],1],2],3],4]", expectedResult = "[[[[0,9],2],3],4]")
        runExplodeActionTest(inputData = "[7,[6,[5,[4,[3,2]]]]]", expectedResult = "[7,[6,[5,[7,0]]]]")
        runExplodeActionTest(inputData = "[[6,[5,[4,[3,2]]]],1]", expectedResult = "[[6,[5,[7,0]]],3]")
        runExplodeActionTest(inputData = "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]", expectedResult = "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        runExplodeActionTest(inputData = "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", expectedResult = "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
    }

    private fun runExplodeActionTest(inputData: String, expectedResult: String) {
        val node = inputData.toSnailfishNumber()
        assertTrue(checkForExplodingAction(node))
        assertEquals(expectedResult, node.toString())
    }

    @Test
    fun listSumTests() {
        val sumInput1 = """
            [1,1]
            [2,2]
            [3,3]
            [4,4]
        """.trimIndent()
        runSumTest(sumInput1, expectedResult = "[[[[1,1],[2,2]],[3,3]],[4,4]]")

        val sumInput2 = """
            [1,1]
            [2,2]
            [3,3]
            [4,4]
            [5,5]
        """.trimIndent()
        runSumTest(sumInput2, expectedResult = "[[[[3,0],[5,3]],[4,4]],[5,5]]")

        val sumInput3 = """
            [1,1]
            [2,2]
            [3,3]
            [4,4]
            [5,5]
            [6,6]
        """.trimIndent()
        runSumTest(sumInput3, expectedResult = "[[[[5,0],[7,4]],[5,5]],[6,6]]")

        val sumInput4a = """
            [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
            [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
        """.trimIndent()
        runSumTest(sumInput4a, expectedResult = "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")

        val sumInput4b = """
            [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
            [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
        """.trimIndent()
        runSumTest(sumInput4b, expectedResult = "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]")

        val sumInput4c = """
            [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]
            [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
        """.trimIndent()
        runSumTest(sumInput4c, expectedResult = "[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")

        val sumInput4d = """
            [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]
            [7,[5,[[3,8],[1,4]]]]
        """.trimIndent()
        runSumTest(sumInput4d, expectedResult = "[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]")

        val sumInput4e = """
            [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
            [[2,[2,2]],[8,[8,1]]]
        """.trimIndent()
        runSumTest(sumInput4e, expectedResult = "[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]")

        val sumInput4f = """
            [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
            [2,9]
        """.trimIndent()
        runSumTest(sumInput4f, expectedResult = "[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]")

        val sumInput4g = """
            [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]
            [1,[[[9,3],9],[[9,0],[0,7]]]]
        """.trimIndent()
        runSumTest(sumInput4g, expectedResult = "[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]")

        val sumInput4h = """
            [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]
            [[[5,[7,4]],7],1]
        """.trimIndent()
        runSumTest(sumInput4h, expectedResult = "[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]")

        val sumInput4i = """
            [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
            [[[[4,2],2],6],[8,7]]
        """.trimIndent()
        runSumTest(sumInput4i, expectedResult = "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")

        val sumInput4 = """
            [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
            [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
            [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
            [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
            [7,[5,[[3,8],[1,4]]]]
            [[2,[2,2]],[8,[8,1]]]
            [2,9]
            [1,[[[9,3],9],[[9,0],[0,7]]]]
            [[[5,[7,4]],7],1]
            [[[[4,2],2],6],[8,7]]
        """.trimIndent()
        runSumTest(sumInput4, expectedResult = "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
    }

    private fun runSumTest(inputData: String, expectedResult: String) {
        val sum = inputData.getInputLines().map { it.toSnailfishNumber() }.sum()
        assertEquals(expectedResult, sum.toString())
    }

    @Test
    fun magnitudeTests() {
        runMagnitudeTest(inputData = "[[1,2],[[3,4],5]]", expectedResult = 143L)
        runMagnitudeTest(inputData = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", expectedResult = 1384L)
        runMagnitudeTest(inputData = "[[[[1,1],[2,2]],[3,3]],[4,4]]", expectedResult = 445L)
        runMagnitudeTest(inputData = "[[[[3,0],[5,3]],[4,4]],[5,5]]", expectedResult = 791L)
        runMagnitudeTest(inputData = "[[[[5,0],[7,4]],[5,5]],[6,6]]", expectedResult = 1137L)
        runMagnitudeTest(inputData = "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", expectedResult = 3488L)
    }

    private fun runMagnitudeTest(inputData: String, expectedResult: Long) {
        val node = inputData.toSnailfishNumber()
        assertEquals(expectedResult, node.magnitude)
    }
}
