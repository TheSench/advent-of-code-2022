package day17

import day17.JetDirection.LEFT
import day17.JetDirection.RIGHT
import groupByBlanks
import runDay
import utils.RepeatingIterator

fun main() {
    fun part1(input: List<String>) = input
        .map {
            it.map(JetDirection::fromChar)
        }.map(::RepeatingIterator)
        .first()
        .let { jetDirections ->
            val chamber = mutableListOf<MutableSet<Int>>()
            val rocks = RepeatingIterator(rockPattern)
            repeat(2022) {
                chamber.addRock(rocks.next(), jetDirections)
//                chamber.print()
//                repeat(5) { println() }
            }
            chamber.size
        }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 3068,
        part2 = ::part2,
        part2Check = 1514285714288,
    )
}

typealias Rock = List<IntRange>
typealias Chamber = MutableList<MutableSet<Int>>
typealias JetDirections = RepeatingIterator<JetDirection>

fun Chamber.print() = reversed()
    .forEach { row ->
        print("X")
        (0..6).forEach {
            when (it) {
                in row -> print("#")
                else -> print(".")
            }
        }
        println("X")
    }

fun Chamber.addRock(rock: Rock, jetDirections: JetDirections) {
    val chamber = this
    var y = this.size + 3
    val width = rock.maxOf { it.last } + 1
    var x = 2
    fun tryMoveLeft(x: Int): Int {
        if (x == 0) return 0
        if ((rock.indices)
                .filter { y + it < chamber.size }
                .any { yToCheck ->
                    (rock[yToCheck].first + x - 1) in chamber[yToCheck + y]
                }
        ) {
            return x
        }
        return x - 1
    }

    fun tryMoveRight(x: Int): Int {
        if (x + width == 7) return x
        if ((rock.indices)
                .filter { y + it < chamber.size }
                .any { yToCheck ->
                    (rock[yToCheck].last + x + 1) in chamber[yToCheck + y]
                }
        ) {
            return x
        }
        return x + 1
    }

    fun Int.applyGravity(): Int {
        val y = this

        if (y > chamber.size) {
            return y - 1
        } else if (y == 0) {
            return 0
        } else if ((rock.indices)
                .filter { y + it - 1 < chamber.size }
                .any {
                    val yToCheck = y + it - 1
                    chamber[yToCheck].any { xC -> xC - x in rock[it] }
                }
        ) {
            return y
        }
        return y - 1

    }

    fun Int.applyJet(jet: JetDirection) = when (jet) {
        LEFT -> tryMoveLeft(this)
        RIGHT -> tryMoveRight(this)
    }

    while (true) {
        x = x.applyJet(jetDirections.next())
        val nextY = y.applyGravity()
        if (nextY == y) {
            break
        }
        y = nextY
    }
    rock.forEachIndexed { rockY, xs ->
        val y = rockY + y
        if (y >= chamber.size) {
            chamber.add(mutableSetOf())
        }
        val row = chamber[y]
        xs.forEach { xRock ->
            row.add(x + xRock)
        }
    }
}

enum class JetDirection {
    LEFT,
    RIGHT;

    companion object {
        fun fromChar(char: Char) = when (char) {
            '<' -> LEFT
            '>' -> RIGHT
            else -> throw IllegalArgumentException()
        }
    }
}

val rockPattern = """
    ####

    .#.
    ###
    .#.

    ..#
    ..#
    ###

    #
    #
    #
    #

    ##
    ##
""".trimIndent().toRocks()

fun String.toRocks() =
    lines()
        .groupByBlanks()
        .map { it.toRock() }

fun List<String>.toRock(): Rock =
    map { row ->
        row.mapIndexedNotNull { i, char ->
            when (char) {
                '#' -> i
                else -> null
            }
        }.let {
            it.first()..it.last()
        }
    }.reversed()
