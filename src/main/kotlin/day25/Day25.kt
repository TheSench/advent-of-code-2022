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
typealias Base10 = Int

fun SNAFU.toBase10(): Int = reversed().foldIndexed(0) { index, base10, char ->
    val next = when (char) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> throw IllegalArgumentException("Invalid char: $char")
    }

    base10 + 5.pow(index) * next
}

fun Base10.toSnafu(): String = toInt().let { base10 ->
    val chars = mutableListOf<Char>()
    var remaining = base10
    while (remaining > 0) {
        val digit = remaining % 5
        remaining = (remaining - digit) / 5
        chars.add(when (digit) {
            0 -> '0'
            1 -> '1'
            2 -> '2'
            3 -> '='.also { remaining += 1 }
            4 -> '-'.also { remaining += 1 }
            else -> throw IllegalArgumentException("Unexpected number")
        })
    }
    chars.reversed().joinToString("")
}

fun Int.pow(power: Int) = toDouble().pow(power).toInt()