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
import java.io.File

sealed class SnailfishNode {
    var left: SnailfishNode? = null
    var right: SnailfishNode? = null
    var parent: SnailfishNode? = null

    abstract val magnitude: Long

    abstract fun deepCopy(): SnailfishNode
}

class SnailfishPairNode(leftNode: SnailfishNode?, rightNode: SnailfishNode?) : SnailfishNode() {
    init {
        this.left = leftNode
        this.right = rightNode
        leftNode?.parent = this
        rightNode?.parent = this
    }

    override val magnitude: Long
        get() {
            val lhs = requireNotNull(left) { "Pair node has null left child" }
            val rhs = requireNotNull(right) { "Pair node has null right child" }
            return 3 * lhs.magnitude + 2 * rhs.magnitude
        }

    override fun deepCopy() =
        SnailfishPairNode(left?.deepCopy(), right?.deepCopy())

    override fun toString() =
        "[$left,$right]"
}

class SnailfishValueNode(var value: Long) : SnailfishNode() {
    override val magnitude: Long
        get() = value

    override fun deepCopy() =
        SnailfishValueNode(value)

    override fun toString() =
        value.toString()
}

val Char.longValue: Long
    get() {
        check(isDigit()) { "Cannot convert non-digit character to value" }
        return (code - '0'.code).toLong()
    }

val List<Char>.longValue: Long
    get() = fold(0L) { value, c ->
        value * 10L + c.longValue
    }

fun List<Char>.parseSnailfishNumber(): Pair<SnailfishNode, Int> {
    val firstChar = first()
    if (firstChar == '[') {
        val leftNodeIndex = 1
        val (leftNode, leftNodeSize) = subList(leftNodeIndex, size).parseSnailfishNumber()

        val rightNodeIndex = leftNodeIndex + leftNodeSize + 1
        check(get(rightNodeIndex - 1) == ',') {
            "Malformed pair node - expected comma separating left and right nodes"
        }
        val (rightNode, rightNodeSize) = subList(rightNodeIndex, size).parseSnailfishNumber()

        val totalNodeSize = rightNodeIndex + rightNodeSize + 1
        check(get(totalNodeSize - 1) == ']') {
            "Malformed pair node - expected closing bracket"
        }
        val node = SnailfishPairNode(leftNode, rightNode)
        return Pair(node, totalNodeSize)
    } else if (firstChar.isDigit()) {
        val endIndex = indexOfFirst { !it.isDigit() }
        val value = subList(0, endIndex).longValue

        val node = SnailfishValueNode(value)
        return Pair(node, endIndex)
    } else {
        throw IllegalArgumentException("Unexpected first character: $firstChar")
    }
}

fun String.toSnailfishNumber() =
    toList().parseSnailfishNumber().first

tailrec fun SnailfishNode.findParentWithLeftSubtreeNotIncludingMe(): SnailfishNode? =
    if (parent?.left != this) {
        parent
    } else {
        parent?.findParentWithLeftSubtreeNotIncludingMe()
    }

tailrec fun SnailfishNode.findParentWithRightSubtreeNotIncludingMe(): SnailfishNode? =
    if (parent?.right != this) {
        parent
    } else {
        parent?.findParentWithRightSubtreeNotIncludingMe()
    }

tailrec fun SnailfishNode.findLeftmostValueNode(): SnailfishValueNode? =
    (this as? SnailfishValueNode) ?: left?.findLeftmostValueNode()

tailrec fun SnailfishNode.findRightmostValueNode(): SnailfishValueNode? =
    (this as? SnailfishValueNode) ?: right?.findRightmostValueNode()

val SnailfishNode.firstNumberToTheLeft: SnailfishValueNode?
    get() = findParentWithLeftSubtreeNotIncludingMe()?.left?.findRightmostValueNode()

val SnailfishNode.firstNumberToTheRight: SnailfishValueNode?
    get() = findParentWithRightSubtreeNotIncludingMe()?.right?.findLeftmostValueNode()

fun SnailfishNode.replaceChild(currentNode: SnailfishNode) {
    val nodeParent = requireNotNull(currentNode.parent) { "Current node has no parent" }
    if (nodeParent.left == currentNode) {
        nodeParent.left = this
    } else if (nodeParent.right == currentNode) {
        nodeParent.right = this
    } else {
        throw IllegalStateException("Corrupt node tree")
    }
    parent = nodeParent
}

fun SnailfishNode.explode() {
    val leftShard = requireNotNull(left as? SnailfishValueNode) { "Exploding node must have left value" }
    val rightShard = requireNotNull(right as? SnailfishValueNode) { "Exploding node must have right value" }
    firstNumberToTheLeft?.let { it.value += leftShard.value }
    firstNumberToTheRight?.let { it.value += rightShard.value }

    val newValue = SnailfishValueNode(0L)
    newValue.replaceChild(this)
}

fun SnailfishValueNode.split() {
    val leftValue = value / 2L
    val rightValue = if (value % 2L == 0L) leftValue else leftValue + 1
    val leftNode = SnailfishValueNode(leftValue)
    val rightNode = SnailfishValueNode(rightValue)
    val newPair = SnailfishPairNode(leftNode, rightNode)
    newPair.replaceChild(this)
}

fun checkForExplodingAction(node: SnailfishNode?, depth: Int = 0): Boolean {
    if (node == null) {
        return false
    }
    if (node is SnailfishPairNode && depth >= 4) {
        node.explode()
        return true
    }
    val childDepth = depth + 1
    return checkForExplodingAction(node.left, childDepth) || checkForExplodingAction(node.right, childDepth)
}

fun checkForSplittingAction(node: SnailfishNode?): Boolean {
    if (node == null) {
        return false
    }
    if (node is SnailfishValueNode && node.value >= 10) {
        node.split()
        return true
    }
    return checkForSplittingAction(node.left) || checkForSplittingAction(node.right)
}

fun SnailfishNode.applyReduction(): SnailfishNode {
    while (checkForExplodingAction(this) || checkForSplittingAction(this)) {
        // action was performed, so check again
    }
    return this
}

fun SnailfishNode.addTo(other: SnailfishNode) =
    SnailfishPairNode(deepCopy(), other.deepCopy()).applyReduction()

fun List<SnailfishNode>.sum() =
    reduce { leftNode, rightNode -> leftNode.addTo(rightNode) }

fun getPart1Answer(numbers: List<SnailfishNode>) =
    numbers.sum().magnitude

fun getPart2Answer(numbers: List<SnailfishNode>) =
    numbers.maxOf { n1 ->
        numbers.maxOf { n2 ->
            if (n1 == n2) {
                Long.MIN_VALUE
            } else {
                n1.addTo(n2).magnitude
            }
        }
    }

fun main() {
    val numbers = File("src/main/resources/2021/input-2021-day18.txt")
        .getInputLines().map { it.toSnailfishNumber() }

    println("The answer to part 1 is ${getPart1Answer(numbers)}")
    println("The answer to part 2 is ${getPart2Answer(numbers)}")
}
