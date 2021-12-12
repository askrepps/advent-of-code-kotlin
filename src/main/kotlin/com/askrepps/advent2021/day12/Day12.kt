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

package com.askrepps.advent2021.day12

import com.askrepps.advent2021.util.getInputLines
import java.io.File

enum class NodeType { START, END, BIG, SMALL }

class CaveNode(val label: String) {
    val type: NodeType =
        when {
            label == "start" -> NodeType.START
            label == "end" -> NodeType.END
            label.all { it.isUpperCase() } -> NodeType.BIG
            else -> NodeType.SMALL
        }
    private val _connectedNodes = mutableSetOf<CaveNode>()
    val connectedNodes: Set<CaveNode>
        get() = _connectedNodes

    fun addPathTo(node: CaveNode) {
        _connectedNodes.add(node)
    }
}

fun List<String>.toCaves(): Map<String, CaveNode> {
    val allNodes = mutableMapOf<String, CaveNode>()
    for (line in this) {
        val (label1, label2) = line.split("-")
        val node1 = allNodes.computeIfAbsent(label1) { CaveNode(label1) }
        val node2 = allNodes.computeIfAbsent(label2) { CaveNode(label2) }
        node1.addPathTo(node2)
        node2.addPathTo(node1)
    }
    return allNodes
}

fun countPathsFrom(
    caves: Map<String, CaveNode>,
    startLabel: String,
    smallNodeVisitMax: Int,
    smallNodeVisitCounts: MutableMap<String, Int> = mutableMapOf()
): Int {
    val startNode = caves[startLabel]
        ?: throw RuntimeException("Node $startLabel not found")

    if (startNode.type == NodeType.END) {
        return 1
    }

    if (startNode.type == NodeType.SMALL) {
        smallNodeVisitCounts[startLabel] = (smallNodeVisitCounts[startLabel] ?: 0) + 1
    }

    var count = 0
    for (node in startNode.connectedNodes) {
        if (node.type == NodeType.START) {
            continue
        }
        if (node.type == NodeType.SMALL) {
            val currentNodeVisitCount = smallNodeVisitCounts[node.label] ?: 0
            if (currentNodeVisitCount >= smallNodeVisitMax) {
                continue
            }
            val maxSmallVisitCount = smallNodeVisitCounts.values.maxOrNull() ?: 0
            if (currentNodeVisitCount == 1 && maxSmallVisitCount >= smallNodeVisitMax) {
                continue
            }
        }
        val newVisitedSmallNodeCounts = smallNodeVisitCounts.toMutableMap()
        count += countPathsFrom(caves, node.label, smallNodeVisitMax, newVisitedSmallNodeCounts)
    }

    return count
}

fun getPart1Answer(caves: Map<String, CaveNode>) =
    countPathsFrom(caves, "start", smallNodeVisitMax = 1)

fun getPart2Answer(caves: Map<String, CaveNode>) =
    countPathsFrom(caves, "start", smallNodeVisitMax = 2)

fun main() {
    val caves = File("src/main/resources/day12.txt")
        .getInputLines().toCaves()

    println("The answer to part 1 is ${getPart1Answer(caves)}")
    println("The answer to part 2 is ${getPart2Answer(caves)}")
}
