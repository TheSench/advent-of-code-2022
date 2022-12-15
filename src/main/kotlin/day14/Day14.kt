package day14

import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) =
        input
            .map { it.toLine() }
            .toGrid()

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 13,
        part2 = ::part2,
        part2Check = 140,
    )
}

typealias Line = List<Point>
typealias Lines = List<Line>

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
