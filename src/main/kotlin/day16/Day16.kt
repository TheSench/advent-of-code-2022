package day16

import runDay

fun main() {
    fun part1(input: List<String>) = input.parse()
        .let { rooms ->
            val stack = listOf(
                RoomState(emptySet(), listOf("AA"))
            )
            (1..30).fold(stack) { statesAtThisStep, i ->
                println(i)
                statesAtThisStep.flatMap { it.getTransitions(rooms) }
                    .distinct().sortedByDescending { it.relieved + it.relieving }.take(500)
            }.maxOf { it.relieved }
        }

    fun part2(input: List<String>) = input.parse()
        .let { rooms ->
            val stack = listOf(
                RoomState(emptySet(), listOf("AA", "AA"))
            )
            (1..26).fold(stack) { statesAtThisStep, i ->
                println(i)
                statesAtThisStep.flatMap { it.getTransitions(rooms) }
                    .distinct().sortedByDescending { it.relieved + it.relieving }.take(500)
            }.maxOf { it.relieved }
        }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 1651,
        part2 = ::part2,
        part2Check = 1707,
    )
}

data class Room(
    val name: String,
    val flowRate: Int,
    val connected: List<String>,
)

fun List<String>.parse() = map { it.toRoom() }.associateBy { it -> it.name }

val roomRegex = Regex("""Valve (\w+) has flow rate=([\d-]+); tunnels? leads? to valves? (.*)""")
fun String.toRoom(): Room {
    val match = roomRegex.matchEntire(this)
    val (
        name,
        rate,
        connected,
    ) = match!!.destructured
    return Room(
        name,
        rate.toInt(),
        connected.split(", ")
    )
}

typealias Rooms = Map<String, Room>

fun RoomState.getTransitions(rooms: Rooms): List<RoomState> {
    val after1Minute = copy(
        current = emptyList(),
        relieved = relieved + relieving
    )
    return current.fold(listOf(after1Minute)) { states, currentRoom ->
        states.flatMap { getTransitions(it, currentRoom, rooms) }
    }.map {
        it.copy(current = it.current.sorted())
    }
}

fun getTransitions(baseState: RoomState, currentRoom: String, rooms: Rooms): List<RoomState> {
    val room = rooms[currentRoom]
    val options = room!!.connected.map { newRoom ->
        baseState.copy(
            current = baseState.current + newRoom,
        )
    }
    return if (baseState.enabledRooms.contains(currentRoom)) {
        options
    } else {
        options + baseState.copy(
            current = baseState.current + currentRoom,
            enabledRooms = baseState.enabledRooms + currentRoom,
            relieving = baseState.relieving + room.flowRate,
        )
    }
}

data class RoomState(
    val enabledRooms: Set<String>,
    val current: List<String>,
    val relieving: Int = 0,
    val relieved: Int = 0,
)