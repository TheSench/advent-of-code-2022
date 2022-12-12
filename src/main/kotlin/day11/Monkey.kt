package day11

data class Monkey(
    val startingItems: List<Long>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val ifTrueTarget: Int,
    val ifFalseTarget: Int,
) {
    private var items = startingItems.map { it }.toMutableList()
    var inspectedItems = 0L
        private set
    val heldItems get() = items.map { it.toInt() }

    fun inspectItems(getsBored: Boolean = true): List<Pair<Long, Int>> {
        return items.map { worryLevel ->
            inspectedItems++
            operation.apply(worryLevel)
                .let { if (getsBored) getBoredWith(it) else it }
                .let(::getTossTarget)
        }.also { items.clear() }
    }

    fun getBoredWith(worryLevel: Long) = worryLevel / 3L

    fun getTossTarget(worryLevel: Long) = when (worryLevel % testDivisibleBy == 0L) {
        true -> worryLevel to ifTrueTarget
        false -> worryLevel to ifFalseTarget
    }

    fun catchItem(item: Long) {
        items.add(item)
    }
}