package day18

import runDay
import stackOf
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun part1(input: List<String>): Long {
        val grid = mutableSetOf<Point3D>()
        var sides = 0L
        input.map { it.toPoint() }
            .forEach {
                grid.add(it)
                sides += 6 - it.neighbors()
                    .filter { neighbor -> neighbor in grid }
                    .sumOf { 2L }
            }
        return sides
    }

    fun part2(input: List<String>): Long {
        val grid = mutableSetOf<Point3D>()
        var sides = 0L
        var min = Point3D(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        var max = Point3D(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
        input.map { it.toPoint() }
            .forEach {
                grid.add(it)
                min = Point3D(min(it.x, min.x), min(it.y, min.y), min(it.z, min.z))
                max = Point3D(max(it.x, max.x), max(it.y, max.y), max(it.z, max.z))
                sides += 6 - it.neighbors()
                    .filter { neighbor -> neighbor in grid }
                    .sumOf { 2L }
            }
        grid.print(min, max)
        val touchesOutside = mutableSetOf<Point3D>()
        val xBounds = min.x+1 until max.x
        val yBounds = min.y+1 until max.y
        val zBounds = min.z+1 until max.z
        // 2528 .. 2540
        (xBounds).forEach { x ->
            (yBounds).forEach { y ->
                (zBounds).forEach { z ->
                    val point = Point3D(x, y, z)
                    if (point !in grid && point !in touchesOutside) {
                        val stack = stackOf(point)
                        val bubble = mutableSetOf(point)
                        while (stack.size > 0) {
                            val next = stack.removeFirst()
                            next.neighbors().filter { it !in grid && it !in bubble }.forEach {
                                stack.addFirst(it)
                                bubble.add(it)
                            }
                            if (next in touchesOutside || next.x !in xBounds || next.y !in yBounds || next.z !in zBounds) {
                                touchesOutside.add(point)
                                break
                            }
                        }
                        if (point !in touchesOutside) {
                            bubble.forEach { pointInBubble ->
                                grid.add(pointInBubble)
                                sides += 6 - pointInBubble.neighbors()
                                    .filter { neighbor -> neighbor in grid }
                                    .sumOf { 2L }
                            }
                        } else {
                            bubble.forEach { pointInBubble ->
                                touchesOutside.add(pointInBubble)
                            }
                        }
                    }
                }
            }
        }
//        grid.print(min, max)

        return sides
    }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 64L,
        part2 = ::part2,
        part2Check = 58L,
    )
}

fun Grid.print(min: Point3D, max: Point3D) =
    List(max.x - min.x + 1) { x ->
        List(max.y - min.y + 1) { y ->
            List(max.z - min.z + 1) { z ->
                val point = Point3D(x + min.x, y + min.y, z + min.z)
                point in this
            }
        }
    }.print()


fun List<List<List<Boolean>>>.print() = forEach { square ->
    square.forEach { row ->
        println(row.map { if (it) '#' else '.' }.joinToString(""))
    }
    println()
}

fun String.toPoint() = split(",")
    .map { it.toInt() }
    .let {
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
) {
    operator fun plus(other: Point3D) = Point3D(
        x = this.x + other.x,
        y = this.y + other.y,
        z = this.z + other.z,
    )
}

fun Point3D.neighbors() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
    copy(z = z - 1),
    copy(z = z + 1),
)

typealias Grid = MutableSet<Point3D>