package day25

import runDay

fun main() {

    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 18,
        part2 = ::part2,
        part2Check = 54,
    )
}

typealias SNAFU = String
typealias Base10 = Int
fun SNAFU.toBase10(): Int = 0
fun Base10.toSnafu(): String = "0"
