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

package com.askrepps.advent.advent2021.day23

import com.askrepps.advent.util.getInputLines
import java.io.File

sealed class Location(val id: Int) {
    private var _distanceMap = mutableMapOf<Int, Long>()
    val distanceMap: Map<Int, Long>
        get() = _distanceMap

    private var _destinationBlockers = mutableMapOf<Int, List<Int>>()
    val destinationBlockers: Map<Int, List<Int>>
        get() = _destinationBlockers

    var neighbors = emptyList<Location>()

    fun generateDataMaps() {
        _distanceMap.clear()
        _destinationBlockers.clear()
        takeStep(this)
    }

    private fun takeStep(
        currentLocation: Location,
        distance: Long = 0L,
        stepsSoFar: List<Location> = emptyList()
    ) {
        val nextStepsSoFar = stepsSoFar + currentLocation
        val nextDistance = distance + 1L

        if (currentLocation !is PassthroughLocation) {
            _distanceMap[currentLocation.id] = distance
            _destinationBlockers[currentLocation.id] = nextStepsSoFar.map { it.id }
                .filter { it != id && it >= 0 }
        }

        for (neighbor in currentLocation.neighbors) {
            if (neighbor !in stepsSoFar) {
                takeStep(neighbor, nextDistance, nextStepsSoFar)
            }
        }
    }
}

class Hallway(id: Int) : Location(id)

class SideRoom(id: Int) : Location(id)

class PassthroughLocation : Location(-1)

fun createPart1Map(): Pair<List<Location>, Map<PodType, List<Int>>> {
    // ##################################
    // #00 01 .. 04 .. 07 .. 10 .. 13 14#
    // #######02####05####08####11#######
    //       #03#  #06#  #09#  #12#
    //       ####  ####  ####  ####
    val locations = listOf(
        Hallway(id = 0),
        Hallway(id = 1),
        SideRoom(id = 2),
        SideRoom(id = 3),
        Hallway(id = 4),
        SideRoom(id = 5),
        SideRoom(id = 6),
        Hallway(id = 7),
        SideRoom(id = 8),
        SideRoom(id = 9),
        Hallway(id = 10),
        SideRoom(id = 11),
        SideRoom(id = 12),
        Hallway(id = 13),
        Hallway(id = 14)
    )

    val passthroughLocations = listOf(
        PassthroughLocation().apply { neighbors = listOf(locations[1], locations[2], locations[4]) },
        PassthroughLocation().apply { neighbors = listOf(locations[4], locations[5], locations[7]) },
        PassthroughLocation().apply { neighbors = listOf(locations[7], locations[8], locations[10]) },
        PassthroughLocation().apply { neighbors = listOf(locations[10], locations[11], locations[13]) },
    )

    locations[0].neighbors = listOf(locations[1])
    locations[1].neighbors = listOf(locations[0], passthroughLocations[0])
    locations[2].neighbors = listOf(locations[3], passthroughLocations[0])
    locations[3].neighbors = listOf(locations[2])
    locations[4].neighbors = listOf(passthroughLocations[0], passthroughLocations[1])
    locations[5].neighbors = listOf(locations[6], passthroughLocations[1])
    locations[6].neighbors = listOf(locations[5])
    locations[7].neighbors = listOf(passthroughLocations[1], passthroughLocations[2])
    locations[8].neighbors = listOf(locations[9], passthroughLocations[2])
    locations[9].neighbors = listOf(locations[8])
    locations[10].neighbors = listOf(passthroughLocations[2], passthroughLocations[3])
    locations[11].neighbors = listOf(locations[12], passthroughLocations[3])
    locations[12].neighbors = listOf(locations[11])
    locations[13].neighbors = listOf(locations[14], passthroughLocations[3])
    locations[14].neighbors = listOf(locations[13])

    for (location in locations) {
        location.generateDataMaps()
    }

    // homes must be ordered from bottom to top
    val homesByType = mapOf(
        PodType.Amber to listOf(3, 2),
        PodType.Bronze to listOf(6, 5),
        PodType.Copper to listOf(9, 8),
        PodType.Desert to listOf(12, 11)
    )

    return Pair(locations, homesByType)
}

