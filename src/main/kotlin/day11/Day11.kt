package day11

import groupByBlanks
import runDay

fun main() {
    fun part1(input: List<String>) = input.toMonkeys()
        .let { monkeys ->
            repeat(20) { processRound(monkeys) }
            monkeys.map { it.inspectedItems }
                .sortedDescending()
                .take(2)
                .reduce { a, b -> a * b }
        }

    fun part2(input: List<String>) = input.toMonkeys()
        .let { monkeys ->
            for (i in (0 until 10000)) {
                processWorriedRound(monkeys)
            }
            monkeys.map { it.inspectedItems }
                .sortedDescending()
                .take(2)
                .fold(1L) { a, b -> a * b }
        }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 10605,
        part2 = ::part2,
        part2Check = 2713310158L,
    )
}

internal fun processRound(monkeys: List<Monkey>) {
    monkeys.forEach {
        it.inspectItems().forEach { (newWorryLevel, monkey) ->
            monkeys[monkey].catchItem(newWorryLevel)
        }
    }
}

internal fun processWorriedRound(monkeys: List<Monkey>) {
    val minimizer = monkeys.fold(1L) { product, monkey -> product * monkey.testDivisibleBy }.toLong()
    monkeys.forEach {
        val origItems = it.heldItems
        it.inspectItems(false).forEach { (newWorryLevel, monkey) ->
            if (newWorryLevel < 0) {
                println(it)
                println(origItems)
                throw IllegalArgumentException()
            }
            monkeys[monkey].catchItem(newWorryLevel % minimizer)
        }
    }
}

internal fun List<String>.toMonkeys() =
    groupByBlanks().map { it.toMonkey() }

internal fun List<String>.toMonkey() = Monkey(
    startingItems = this[1].toStartingItems(),
    operation = this[2].toOperation(),
    testDivisibleBy = this[3].toTestDivisibleBy(),
    ifTrueTarget = this[4].toIfTrueTarget(),
    ifFalseTarget = this[5].toIfFalseTarget(),
)

private const val STARTING_ITEMS = "Starting items: "
private fun String.toStartingItems() =
    this.substringAfter(STARTING_ITEMS).split(',')
        .map { it.trim().toLong() }
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
                    "+" -> Add(value.toLong())
                    "*" -> Multiply(value.toLong())
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

