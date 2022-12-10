/*
 * MIT License
 *
 * Copyright (c) 2022 Andrew Krepps
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

package com.askrepps.advent2022.day10

import com.askrepps.advent2022.util.getInputLines
import java.io.File
import kotlin.math.abs

private const val NOOP_CYCLES = 1
private const val ADDX_CYCLES = 2

sealed class Instruction(val cycles: Int, val value: Int)

object NoOp : Instruction(cycles = NOOP_CYCLES, value = 0)

class AddX(value: Int) : Instruction(cycles = ADDX_CYCLES, value)

fun String.toInstruction() =
    if (this == "noop") {
        NoOp
    } else {
        AddX(split(" ").last().toInt())
    }

fun runProgram(instructions: List<Instruction>, onCycleFinished: (Int, Int) -> Unit) {
    var programCounter = 0
    var register = 1
    var cycle = 0
    var skipCount = 0
    var pendingValue = 0
    while (programCounter < instructions.size) {
        cycle++
        if (skipCount > 0) {
            skipCount--
        } else {
            register += pendingValue
            val instruction = instructions[programCounter]
            skipCount = instruction.cycles - 1
            pendingValue = instruction.value
            programCounter++
        }
        onCycleFinished(cycle, register)
    }
}

fun getTotalSignalStrength(instructions: List<Instruction>): Int {
    var totalStrength = 0
    runProgram(instructions) { cycle, register ->
        if ((cycle - 20) % 40 == 0) {
            totalStrength += cycle * register
        }
    }
    return totalStrength
}

fun getRenderedImage(instructions: List<Instruction>): String {
    val sb = StringBuilder()
    runProgram(instructions) { cycle, register ->
        val renderColumn = (cycle - 1) % 40
        val pixel =
            if (abs(register - renderColumn) <= 1) {
                '#'
            } else {
                '.'
            }
        sb.append(pixel)
        if (renderColumn == 39) {
            sb.append('\n')
        }
    }
    return sb.toString().trim()
}

fun getPart1Answer(instructions: List<Instruction>) =
    getTotalSignalStrength(instructions)

fun getPart2Answer(instructions: List<Instruction>) =
    getRenderedImage(instructions)

fun main() {
    val instructions = File("src/main/resources/2022/day10.txt")
        .getInputLines().map { it.toInstruction() }

    println("The answer to part 1 is ${getPart1Answer(instructions)}")
    println("The answer to part 2 is \n${getPart2Answer(instructions)}")
}
