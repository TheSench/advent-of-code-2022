package day11

import groupByBlanks
import runDay

fun main() {
    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}

internal fun List<String>.toMonkeys() =
    groupByBlanks().map { it.toMonkey() }

internal fun List<String>.toMonkey() = Monkey(
    items = this[1].toStartingItems(),
    operation = this[2].toOperation(),
    testDivisibleBy = this[3].toTestDivisibleBy(),
    ifTrueTarget = this[4].toIfTrueTarget(),
    ifFalseTarget = this[5].toIfFalseTarget(),
)

private const val STARTING_ITEMS = "Starting items: "
private fun String.toStartingItems() =
    this.substringAfter(STARTING_ITEMS).split(',')
        .map { it.trim().toInt() }
        .toMutableList()

private const val TEST_DIVISIBLE_BY = "  Test: divisible by "
private fun String.toTestDivisibleBy() =
    this.substringAfter(TEST_DIVISIBLE_BY).toInt()


private const val OPERATION = "  Operation: new = old "
private fun String.toOperation() =
    this.substringAfter(OPERATION)
        .split(" ", limit = 2)
        .let {
            val value = it.last()
            when (it.last()) {
                "old" -> when (it.first()) {
                    "+" -> DoubleIt
                    "*" -> SquareIt
                    else -> throw IllegalArgumentException(it.first())
                }

                else -> when (it.first()) {
                    "+" -> Add(value.toInt())
                    "*" -> Multiply(value.toInt())
                    else -> throw IllegalArgumentException(it.first())
                }
            }
        }

private const val IF_TRUE_TARGET = "    If true: throw to monkey "
private fun String.toIfTrueTarget() =
    this.substringAfter(IF_TRUE_TARGET).toInt()

private const val IF_FALSE_TARGET = "    If false: throw to monkey "
private fun String.toIfFalseTarget() =
    this.substringAfter(IF_FALSE_TARGET).toInt()

