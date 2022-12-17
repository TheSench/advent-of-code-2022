package day16

import runDay
import stackOf

fun main() {
    fun part1(input: List<String>) = input.parse()
        .let { rooms ->
            val stack = listOf(
                RoomState(emptySet(), "AA")
            )
            (1..30).fold(stack) { statesAtThisStep, i ->
                println(i)
                statesAtThisStep.flatMap { it.getTransitions(rooms) }
                    .distinct().sortedByDescending { it.relieved + it.relieving }.take(500)
            }.maxOf { it.relieved }
        }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 1651,
        part2 = ::part2,
        part2Check = -1,
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
    val room = rooms[current]
    val options = room!!.connected.map { newRoom ->
        copy(
            enabledRooms = enabledRooms,
            current = newRoom,
            path = path + newRoom,
            relieving = relieving,
            relieved = relieved + relieving,
        )
    }
    return if (enabledRooms.contains(current)) {
        options
    } else {
        options + copy(
            enabledRooms = enabledRooms + current,
            current = current,
            path = path + current,
            relieving = relieving + room.flowRate,
            relieved = relieved + relieving,
        )
    }
}

data class RoomState(
    val enabledRooms: Set<String>,
    val current: String,
    val path: List<String> = listOf(current),
    val relieving: Int = 0,
    val relieved: Int = 0,
)