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

package com.askrepps.advent2022.day05

import java.io.File
import java.util.Stack

data class Command(val count: Int, val source: Int, val destination: Int)

data class Input(val stacks: List<Stack<Char>>, val commands: List<Command>)

fun String.toCrateStacks(): List<Stack<Char>> {
    val stacks = mutableListOf<Stack<Char>>()
    for (line in split("\n").reversed()) {
        if (line.isBlank()) continue
        line.forEachIndexed { index, c ->
            if (c.isLetter()) {
                val stackIndex = index / 4
                while (stacks.size < stackIndex + 1) {
                    stacks.add(Stack())
                }
                stacks[stackIndex].push(c)
            }
        }
    }
    return stacks
}

fun String.toCommands() =
    lines().mapNotNull {
        if (it.isBlank()) {
            null
        } else {
            val tokens = it.split(" ")
            Command(count = tokens[1].toInt(), source = tokens[3].toInt(), destination = tokens[5].toInt())
        }
    }

fun String.toInputData(): Input {
    val (stacksString, commandsString) = split("\n\n")
    return Input(stacksString.toCrateStacks(), commandsString.toCommands())
}

fun applyCommandsWithSingleGrab(input: Input) {
    for (command in input.commands) {
        repeat(command.count) {
            input.stacks[command.destination - 1].push(input.stacks[command.source - 1].pop())
        }
    }
}

fun applyCommandsWithMultiGrab(input: Input) {
    val tempStack = Stack<Char>()
    for (command in input.commands) {
        repeat(command.count) {
            tempStack.push(input.stacks[command.source - 1].pop())
        }
        repeat(command.count) {
            input.stacks[command.destination - 1].push(tempStack.pop())
        }
    }
}

fun runCommands(inputText: String, commandStrategy: (Input) -> Unit): String {
    val input = inputText.toInputData()
    commandStrategy(input)
    return input.stacks.joinToString(separator = "") { it.peek().toString() }
}

fun getPart1Answer(inputText: String) =
    runCommands(inputText, ::applyCommandsWithSingleGrab)

fun getPart2Answer(inputText: String) =
    runCommands(inputText, ::applyCommandsWithMultiGrab)

fun main() {
    val input = File("src/main/resources/2022/day05.txt")
        .readText()

    println("The answer to part 1 is ${getPart1Answer(input)}")
    println("The answer to part 2 is ${getPart2Answer(input)}")
}
