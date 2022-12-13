package day12

import Lines
import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) = input.toGrid().shortestPath()

    fun part2(input: List<String>) = input.toGrid().let {  grid ->
        grid.shortestPath(
            startingPoint = grid.end,
            isDestination = { grid[this] == 0 },
            canHandle = { it >= -1 }
        )
    }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 31,
        part2 = ::part2,
        part2Check = 29,
    )
}

typealias Elevations = List<List<Int>>

operator fun Elevations.get(point: Point) = this[point.y][point.x]

data class Grid(
    val start: Point,
    val end: Point,
    val elevations: Elevations,
) {
    operator fun get(point: Point) = elevations[point]
    operator fun contains(point: Point) = point.x in (elevations[0].indices) && point.y in elevations.indices
}

fun Lines.toGrid(): Grid = this.let {
    lateinit var start: Point
    lateinit var end: Point
    val elevations = mapIndexed { top, row ->
        row.mapIndexed { left, char ->
            when (char) {
                in 'a'..'z' -> char.code - 'a'.code
                'S' -> 0.also { start = Point(left, top) }
                'E' -> 25.also { end = Point(left, top) }
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


fun Grid.shortestPath(
    startingPoint: Point = this.start,
    isDestination: Point.() -> Boolean = { this == this@shortestPath.end },
    canHandle: (elevationChange: Int) -> Boolean = { it <= 1 }
): Int {
    val grid = this
    val visited = mutableMapOf<Point, Int>().apply { this[startingPoint] = 0 }
    val paths = ArrayDeque<Point>().apply { add(startingPoint) }
    while (paths.size > 0) {
        val point = paths.removeFirst()
        val currentElevation = grid[point]
        val newDistance = visited[point]!! + 1
        point.neighbors
            .filter {
                it in grid &&
                        (it !in visited || newDistance < visited[it]!!) &&
                        canHandle(grid[it] - currentElevation)
            }
            .forEach { nextPoint ->
                when {
                    nextPoint.isDestination() -> return newDistance
                    else -> {
                        visited[nextPoint] = newDistance
                        paths.addLast(nextPoint)
                    }
                }
            }
    }
    throw IllegalArgumentException("Grid did not contain valid path")
}

val Point.neighbors
    get() = listOf(
        this + Point(0, -1),
        this + Point(0, 1),
        this + Point(-1, 0),
        this + Point(1, 0)
    )