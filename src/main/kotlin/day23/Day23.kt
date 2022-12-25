package day23

import runDay
import utils.Point
import utils.Ring

fun main() {

    fun part1(input: List<String>) =
        input.toElves()
            .let { allElves ->
                var positions = allElves
                var movesToTry = possibleMoves
                repeat(10) { i ->
                    positions = positions.fold(mutableMapOf<Elf, List<Elf>>()) { attempts, elf ->
                        val next = elf.attemptToMove(positions, movesToTry)
                        attempts.compute(next) { _, elves ->
                            when (elves) {
                                null -> listOf(elf)
                                else -> elves + elf
                            }
                        }
                        attempts
                    }.flatMap { (next, originals) ->
                        when (originals.size) {
                            1 -> listOf(next)
                            else -> originals
                        }

                    }.toSet()
                    movesToTry = movesToTry.next
                }
                positions
            }.let { elves ->
                val min = Point(
                    x = elves.minOf { it.x },
                    y = elves.minOf { it.y },
                )
                val max = Point(
                    x = elves.maxOf { it.x },
                    y = elves.maxOf { it.y },
                )
                (max.x - min.x + 1) * (max.y - min.y + 1) - elves.size
            }

    fun part2(input: List<String>) =
        input.toElves()
            .let { allElves ->
                var positions = allElves
                var movesToTry = possibleMoves
                repeat(Int.MAX_VALUE) { i ->
                    val oldPositions = positions
                    positions = positions.fold(mutableMapOf<Elf, List<Elf>>()) { attempts, elf ->
                        val next = elf.attemptToMove(positions, movesToTry)
                        attempts.compute(next) { _, elves ->
                            when (elves) {
                                null -> listOf(elf)
                                else -> elves + elf
                            }
                        }
                        attempts
                    }.flatMap { (next, originals) ->
                        when (originals.size) {
                            1 -> listOf(next)
                            else -> originals
                        }

                    }.toSet()
                    movesToTry = movesToTry.next
                    if (oldPositions.intersect(positions) == positions) {
                        return i + 1
                    }
                }
                Int.MAX_VALUE
            }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 110,
        part2 = ::part2,
        part2Check = 20,
    )
}

val possibleMoves = Ring(
    MovementAttempt(Elf::noneNorth) { copy(y = y - 1) },
    MovementAttempt(Elf::noneSouth) { copy(y = y + 1) },
    MovementAttempt(Elf::noneWest) { copy(x = x - 1) },
    MovementAttempt(Elf::noneEast) { copy(x = x + 1) },
)

fun Elf.attemptToMove(allElves: Set<Elf>, possibilities: Ring<MovementAttempt>): Elf {
    if (this.isAlone(allElves)) return this

    for (attempt in possibilities) {
        return attempt(this, allElves) ?: continue
    }
    return this
}

typealias Elf = Point

data class MovementAttempt(val check: Elf.(Set<Elf>) -> Boolean, val move: Elf.() -> Elf) {
    operator fun invoke(elf: Elf, allElves: Set<Elf>): Elf? = when (elf.check(allElves)) {
        true -> elf.move()
        false -> null
    }
}

fun Elf.isAlone(allElves: Set<Elf>) = listOf(
    Elf(x - 1, y - 1),
    Elf(x, y - 1),
    Elf(x + 1, y - 1),
    Elf(x + 1, y),
    Elf(x + 1, y + 1),
    Elf(x, y + 1),
    Elf(x - 1, y + 1),
    Elf(x - 1, y),
).intersect(allElves).none()

fun Elf.noneNorth(allElves: Set<Elf>) = listOf(
    Elf(x - 1, y - 1),
    Elf(x, y - 1),
    Elf(x + 1, y - 1),
).intersect(allElves).none()

fun Elf.noneSouth(allElves: Set<Point>) = listOf(
    Elf(x - 1, y + 1),
    Elf(x, y + 1),
    Elf(x + 1, y + 1),
).intersect(allElves).none()

fun Elf.noneWest(allElves: Set<Point>) = listOf(
    Elf(x - 1, y - 1),
    Elf(x - 1, y),
    Elf(x - 1, y + 1),
).intersect(allElves).none()

fun Elf.noneEast(allElves: Set<Point>) = listOf(
    Elf(x + 1, y - 1),
    Elf(x + 1, y),
    Elf(x + 1, y + 1),
).intersect(allElves).none()


fun List<String>.toElves() = flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, cell ->
        when (cell) {
            '#' -> Point(x, y)
            else -> null
        }
    }
}.toSet()