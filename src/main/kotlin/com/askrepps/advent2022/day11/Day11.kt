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

package com.askrepps.advent2022.day11

import java.io.File
import java.math.BigInteger

sealed class Operation(val value: BigInteger?) {
    abstract fun applyTo(incomingValue: BigInteger): BigInteger
}

class Addition(value: BigInteger?) : Operation(value) {
    override fun applyTo(incomingValue: BigInteger): BigInteger =
        incomingValue.add(value ?: incomingValue)
}

class Multiplication(value: BigInteger?) : Operation(value) {
    override fun applyTo(incomingValue: BigInteger): BigInteger =
        incomingValue.multiply(value ?: incomingValue)
}

class Monkey(
    val initialItems: List<BigInteger>,
    val operation: Operation,
    val divisibleTest: BigInteger,
    val trueMonkeyId: Int,
    val falseMonkeyId: Int
) {
    private val items = ArrayDeque(initialItems)

    var inspectionCount = 0L
        private set

    fun takeTurn(monkeys: List<Monkey>, commonModulo: BigInteger, worryReductionFactor: BigInteger?) {
        while (items.isNotEmpty()) {
            val currentItem = items.removeFirst()
            val newItem = operation.applyTo(currentItem).let { baseValue ->
                worryReductionFactor?.let { baseValue.divide(it) } ?: baseValue
            }
            val targetMonkeyId =
                if (newItem.mod(divisibleTest) == BigInteger.ZERO) {
                    trueMonkeyId
                } else {
                    falseMonkeyId
                }
            monkeys[targetMonkeyId].catch(newItem.mod(commonModulo))
            inspectionCount++
        }
    }

    private fun catch(item: BigInteger) {
        items.addLast(item)
    }
}

fun String.toMonkey(): Monkey {
    val lines = lines()
    val initialItems = lines[1].substringAfter(": ")
        .split(", ")
        .map { it.toBigInteger() }
    val operationValue = lines[2].split(" ").last().toBigIntegerOrNull()
    val operation =
        if (lines[2].contains("+")) {
            Addition(operationValue)
        } else {
            Multiplication(operationValue)
        }
    val divisibleTest = lines[3].split(" ").last().toBigInteger()
    val trueMonkeyId = lines[4].split(" ").last().toInt()
    val falseMonkeyId = lines[5].split(" ").last().toInt()
    return Monkey(initialItems, operation, divisibleTest, trueMonkeyId, falseMonkeyId)
}

fun cloneMonkey(monkey: Monkey) =
    Monkey(monkey.initialItems, monkey.operation, monkey.divisibleTest, monkey.trueMonkeyId, monkey.falseMonkeyId)

fun getMonkeyBusiness(monkeys: List<Monkey>, numRounds: Int, worryReductionFactor: BigInteger? = null): Long {
    val monkeyClones = monkeys.map { cloneMonkey(it) }
    val commonModulo = monkeys.fold(BigInteger.ONE) { acc, monkey ->
        acc.multiply(monkey.divisibleTest)
    }
    repeat(numRounds) {
        for (monkey in monkeyClones) {
            monkey.takeTurn(monkeyClones, commonModulo, worryReductionFactor)
        }
    }
    return monkeyClones.sortedByDescending { it.inspectionCount }
        .take(2)
        .fold(1L) { acc, monkey ->
            acc * monkey.inspectionCount
        }
}

fun getPart1Answer(monkeys: List<Monkey>) =
    getMonkeyBusiness(monkeys, numRounds = 20, worryReductionFactor = 3L.toBigInteger())

fun getPart2Answer(monkeys: List<Monkey>) =
    getMonkeyBusiness(monkeys, numRounds = 10000)

fun main() {
    val monkeys = File("src/main/resources/2022/day11.txt")
        .readText().split("\n\n").map { it.trim().toMonkey() }

    println("The answer to part 1 is ${getPart1Answer(monkeys)}")
    println("The answer to part 2 is ${getPart2Answer(monkeys)}")
}
