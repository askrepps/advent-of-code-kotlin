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

package com.askrepps.advent2022.day07

import com.askrepps.advent2022.util.getInputLines
import java.io.File

interface Node {
    val name: String
    val size: Long
}

class FileNode(override val name: String, override val size: Long) : Node

class DirectoryNode(override val name: String, val parent: DirectoryNode? = null) : Node {
    private val _childNodes = mutableMapOf<String, Node>()
    val childNodes: Map<String, Node>
        get() = _childNodes

    private var dirtySize = true
    private var cachedSize = 0L

    override val size: Long
        get() =
            if (dirtySize) {
                dirtySize = false
                childNodes.values.sumOf { it.size }
                    .also { cachedSize = it }
            } else {
                cachedSize
            }

    fun addChild(node: Node) {
        _childNodes[node.name] = node
        dirtySize = true
    }

    fun getDirectoriesRecursive(): List<DirectoryNode> =
        childNodes.values.filterIsInstance<DirectoryNode>()
            .flatMap { it.getDirectoriesRecursive() } + this
}

fun String.lastToken() = split(" ").last()

fun List<String>.toFileTree(): DirectoryNode {
    val rootNode = DirectoryNode("/")
    var currentNode = rootNode
    var index = 1
    while (index < size) {
        val line = get(index)
        when {
            line.startsWith("dir") -> {
                val dirName = line.lastToken()
                currentNode.addChild(DirectoryNode(dirName, currentNode))
            }
            line.first().isDigit() -> {
                val (fileSize, fileName) = line.split(" ")
                currentNode.addChild(FileNode(fileName, fileSize.toLong()))
            }
            line.startsWith("$ cd ..") -> {
                currentNode = checkNotNull(currentNode.parent) {
                    "node ${currentNode.name} does not have a parent directory"
                }
            }
            line.startsWith("$ cd") -> {
                val dirName = line.lastToken()
                currentNode = checkNotNull(currentNode.childNodes[dirName] as? DirectoryNode) {
                    "directory $dirName not found in node ${currentNode.name}"
                }
            }
        }
        index++
    }
    return rootNode
}

fun findSumOfDirectoriesUnderLimit(root: DirectoryNode, limit: Long) =
    root.getDirectoriesRecursive()
        .filter { it.size <= limit }
        .sumOf { it.size }

fun findSmallestDirectoryToDelete(root: DirectoryNode, diskSize: Long, spaceNeeded: Long): Long {
    val freeSpace = diskSize - root.size
    return root.getDirectoriesRecursive()
        .filter { freeSpace + it.size >= spaceNeeded }
        .minOf { it.size }
}

fun getPart1Answer(root: DirectoryNode) =
    findSumOfDirectoriesUnderLimit(root, limit = 100000L)

fun getPart2Answer(root: DirectoryNode) =
    findSmallestDirectoryToDelete(root, diskSize = 70000000L, spaceNeeded = 30000000L)

fun main() {
    val root = File("src/main/resources/2022/day07.txt")
        .getInputLines().toFileTree()

    println("The answer to part 1 is ${getPart1Answer(root)}")
    println("The answer to part 2 is ${getPart2Answer(root)}")
}
