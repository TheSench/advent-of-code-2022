package day11

import java.math.BigInteger

data class Monkey(
    val startingItems: List<Int>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val ifTrueTarget: Int,
    val ifFalseTarget: Int,
) {
    private val bigTestDivisibleBy = testDivisibleBy.toBigInteger()
    private var items = startingItems.map { it.toBigInteger() }.toMutableList()
    var inspectedItems = 0
        private set
    val heldItems get() = items.map { it.toInt() }

    fun inspectItems(getsBored: Boolean = true): List<Pair<BigInteger, Int>> {
        return items.map { worryLevel ->
            inspectedItems++
            operation.apply(worryLevel)
                .let { if (getsBored) getBoredWith(it) else it }
                .let(::getTossTarget)
        }.also { items.clear() }
    }

    fun getBoredWith(worryLevel: BigInteger) = worryLevel / BigInteger.valueOf(3)

    fun getTossTarget(worryLevel: BigInteger) = when (worryLevel % bigTestDivisibleBy == BigInteger.ZERO) {
        true -> worryLevel to ifTrueTarget
        false -> worryLevel to ifFalseTarget
    }

    fun catchItem(item: BigInteger) {
        items.add(item)
    }
}