package day17

import day17.JetDirection.LEFT
import day17.JetDirection.RIGHT
import groupByBlanks
import runDay
import utils.RepeatingIterator

fun main() {
    fun List<String>.simulate(num: Long) =
        map {
            it.map(JetDirection::fromChar)
        }.map(::RepeatingIterator)
            .first()
            .let { jetDirections ->
                val cave = Cave()
                val rocks = RepeatingIterator(rockPattern)
                val memoized = mutableMapOf<Long, Pair<Long, Long>>()
                var i = 0L
                val end = num
                var heightOffset = 0L
                while (i < end) {
                    i++
                    cave.addRock(rocks.next(), jetDirections)
                    val signature = cave.signature(rocks, jetDirections)
                    if (memoized.containsKey(signature)) {
                        val (prevI, prevSize) = memoized[signature]!!
                        val diffI = i - prevI
                        val diffSize = cave.fullSize - prevSize
                        val iterations = (end - i) / diffI
                        i += iterations * diffI
                        heightOffset = iterations * diffSize
                        break
                    }
                    memoized[signature] = i to cave.fullSize
                }
                while (i < end) {
                    cave.addRock(rocks.next(), jetDirections)
                    i++
                }
                cave.fullSize + heightOffset
            }

    fun part1(input: List<String>) = input.simulate(2_022L)

    fun part2(input: List<String>) = input.simulate(1_000_000_000_000L)

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 3068L,
        part2 = ::part2,
        part2Check = 1514285714288L,
    )
}

typealias Rock = List<IntRange>
typealias Rocks = RepeatingIterator<Rock>
typealias JetDirections = RepeatingIterator<JetDirection>

class Cave(
    private val chamber: MutableList<MutableSet<Int>> = mutableListOf()
) : MutableList<MutableSet<Int>> by chamber {
    val fullSize: Long get() = chamber.size.toLong()

    private fun getTop8() = chamber.reversed().take(8).flatMap { row ->
        (0..6).map {
            row.contains(it)
        }
    }.fold(0L) { bits, next ->
        bits * 2 + if (next) 1 else 0
    }

    fun signature(rocks: Rocks, jets: JetDirections): Long =
        (getTop8() * rocks.size.toLong() +
                rocks.current.toLong()) * jets.size.toLong() +
                jets.current.toLong()
}

fun Cave.addRock(rock: Rock, jetDirections: JetDirections) {
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
