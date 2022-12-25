package day24

import runDay
import stackOf
import utils.Point

fun main() {

    fun part1(input: List<String>) = input.parse().let { (boundaries, blizzards) ->
        val positions = stackOf(Point(1, 0))
        while (positions.isNotEmpty()) {
            blizzards
        }
        0
    }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun List<String>.parse() = getBoundaries() to toBlizzards()

fun List<String>.getBoundaries() = Bounds(
    width = this[0].length,
    height = this.size,
)

fun List<String>.toBlizzards() = flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, char ->
        when (char) {
            '^' -> Blizzard.up(x, y)
            'v' -> Blizzard.down(x, y)
            '<' -> Blizzard.left(x, y)
            '>' -> Blizzard.right(x, y)
            else -> null
        }
    }
}

data class Bounds(val width: Int, val height: Int)

data class Blizzard(val location: Point, val velocity: Point) {
    val x get() = location.x
    val y get() = location.y

    fun move() = copy(location = location + velocity)

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