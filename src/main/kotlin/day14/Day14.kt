package day14

import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) =
        input
            .map { it.toLine() }
            .toGrid()
            .dropSand()
            .size

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 24,
        part2 = ::part2,
        part2Check = 140,
    )
}

typealias Line = List<Point>
typealias Lines = List<Line>
typealias Grid = Set<Point>

fun String.toLine() = split(" -> ")
    .map {
        it.split(",", limit = 2)
            .map { num -> num.toInt() }
    }
    .map { nums -> Point(nums.first(), nums.last()) }

fun Lines.toGrid(): Set<Point> {
    val grid = mutableSetOf<Point>()
    this.forEach { it.addToGrid(grid) }
    return grid
}

fun Line.addToGrid(grid: MutableSet<Point>) {
    this.windowed(2).forEach {
        (it.first()..it.last()).forEach { point ->
            grid.add(point)
        }
    }
}

fun Grid.dropSand(start: Point = Point(500, 0)): Set<Point> {
    val stones = this
    val sand = mutableSetOf<Point>()
    val maxY = stones.maxOf { it.y }
    val pointsToRevisit = ArrayDeque<Point>()
    var pointConsidering = start
    while (pointConsidering.y < maxY) {
        val next = pointConsidering.next(stones, sand)
        pointConsidering = when (next) {
            null -> {
                sand.add(pointConsidering)
                pointsToRevisit.removeFirst()
            }

            else -> {
                pointsToRevisit.addFirst(pointConsidering)
                next
            }
        }
    }
    return sand
}

fun Point.next(stones: Grid, sand: Grid) = listOf(
    this + Point(0, 1),
    this + Point(-1, 1),
    this + Point(1, 1),
).firstOrNull { it !in stones && it !in sand }