fun createPart2Map(): Pair<List<Location>, Map<PodType, List<Int>>> {
    // ##################################
    // #00 01 .. 04 .. 07 .. 10 .. 13 14#
    // #######02####05####08####11#######
    //     [ #15#  #17#  #19#  #21# ]
    //     [ #16#  #18#  #20#  #22# ]
    //       #03#  #06#  #09#  #12#
    //       ####  ####  ####  ####
    val locations = listOf(
        Hallway(id = 0),
        Hallway(id = 1),
        SideRoom(id = 2),
        SideRoom(id = 15),
        SideRoom(id = 16),
        SideRoom(id = 3),
        Hallway(id = 4),
        SideRoom(id = 5),
        SideRoom(id = 17),
        SideRoom(id = 18),
        SideRoom(id = 6),
        Hallway(id = 7),
        SideRoom(id = 8),
        SideRoom(id = 19),
        SideRoom(id = 20),
        SideRoom(id = 9),
        Hallway(id = 10),
        SideRoom(id = 11),
        SideRoom(id = 21),
        SideRoom(id = 22),
        SideRoom(id = 12),
        Hallway(id = 13),
        Hallway(id = 14)
    ).sortedBy { it.id }

    val passthroughLocations = listOf(
        PassthroughLocation().apply { neighbors = listOf(locations[1], locations[2], locations[4]) },
        PassthroughLocation().apply { neighbors = listOf(locations[4], locations[5], locations[7]) },
        PassthroughLocation().apply { neighbors = listOf(locations[7], locations[8], locations[10]) },
        PassthroughLocation().apply { neighbors = listOf(locations[10], locations[11], locations[13]) },
    )

    locations[0].neighbors = listOf(locations[1])
    locations[1].neighbors = listOf(locations[0], passthroughLocations[0])
    locations[2].neighbors = listOf(locations[15], passthroughLocations[0])
    locations[15].neighbors = listOf(locations[2], locations[16])
    locations[16].neighbors = listOf(locations[15], locations[3])
    locations[3].neighbors = listOf(locations[16])
    locations[4].neighbors = listOf(passthroughLocations[0], passthroughLocations[1])
    locations[5].neighbors = listOf(locations[17], passthroughLocations[1])
    locations[17].neighbors = listOf(locations[5], locations[18])
    locations[18].neighbors = listOf(locations[17], locations[6])
    locations[6].neighbors = listOf(locations[18])
    locations[7].neighbors = listOf(passthroughLocations[1], passthroughLocations[2])
    locations[8].neighbors = listOf(locations[19], passthroughLocations[2])
    locations[19].neighbors = listOf(locations[8], locations[20])
    locations[20].neighbors = listOf(locations[19], locations[9])
    locations[9].neighbors = listOf(locations[20])
    locations[10].neighbors = listOf(passthroughLocations[2], passthroughLocations[3])
    locations[11].neighbors = listOf(locations[21], passthroughLocations[3])
    locations[21].neighbors = listOf(locations[11], locations[22])
    locations[22].neighbors = listOf(locations[21], locations[12])
    locations[12].neighbors = listOf(locations[22])
    locations[13].neighbors = listOf(locations[14], passthroughLocations[3])
    locations[14].neighbors = listOf(locations[13])

    for (location in locations) {
        location.generateDataMaps()
    }

    // homes must be ordered from bottom to top
    val homesByType = mapOf(
        PodType.Amber to listOf(3, 16, 15, 2),
        PodType.Bronze to listOf(6, 18, 17, 5),
        PodType.Copper to listOf(9, 20, 19, 8),
        PodType.Desert to listOf(12, 22, 21, 11)
    )

    return Pair(locations, homesByType)
}

enum class PodType(val energyPerMove: Long) {
    Amber(energyPerMove = 1L),
    Bronze(energyPerMove = 10L),
    Copper(energyPerMove = 100L),
    Desert(energyPerMove = 1000L)
}

data class PodState(val type: PodType, val locationId: Int) {
    fun isHome(allPods: List<PodState>, homesByType: Map<PodType, List<Int>>): Boolean {
        val homeLocations = homesByType[type]
            ?: throw IllegalStateException("No homes found for type $type")
        if (locationId !in homeLocations) {
            return false
        }
        val hasMismatchedNeighbors = homeLocations.any { homeId ->
            val pod = allPods.find { it.locationId == homeId }
            pod != null && pod.type != type
        }
        return !hasMismatchedNeighbors
    }
}

data class BurrowState(val pods: List<PodState>) {
    fun getNextValidStates(
        locations: List<Location>,
        homesByType: Map<PodType, List<Int>>
    ): List<Pair<BurrowState, Long>> {
        val nextStates = mutableListOf<Pair<BurrowState, Long>>()

        fun checkDestination(pod: PodState, destinationId: Int): Boolean {
            if (pod.locationId != destinationId && pod.canMoveTo(destinationId, locations)) {
                val (movedPod, newState) = createNewState(pod, destinationId)
                if (locations[destinationId] is SideRoom && !movedPod.isHome(newState.pods, homesByType)) {
                    return false
                }
                val distance = locations[pod.locationId].distanceMap[destinationId]
                    ?: throw IllegalStateException("No distance from ${pod.locationId} to $destinationId")
                val energyCost = distance * pod.type.energyPerMove
                nextStates.add(Pair(newState, energyCost))
                return true
            }
            return false
        }

        for (pod in pods) {
            if (pod.isHome(pods, homesByType)) {
                continue
            }

            val homeLocations = homesByType[pod.type]
                ?: throw IllegalStateException("No homes for type ${pod.type}")

            // a pod can always move to its home if available
            var homeAvailable = false
            for (homeId in homeLocations) {
                if (checkDestination(pod, homeId)) {
                    // only consider the lowest available home location
                    homeAvailable = true
                    break
                }
            }

            // a pod can only move to the hallway if in a side room
            if (!homeAvailable && locations[pod.locationId] is SideRoom) {
                for (location in locations) {
                    if (location is Hallway) {
                        checkDestination(pod, location.id)
                    }
                }
            }
        }
        return nextStates
    }

