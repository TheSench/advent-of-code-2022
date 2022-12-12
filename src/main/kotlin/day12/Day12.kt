package day12

import Lines
import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}

typealias Elevations = List<List<Int>>
operator fun Elevations.get(point: Point) = this[point.y][point.x]

data class Grid(
    val start: Point,
    val end: Point,
    val elevations: Elevations,
)
fun Lines.toGrid(): Grid = this.let {
    lateinit var start: Point
    lateinit var end: Point
    val height = this.size - 1
    val elevations = mapIndexed { top, row ->
        row.mapIndexed { left, char ->
            when (char) {
                in 'a'..'z' -> char.code - 'a'.code
                'S' -> 0.also { start = Point(left, height-top) }
                'E' -> 25.also { end = Point(left, height-top) }
                else -> throw IllegalArgumentException("Invalid character: $char")
            }
        }
    }
    Grid(
        start,
        end,
        elevations,
    )
}
