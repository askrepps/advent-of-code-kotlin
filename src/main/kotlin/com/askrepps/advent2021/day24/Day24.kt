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
import java.io.File

sealed interface ValueSource

class Literal(val value: Long) : ValueSource

enum class Register(val address: Int) : ValueSource {
    W(address = 0),
    X(address = 1),
    Y(address = 2),
    Z(address = 3)
}

sealed class Instruction(val targetRegister: Register)

class Input(targetRegister: Register) : Instruction(targetRegister)

sealed class BinaryInstruction(val leftRegister: Register, val rightSource: ValueSource) : Instruction(leftRegister) {
    abstract fun evaluate(leftValue: Long, rightValue: Long): Long
}

class Add(leftRegister: Register, rightSource: ValueSource) : BinaryInstruction(leftRegister, rightSource) {
    override fun evaluate(leftValue: Long, rightValue: Long) =
        leftValue + rightValue
}

class Multiply(leftRegister: Register, rightSource: ValueSource) : BinaryInstruction(leftRegister, rightSource) {
    override fun evaluate(leftValue: Long, rightValue: Long) =
        leftValue * rightValue
}

class Divide(leftRegister: Register, rightSource: ValueSource) : BinaryInstruction(leftRegister, rightSource) {
    override fun evaluate(leftValue: Long, rightValue: Long) =
        leftValue / rightValue
}

class Mod(leftRegister: Register, rightSource: ValueSource) : BinaryInstruction(leftRegister, rightSource) {
    override fun evaluate(leftValue: Long, rightValue: Long) =
        leftValue % rightValue
}

class Equal(leftRegister: Register, rightSource: ValueSource) : BinaryInstruction(leftRegister, rightSource) {
    override fun evaluate(leftValue: Long, rightValue: Long) =
        if (leftValue == rightValue) {
            1L
        } else {
            0L
        }
}

class ALU {
    private val registerValues = Array(4) { 0L }
    private var inputIndex = 0

    fun getValue(register: Register) =
        registerValues[register.address]

    fun runProgram(program: List<Instruction>, input: List<Long>) {
        registerValues.fill(0L)
        inputIndex = 0
        for (instruction in program) {
            executeInstruction(instruction, input)
        }
    }

    private fun executeInstruction(instruction: Instruction, input: List<Long>) {
        val result = when (instruction) {
            is Input -> {
                input[inputIndex++]
            }
            is BinaryInstruction -> {
                val leftValue = registerValues[instruction.leftRegister.address]
                val rightValue = when (val source = instruction.rightSource) {
                    is Literal -> source.value
                    is Register -> registerValues[source.address]
                }
                instruction.evaluate(leftValue, rightValue)
            }
        }
        registerValues[instruction.targetRegister.address] = result
    }
}

fun String.toRegister() =
    when (lowercase()) {
        "w" -> Register.W
        "x" -> Register.X
        "y" -> Register.Y
        "z" -> Register.Z
        else -> throw IllegalArgumentException("Unrecognized register: '$this'")
    }

fun String.toInstruction(): Instruction {
    val tokens = split(" ")
    val operation = tokens.first()
    val leftRegister = tokens[1].toRegister()
    return if (operation == "inp") {
        Input(leftRegister)
    } else {
        val rightRegister = tokens[2].toLongOrNull()?.let { Literal(it) }
            ?: tokens[2].toRegister()
        when (operation) {
            "add" -> Add(leftRegister, rightRegister)
            "mul" -> Multiply(leftRegister, rightRegister)
            "div" -> Divide(leftRegister, rightRegister)
            "mod" -> Mod(leftRegister, rightRegister)
            "eql" -> Equal(leftRegister, rightRegister)
            else -> throw IllegalArgumentException("Unrecognized operation '$operation'")
        }
    }
}

fun List<String>.toProgram() =
    map { it.toInstruction() }

fun checkModelNumber(alu: ALU, program: List<Instruction>, input: List<Long>): Boolean {
    alu.runProgram(program, input)
    return alu.getValue(Register.Z) == 0L
}

fun findFirstValidModelNumber(program: List<Instruction>, reverseSearchSpace: Boolean): Long? {
    val alu = ALU()
    val digitValues = 1..9

    // only half of the digits are independent, which greatly narrows the search space
    val searchSpace =
        if (reverseSearchSpace) {
            // finds max
            9999999L downTo 1111111L
        } else {
            // finds min
            1111111L..9999999L
        }
    for (freeDigits in searchSpace) {
        // dependent values were determined by manual analysis of the input program
        // credit to William Feng's video for help with the analysis approach:
        //   - https://www.youtube.com/watch?v=Eswmo7Y7C4U
        val input = freeDigits.toString().map { (it.code - '0'.code).toLong() }.toMutableList()
        input.add(3, input[2] - 6L)
        input.add(5, input[4] + 5L)
        input.add(9, input[8] - 5L)
        input.add(10, input[7] + 1L)
        input.add(11, input[6] - 8L)
        input.add(12, input[1] + 7L)
        input.add(13, input[0] + 8L)
        if (input.any { it !in digitValues }) {
            continue
        }
        if (checkModelNumber(alu, program, input)) {
            return input.fold(0L) { result, digit -> result * 10L + digit }
        }
    }
    return null
}

fun getPart1Answer(program: List<Instruction>) =
    findFirstValidModelNumber(program, reverseSearchSpace = true)

fun getPart2Answer(program: List<Instruction>) =
    findFirstValidModelNumber(program, reverseSearchSpace = false)

fun main() {
    val program = File("src/main/resources/day24.txt")
        .getInputLines().map { it.toInstruction() }

    println("The answer to part 1 is ${getPart1Answer(program)}")
    println("The answer to part 2 is ${getPart2Answer(program)}")
}