    private fun createNewState(movingPod: PodState, newLocationId: Int): Pair<PodState, BurrowState> {
        val movedState = PodState(movingPod.type, newLocationId)
        return Pair(
            movedState,
            BurrowState(
                pods.map {
                    if (it == movingPod) {
                        movedState
                    } else {
                        it
                    }
                }
            )
        )
    }

    private fun PodState.canMoveTo(destinationId: Int, locations: List<Location>): Boolean {
        val blockers = locations[locationId].destinationBlockers[destinationId]
            ?: throw IllegalStateException("Missing destination blockers for location $locationId")
        return blockers.none { blocker -> pods.any { it.locationId == blocker } }
    }
}

fun List<String>.toInitialState(): BurrowState {
    val pods = mutableListOf<PodState>()

    fun addPodStateFrom(lineIndex: Int, charIndex: Int, locationId: Int) {
        val pod = when (val c = getOrNull(lineIndex)?.getOrNull(charIndex)) {
            'A' -> PodState(PodType.Amber, locationId)
            'B' -> PodState(PodType.Bronze, locationId)
            'C' -> PodState(PodType.Copper, locationId)
            'D' -> PodState(PodType.Desert, locationId)
            else -> throw IllegalArgumentException("Invalid pod definition: '$c'")
        }
        pods.add(pod)
    }

    addPodStateFrom(lineIndex = 2, charIndex = 3, locationId = 2)
    addPodStateFrom(lineIndex = 3, charIndex = 1, locationId = 3)
    addPodStateFrom(lineIndex = 2, charIndex = 5, locationId = 5)
    addPodStateFrom(lineIndex = 3, charIndex = 3, locationId = 6)
    addPodStateFrom(lineIndex = 2, charIndex = 7, locationId = 8)
    addPodStateFrom(lineIndex = 3, charIndex = 5, locationId = 9)
    addPodStateFrom(lineIndex = 2, charIndex = 9, locationId = 11)
    addPodStateFrom(lineIndex = 3, charIndex = 7, locationId = 12)
    return BurrowState(pods)
}

fun findMinimumOrganizationEnergy(
    currentState: BurrowState,
    locations: List<Location>,
    homesByType: Map<PodType, List<Int>>,
    resultsCache: MutableMap<BurrowState, Long?> = mutableMapOf()
): Long? {
    val result = resultsCache[currentState] ?:
        if (currentState.pods.all { it.isHome(currentState.pods, homesByType) }) {
            0L
        } else {
            currentState.getNextValidStates(locations, homesByType).mapNotNull { (nextState, energyCost) ->
                findMinimumOrganizationEnergy(nextState, locations, homesByType, resultsCache)?.let { energyCost + it }
            }.minOrNull()
        }
    resultsCache[currentState] = result
    return result
}

fun getPart1Answer(initialState: BurrowState): Long? {
    val (locations, homesByType) = createPart1Map()
    return findMinimumOrganizationEnergy(initialState, locations, homesByType)
}

fun getPart2Answer(initialState: BurrowState): Long? {
    val modifiedPods =initialState.pods + listOf(
        PodState(PodType.Desert, locationId = 15),
        PodState(PodType.Desert, locationId = 16),
        PodState(PodType.Copper, locationId = 17),
        PodState(PodType.Bronze, locationId = 18),
        PodState(PodType.Bronze, locationId = 19),
        PodState(PodType.Amber, locationId = 20),
        PodState(PodType.Amber, locationId = 21),
        PodState(PodType.Copper, locationId = 22)
    )
    val modifiedState = BurrowState(modifiedPods)
    val (locations, homesByType) = createPart2Map()
    return findMinimumOrganizationEnergy(modifiedState, locations, homesByType)
}

fun main() {
    val initialState = File("src/main/resources/day23.txt")
        .getInputLines().toInitialState()

    println("The answer to part 1 is ${getPart1Answer(initialState)}")
    println("The answer to part 2 is ${getPart2Answer(initialState)}")
}
