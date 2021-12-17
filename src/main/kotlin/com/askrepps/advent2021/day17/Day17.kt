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

package com.askrepps.advent2021.day17

import java.io.File
import kotlin.math.max
import kotlin.math.min

data class GraphCoordinates(val x: Int, val y: Int)

data class Rectangle(val xBounds: IntRange, val yBounds: IntRange) {
    fun contains(coordinates: GraphCoordinates) =
        coordinates.x in xBounds && coordinates.y in yBounds
}

fun String.toIntRange(): IntRange {
    val (firstValue, secondValue) = split("=").last().split("..").map { it.toInt() }
    return min(firstValue, secondValue)..max(firstValue, secondValue)
}

fun String.toTargetArea(): Rectangle {
    val (xDefinition, yDefinition) = split(": ").last().split(", ")
    return Rectangle(xDefinition.toIntRange(), yDefinition.toIntRange())
}

fun simulateTrajectory(initialVelocityX: Int, initialVelocityY: Int, targetArea: Rectangle): List<GraphCoordinates>? {
    var position = GraphCoordinates(0, 0)
    var velocityX = initialVelocityX
    var velocityY = initialVelocityY

    val trajectory = mutableListOf<GraphCoordinates>()
    while (velocityY > 0 || position.y >= targetArea.yBounds.first) {
        trajectory.add(position)

        if (targetArea.contains(position)) {
            return trajectory
        }

        position = GraphCoordinates(position.x + velocityX, position.y + velocityY)
        if (velocityX > 0) {
            velocityX--
        } else if (velocityX < 0) {
            velocityX++
        }
        velocityY--
    }

    return null
}

fun generateValidTrajectories(targetArea: Rectangle): List<List<GraphCoordinates>> {
    // assumes target area is to the right of and below the starting position
    //   - search every positive vx0 that doesn't immediately overshoot the target
    //   - search every vy0 that won't overshoot the target on the way down
    //     (all trajectories with positive vy0 values will eventually return to y = 0 with a -vy0 velocity)
    val vxSearch = 1..targetArea.xBounds.last
    val vySearch = targetArea.yBounds.first..(-targetArea.yBounds.first)
    return vxSearch.flatMap { vx0 -> vySearch.map { vy0 -> vx0 to vy0 } }
        .mapNotNull { (vx0, vy0) -> simulateTrajectory(vx0, vy0, targetArea) }
}

fun getPart1Answer(validTrajectories: List<List<GraphCoordinates>>) =
    validTrajectories.maxOf { trajectory -> trajectory.maxOf { it.y } }

fun getPart2Answer(validTrajectories: List<List<GraphCoordinates>>) =
    validTrajectories.size

fun main() {
    val targetArea = File("src/main/resources/day17.txt")
        .readText().toTargetArea()
    val trajectories = generateValidTrajectories(targetArea)

    println("The answer to part 1 is ${getPart1Answer(trajectories)}")
    println("The answer to part 2 is ${getPart2Answer(trajectories)}")
}
