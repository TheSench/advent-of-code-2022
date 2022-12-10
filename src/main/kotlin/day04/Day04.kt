package day04

import runDay
import toPair

fun main() {
    fun part1(input: List<String>): Int {
        return input.mapToPairsOfRanges()
            .count { it.oneFullyContainsOther() }
    }

    fun part2(input: List<String>): Int {
        return input.mapToPairsOfRanges()
            .count { it.overlap() }
    }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 2,
        part2 = ::part2,
        part2Check = 4
    )
}

private fun List<String>.mapToPairsOfRanges() = map { it.split(",") }
    .map { pair -> pair.map(String::toRange).toPair() }

private fun String.toRange() = split("-")
    .let {
        val left = it[0].toInt()
        val right = it[1].toInt()
        left..right
    }

private fun Pair<IntRange, IntRange>.oneFullyContainsOther() =
    (first.contains((second)) || second.contains(first))

private fun IntRange.contains(other: IntRange) =
    first <= other.first && last >= other.last

private fun Pair<IntRange, IntRange>.overlap() =
    first.intersect(second).isNotEmpty()
