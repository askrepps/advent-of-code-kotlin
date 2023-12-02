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

package com.askrepps.advent.advent2022.day03

import com.askrepps.advent.util.getInputLines
import java.io.File

data class Rucksack(val items: String) {
    val firstCompartment = items.take(items.length / 2)
    val secondCompartment = items.takeLast(items.length / 2)
}

fun String.toRucksack() = Rucksack(this)

val Char.priority: Int
    get() = when (this) {
        in 'a'..'z' -> this - 'a' + 1
        in 'A'..'Z' -> this - 'A' + 27
        else -> error("Invalid character $this")
    }

val allTypes = ('a'..'z').toSet() + ('A'..'Z').toSet()

val List<Rucksack>.commonPriority: Int
    get() {
        val commonType = fold(allTypes) { acc, rucksack ->
            acc.intersect(rucksack.items.toSet())
        }
        check(commonType.size == 1)
        return commonType.first().priority
    }

fun getPart1Answer(rucksacks: List<Rucksack>) =
    rucksacks.sumOf { rucksack ->
        rucksack.firstCompartment.toSet()
            .intersect(rucksack.secondCompartment.toSet())
            .sumOf { it.priority }
    }

fun getPart2Answer(rucksacks: List<Rucksack>) =
    rucksacks.chunked(3)
        .sumOf { it.commonPriority }

fun main() {
    val rucksacks = File("src/main/resources/2022/day03.txt")
        .getInputLines().map { it.toRucksack() }

    println("The answer to part 1 is ${getPart1Answer(rucksacks)}")
    println("The answer to part 2 is ${getPart2Answer(rucksacks)}")
}
