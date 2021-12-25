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

package com.askrepps.advent2021.day24

import com.askrepps.advent2021.util.getInputLines
import kotlin.test.assertEquals
import kotlin.test.Test

class Day24Test {
    private val alu = ALU()

    @Test
    fun testNegateProgram() {
        val negateProgram = """
            inp x
            mul x -1
        """.trimMargin().getInputLines().toProgram()

        alu.runProgram(negateProgram, listOf(0L))
        assertEquals(0L, alu.getValue(Register.X))

        alu.runProgram(negateProgram, listOf(1L))
        assertEquals(-1L, alu.getValue(Register.X))

        alu.runProgram(negateProgram, listOf(-1L))
        assertEquals(1L, alu.getValue(Register.X))

        alu.runProgram(negateProgram, listOf(42L))
        assertEquals(-42L, alu.getValue(Register.X))
    }

    @Test
    fun testIsThreeTimesProgram() {
        val isThreeTimesProgram = """
            inp z
            inp x
            mul z 3
            eql z x
        """.trimMargin().getInputLines().toProgram()

        alu.runProgram(isThreeTimesProgram, listOf(1L, 3L))
        assertEquals(1L, alu.getValue(Register.Z))

        alu.runProgram(isThreeTimesProgram, listOf(3L, 1L))
        assertEquals(0L, alu.getValue(Register.Z))
    }

    @Test
    fun testToBinaryProgram() {
        val toBinaryProgram = """
            inp w
            add z w
            mod z 2
            div w 2
            add y w
            mod y 2
            div w 2
            add x w
            mod x 2
            div w 2
            mod w 2
        """.trimMargin().getInputLines().toProgram()

        alu.runProgram(toBinaryProgram, listOf(5L))
        assertEquals(0L, alu.getValue(Register.W))
        assertEquals(1L, alu.getValue(Register.X))
        assertEquals(0L, alu.getValue(Register.Y))
        assertEquals(1L, alu.getValue(Register.Z))

        alu.runProgram(toBinaryProgram, listOf(10L))
        assertEquals(1L, alu.getValue(Register.W))
        assertEquals(0L, alu.getValue(Register.X))
        assertEquals(1L, alu.getValue(Register.Y))
        assertEquals(0L, alu.getValue(Register.Z))
    }
}
