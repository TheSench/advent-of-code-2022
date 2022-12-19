package day18

import runDay

fun main() {

    fun part1(input: List<String>): Long {
        val grid = mutableSetOf<Point3D>()
        var sides = 0L
        input.map { it.toPoint() }
            .forEach {
                grid.add(it)
                sides += 6 - it.neighbors().filter {
                    it in grid
                }.sumOf { 2L }
            }
        return sides
    }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 64L,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun String.toPoint() = split(",")
    .map { it.toInt() }
    .let{
        Point3D(
            it[0],
            it[1],
            it[2],
        )
    }

data class Point3D(
    val x: Int,
    val y: Int,
    val z: Int,
)

fun Point3D.neighbors() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
    copy(z = z - 1),
    copy(z = z + 1),
)

typealias Grid = MutableSet<Point3D>