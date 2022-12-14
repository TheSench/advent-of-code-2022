package day14

import runDay
import utils.Point

fun main() {
    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 13,
        part2 = ::part2,
        part2Check = 140,
    )
}

fun String.toLines() = split(" -> ")
    .map {
        it.split(",", limit = 2)
            .map { num -> num.toInt() }
    }
    .map { nums -> Point(nums.first(), nums.last()) }
