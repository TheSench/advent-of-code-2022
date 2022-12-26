package day25

import runDay
import kotlin.math.pow

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
typealias Base10 = Long

fun SNAFU.toBase10(): Long = reversed().foldIndexed(0L) { index, base10, char ->
    val next = when (char) {
        '=' -> -2L
        '-' -> -1L
        '0' -> 0L
        '1' -> 1L
        '2' -> 2L
        else -> throw IllegalArgumentException("Invalid char: $char")
    }

    base10 + 5L.pow(index) * next
}

fun Base10.toSnafu(): String = toLong().let { base10 ->
    val chars = mutableListOf<Char>()
    var remaining = base10
    while (remaining > 0) {
        val digit = remaining % 5
        remaining = (remaining - digit) / 5
        chars.add(when (digit) {
            0L -> '0'
            1L -> '1'
            2L -> '2'
            3L -> '='.also { remaining += 1 }
            4L -> '-'.also { remaining += 1 }
            else -> throw IllegalArgumentException("Unexpected number")
        })
    }
    chars.reversed().joinToString("")
}

fun Long.pow(power: Int) = toDouble().pow(power).toLong()