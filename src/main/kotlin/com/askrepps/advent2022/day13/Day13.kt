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

package com.askrepps.advent2022.day13

import com.askrepps.advent2022.util.getInputLines
import java.io.File
import java.util.Stack

sealed class PacketData : Comparable<PacketData> {
    override fun compareTo(other: PacketData): Int {
        return when (this) {
            is PacketNumber -> {
                when (other) {
                    is PacketNumber -> value.compareTo(other.value)
                    is PacketList -> wrapInList().compareTo(other)
                }
            }
            is PacketList -> {
                when (other) {
                    is PacketNumber -> compareTo(other.wrapInList())
                    is PacketList -> {
                        var listResult = 0
                        for ((thisData, otherData) in data.zip(other.data)) {
                            val result = thisData.compareTo(otherData)
                            if (result != 0) {
                                listResult = result
                                break
                            }
                        }
                        if (listResult != 0) {
                            listResult
                        } else {
                            this.data.size - other.data.size
                        }
                    }
                }
            }
        }
    }
}

class PacketNumber(val value: Int) : PacketData() {
    fun wrapInList() = PacketList().apply { addData(PacketNumber(value)) }
}

class PacketList : PacketData() {
    private val _data = mutableListOf<PacketData>()
    val data: List<PacketData>
        get() = _data

    fun addData(newData: PacketData) {
        _data.add(newData)
    }
}

fun String.toPacketData(): PacketData? {
    if (isBlank()) {
        return null
    }

    val listStack = Stack<PacketList>()
    var currentList = PacketList()
    var pendingValue: Int? = null

    fun storePendingValue() {
        pendingValue?.let {
            currentList.addData(PacketNumber(it))
            pendingValue = null
        }
    }

    for (c in this.subSequence(1, length - 1)) {
        when (c) {
            in '0'..'9' -> {
                pendingValue = (pendingValue ?: 0) * 10 + c.digitToInt()
            }
            ',' -> {
                storePendingValue()
            }
            '[' -> {
                listStack.push(currentList)
                currentList = PacketList()
            }
            ']' -> {
                storePendingValue()
                val previousList = listStack.pop()
                previousList.addData(currentList)
                currentList = previousList
            }
        }
    }
    storePendingValue()
    return currentList
}

fun getPart1Answer(packets: List<PacketData>) =
    packets.chunked(2)
        .mapIndexedNotNull { idx, (p1, p2) ->
            if (p1 <= p2) {
                idx + 1
            } else {
                null
            }
        }.sum()

fun getPart2Answer(packets: List<PacketData>): Int {
    val divider1 = checkNotNull("[[2]]".toPacketData())
    val divider2 = checkNotNull("[[6]]".toPacketData())
    val sortedPackets = (packets + listOf(divider1, divider2)).sorted()
    return (sortedPackets.indexOf(divider1) + 1) * (sortedPackets.indexOf(divider2) + 1)
}

fun main() {
    val packets = File("src/main/resources/2022/day13.txt")
        .getInputLines().mapNotNull { it.toPacketData() }

    println("The answer to part 1 is ${getPart1Answer(packets)}")
    println("The answer to part 2 is ${getPart2Answer(packets)}")
}
