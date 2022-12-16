package day16

import runDay

fun main() {
    fun part1(input: List<String>) = input.parse()

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
    val connected: List<String>
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