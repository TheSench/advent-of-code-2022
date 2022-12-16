package day15

import runDay
import utils.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        var minX: Int = Int.MAX_VALUE
        var maxX: Int = Int.MIN_VALUE
        val y = 2000000
        return input
            .map { it.parse() }
            .map {
                minX = min(minX, it.xBounds.first)
                maxX = max(maxX, it.xBounds.last)
                it
            }
            .filter { y in it.yBounds }
            .toList().sortedBy { it.xBounds.first }
            .let {
                val offsetX = max(0, 0 - minX)
                it.fold(".".repeat(maxX - minX + 1).toCharArray()) { row, coords ->
                    val narrowing = abs(y - coords.sensor.y)
                    val coveredRange = (coords.xBounds.first + narrowing)..(coords.xBounds.last - narrowing)
                    coveredRange.fold(row) { row, x ->
                        val adjX = x + offsetX
                        row[adjX] = when (row[adjX]) {
                            'B' -> 'B'
                            else -> '#'
                        }
                        row
                    }.also {
                        if (coords.beacon.y == y) {
                            row[coords.beacon.x + offsetX] = 'B'
                        }
                    }
                }
            }.count { it == '#' }
    }

    fun part2(input: List<String>): Long {
        var maxCoordX = 0
        var maxCoordY = 0
        return input
            .map { it.parse() }
            .map {
                maxCoordX = max(maxCoordX, it.xBounds.last)
                maxCoordY = max(maxCoordX, it.yBounds.last)
                it
            }
            .toList()
            .let found@{ row ->
                val maxX = min(4000000, maxCoordX)
                val maxY = min(4000000, maxCoordY)
                (0..maxY).forEach { y ->
                    row.filter { y in it.yBounds }
                        .checkForBeacon(y, maxX)?.let {
                            return@found it
                        }
                }
                Point(0, 0)
            }.let { (x, y) ->
                x.toLong() * 4000000 + y.toLong()
            }
    }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 0, // change to 26 to test output - test and actual check different rows
        part2 = ::part2,
        part2Check = 108000000L, // change to 56000011 to test output - test and actual check different rows
    )
}

fun List<ParsedLine>.checkForBeacon(y: Int, maxX: Int): Point? {
    this.map { coords ->
        val narrowing = abs(y - coords.sensor.y)
        (coords.xBounds.first + narrowing)..(coords.xBounds.last - narrowing)
    }.sortedBy { it.first }
        .let { ranges ->
            val lastX = ranges.maxOf { it.last }
            when {
                (ranges.first().first > 0) -> return Point(0, y)
                (lastX < maxX) -> return Point(lastX + 1, y)
                else -> ranges
            }
        }.fold(0) { lastX, nextRange ->
            if (nextRange.first > lastX + 1) {
                return Point(lastX + 1, y)
            }
            max(lastX, nextRange.last)
        }
    return null
}

data class ParsedLine(val sensor: Point, val beacon: Point) {
    private val manhattanDistance = sensor manhattanDistanceTo beacon
    val xBounds = (sensor.x - manhattanDistance)..(sensor.x + manhattanDistance)
    val yBounds = (sensor.y - manhattanDistance)..(sensor.y + manhattanDistance)
}

fun String.parse(): ParsedLine = split(": ", limit = 2).let {
    ParsedLine(
        it.first().toPoint(),
        it.last().toPoint(),
    )
}

val pointRegex = Regex("""x=(-?\d+), y=(-?\d+)""")
fun String.toPoint(): Point = pointRegex.find(this)!!
    .destructured
    .let { (x, y) -> Point(x.toInt(), y.toInt()) }

infix fun Point.manhattanDistanceTo(other: Point) =
    (other - this).toAbsolute().run { x + y }