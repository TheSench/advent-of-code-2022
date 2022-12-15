package day15

import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) = input

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 24,
        part2 = ::part2,
        part2Check = 93,
    )
}

data class ParsedLine(val sensor: Point, val beacon: Point)

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
