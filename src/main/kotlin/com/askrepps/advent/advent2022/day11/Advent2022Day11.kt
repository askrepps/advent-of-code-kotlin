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

package com.askrepps.advent.advent2022.day11

import java.io.File

sealed class Operation(val value: Long?) {
    abstract fun applyTo(incomingValue: Long): Long
}

class Addition(value: Long?) : Operation(value) {
    override fun applyTo(incomingValue: Long): Long =
        incomingValue + (value ?: incomingValue)
}

class Multiplication(value: Long?) : Operation(value) {
    override fun applyTo(incomingValue: Long): Long =
        incomingValue * (value ?: incomingValue)
}

class Monkey(
    val initialItems: List<Long>,
    val operation: Operation,
    val divisibleTest: Long,
    val trueMonkeyId: Int,
    val falseMonkeyId: Int
) {
    private val items = ArrayDeque(initialItems)

    var inspectionCount = 0L
        private set

    fun takeTurn(monkeys: List<Monkey>, commonModulo: Long, worryReductionFactor: Long) {
        while (items.isNotEmpty()) {
            val currentItem = items.removeFirst()
            val newItem = (operation.applyTo(currentItem) / worryReductionFactor) % commonModulo
            val targetMonkeyId =
                if (newItem % divisibleTest == 0L) {
                    trueMonkeyId
                } else {
                    falseMonkeyId
                }
            monkeys[targetMonkeyId].catch(newItem)
            inspectionCount++
        }
    }

    private fun catch(item: Long) {
        items.addLast(item)
    }
}

fun String.toMonkey(): Monkey {
    val lines = lines()
    val initialItems = lines[1].substringAfter(": ")
        .split(", ")
        .map { it.toLong() }
    val operationValue = lines[2].split(" ").last().toLongOrNull()
    val operation =
        if (lines[2].contains("+")) {
            Addition(operationValue)
        } else {
            Multiplication(operationValue)
        }
    val divisibleTest = lines[3].split(" ").last().toLong()
    val trueMonkeyId = lines[4].split(" ").last().toInt()
    val falseMonkeyId = lines[5].split(" ").last().toInt()
    return Monkey(initialItems, operation, divisibleTest, trueMonkeyId, falseMonkeyId)
}

fun cloneMonkey(monkey: Monkey) =
    Monkey(monkey.initialItems, monkey.operation, monkey.divisibleTest, monkey.trueMonkeyId, monkey.falseMonkeyId)

fun getMonkeyBusiness(monkeys: List<Monkey>, numRounds: Int, worryReductionFactor: Long = 1L): Long {
    val monkeyClones = monkeys.map { cloneMonkey(it) }
    val commonModulo = monkeys.fold(1L) { acc, monkey ->
        acc * monkey.divisibleTest
    }
    repeat(numRounds) {
        for (monkey in monkeyClones) {
            monkey.takeTurn(monkeyClones, commonModulo, worryReductionFactor)
        }
    }
    return monkeyClones.sortedByDescending { it.inspectionCount }
        .take(2)
        .let { (m1, m2) ->
            m1.inspectionCount * m2.inspectionCount
        }
}

fun getPart1Answer(monkeys: List<Monkey>) =
    getMonkeyBusiness(monkeys, numRounds = 20, worryReductionFactor = 3L)

fun getPart2Answer(monkeys: List<Monkey>) =
    getMonkeyBusiness(monkeys, numRounds = 10000)

fun main() {
    val monkeys = File("src/main/resources/2022/input-2022-day11.txt")
        .readText().split("\n\n").map { it.trim().toMonkey() }

    println("The answer to part 1 is ${getPart1Answer(monkeys)}")
    println("The answer to part 2 is ${getPart2Answer(monkeys)}")
}
