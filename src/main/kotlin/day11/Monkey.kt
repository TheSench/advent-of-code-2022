package day11

data class Monkey(
    val startingItems: List<Int>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val ifTrueTarget: Int,
    val ifFalseTarget: Int,
) {
    private var items = startingItems.toMutableList()
    var inspectedItems = 0
        private set
    val heldItems get() = items.toList()

    fun inspectItems(): List<Pair<Int, Int>> {
        return items.map { worryLevel ->
            inspectedItems++
            getBoredWith(operation.apply(worryLevel))
                .let(::getTossTarget)
        }.also { items.clear() }
    }

    fun getBoredWith(worryLevel: Int) = worryLevel / 3

    fun getTossTarget(worryLevel: Int) = when (worryLevel % testDivisibleBy == 0) {
        true -> worryLevel to ifTrueTarget
        false -> worryLevel to ifFalseTarget
    }

    fun catchItem(item: Int) {
        items.add(item)
    }
}