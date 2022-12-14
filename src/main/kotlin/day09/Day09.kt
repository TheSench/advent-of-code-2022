package day09

import runDay
import toUnit
import utils.Point
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>) =
        input.toMoves()
            .fold(Rope(2)) { path, next ->
                // TODO: Condense
                repeat(next.distance) {
                    path.moveHead(next.direction)
                }
                path
            }.tailVisited.size

    fun part2(input: List<String>) =
        input.toMoves()
            .fold(Rope(10)) { path, next ->
                // TODO: Condense
                repeat(next.distance) {
                    path.moveHead(next.direction)
                }
                path
            }.tailVisited.size


    (object {}).runDay(
        part1 = ::part1,
        part1Check = 13,
        part2 = ::part2,
        part2Check = 36,
    )
}

private data class Move(val direction: Point, val distance: Int)

private fun abs(point: Point) = Point(
    x = abs(point.x),
    y = abs(point.y),
)

private class Rope(
    knots: Int = 2,
) {
    private var points: MutableList<Point> = MutableList(
        knots
    ) { Point(0, 0) }
    val tailVisited = mutableSetOf(points.last())

    fun moveHead(point: Point) {
        points[0] += point
        points.indices.windowed(2).forEach { (head, tail) ->
            points[tail] = moveTail(points[head], points[tail])
        }
        tailVisited.add(points.last())
    }
}

private fun moveTail(head: Point, tail: Point): Point {
    val distance = head - tail
    val absoluteDistance = abs(distance)
    if (absoluteDistance.x > 1 || absoluteDistance.y > 1) {
        val dX = absoluteDistance.x
        val dY = absoluteDistance.y
        return tail + when {
            dX > 0 && dY > 0 -> Point(distance.x.toUnit(), distance.y.toUnit())
            dX > 0 -> Point(distance.x.toUnit(), 0)
            else -> Point(0, distance.y.toUnit())
        }
    }
    return tail
}

private typealias Direction = String

private fun Direction.toPoint() = when (this) {
    "L" -> Point(-1, 0)
    "R" -> Point(1, 0)
    "U" -> Point(0, 1)
    "D" -> Point(0, -1)
    else -> throw IllegalArgumentException(this)
}

private fun List<String>.toMoves() = map {
    it.split(' ', limit = 2)
        .let { pair ->
            Move(
                direction = pair.first().toPoint(),
                distance = pair.last().toInt()
            )
        }
}