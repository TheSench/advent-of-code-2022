package day24

import runDay
import utils.Point

fun main() {

    fun part1(input: List<String>) = input.parse().let { (bounds, blizzards) ->
        solve(bounds, blizzards, bounds.start, bounds.end).first
    }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 18,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun solve(bounds: Bounds, blizzards: List<Blizzard>, start: Point, goal: Point): Pair<Int, List<Blizzard>> {
    var positions = listOf(start)
    var currentBlizzards = blizzards
    val lcm = lcm(bounds.width, bounds.height)
    val seen = mutableSetOf(0 to start)
    repeat(Int.MAX_VALUE) { turn ->
        currentBlizzards = currentBlizzards.move(bounds)
        val blizzardLocations = currentBlizzards.map { it.location }.toSet()
        positions = positions.flatMap { location ->
            location.getMoves().filter {
                it in bounds && location !in blizzardLocations
            }.filter {
                (turn % lcm) to it !in seen
            }.also { moves ->
                if (moves.any { it == goal }) {
                    return (turn + 2) to currentBlizzards
                }
                moves.forEach {
                    seen.add((turn % lcm) to it)
                }
            }
        }
    }
    return Int.MAX_VALUE to currentBlizzards
}

fun debug(turn: Int, bounds: Bounds, allBlizzards: List<Blizzard>) {
    val blizzardLocations = allBlizzards.groupBy { it.location }
    val map = (0..bounds.height).map { y ->
        (0..bounds.width).map { x ->
            val point = Point(x, y)
            when {
                x == 0 || y == 0 || x == bounds.width || y == bounds.height -> '#'
                point in blizzardLocations -> {
                    val blizzards = blizzardLocations[point]!!
                    when (blizzards.size) {
                        1 -> blizzards[0].toString()
                        in (2..9) -> blizzards.size
                        else -> '@'
                    }
                }

                else -> ' '
            }
        }.joinToString("")
    }.joinToString("\n")

    println("After turn ${turn + 1}")
    println(map)
    println()
}

operator fun Bounds.contains(point: Point) =
    (point.x in (1 until width) && point.y in (1 until height))
            || point == start
            || point == end

fun Point.getMoves() = listOf(
    copy(x = x + 1),
    copy(x = x - 1),
    copy(y = y + 1),
    copy(y = y - 1),
    this,
)

fun List<String>.parse() = getBoundaries() to toBlizzards()

fun List<String>.getBoundaries() = Bounds(
    width = this[0].length - 1,
    height = this.size - 1,
)

fun List<String>.toBlizzards() = flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, char ->
        when (char) {
            '^' -> Blizzard.up(x, y)
            'v' -> Blizzard.down(x, y)
            '<' -> Blizzard.left(x, y)
            '>' -> Blizzard.right(x, y)
            '#', '.' -> null
            else -> throw IllegalArgumentException("$char")
        }
    }
}

data class Bounds(val width: Int, val height: Int) {
    val start = Point(1, 0)
    val end = Point(width - 1, height)
}

data class Blizzard(val location: Point, val velocity: Point) {
    val x get() = location.x
    val y get() = location.y

    fun move() = copy(location = location + velocity)

    override fun toString(): String = when {
        velocity.x > 0 -> ">"
        velocity.x < 0 -> "<"
        velocity.y > 0 -> "v"
        velocity.y < 0 -> "^"
        else -> "A"
    }

    companion object {
        fun up(x: Int, y: Int) = Blizzard(Point(x, y), Point(0, -1))
        fun down(x: Int, y: Int) = Blizzard(Point(x, y), Point(0, 1))
        fun left(x: Int, y: Int) = Blizzard(Point(x, y), Point(-1, 0))
        fun right(x: Int, y: Int) = Blizzard(Point(x, y), Point(1, 0))
    }
}

fun List<Blizzard>.move(bounds: Bounds) = map { it.move() }.map {
    when {
        it.x == 0 -> it.run { copy(location = Point(bounds.width - 1, y)) }
        it.y == 0 -> it.run { copy(location = Point(x, bounds.height - 1)) }
        it.x == bounds.width -> it.run { copy(location = Point(1, y)) }
        it.y == bounds.height -> it.run { copy(location = Point(x, 1)) }
        else -> it
    }
}

private fun gcd(a: Int, b: Int): Int {
    var a = a
    var b = b
    while (b > 0) {
        val temp = b
        b = a % b // % is remainder
        a = temp
    }
    return a
}

private fun lcm(a: Int, b: Int): Int = a * (b / gcd(a, b))
