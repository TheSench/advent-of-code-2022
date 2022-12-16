package day15

import sequenceDay
import utils.Point

fun main() {
    fun part1(input: Sequence<String>) = input
        .map { it.parse() }

    fun part2(input: Sequence<String>) = 0

    (object {}).sequenceDay(
        part1 = ::part1,
        part1Check = 24,
        part2 = ::part2,
        part2Check = 93,
    )
}

data class ParsedLine(val sensor: Point, val beacon: Point) {
    private val manhattanDistance = sensor manhattanDistanceTo beacon
    val xBounds = (sensor.x - manhattanDistance)..(sensor.x + manhattanDistance)
    val yBounds = (sensor.y - manhattanDistance)..(sensor.y + manhattanDistance)
}

fun String.parse(): ParsedLine = split(": ", limit = 2).let {
    val sensor = it.first().toPoint()
    val relativeBeacon = it.last().toPoint()
    return ParsedLine(
        sensor,
        sensor + relativeBeacon,
    )
}

val pointRegex = Regex("""x=(-?\d+), y=(-?\d+)""")
fun String.toPoint(): Point = pointRegex.find(this)!!
    .destructured
    .let { (x, y) -> Point(x.toInt(), y.toInt()) }

infix fun Point.manhattanDistanceTo(other: Point) =
    (other - this).toAbsolute().run { x + y }