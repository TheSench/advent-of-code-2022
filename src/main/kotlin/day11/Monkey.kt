package day11

data class Monkey(
    val items: MutableList<Int>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val ifTrueTarget: Int,
    val ifFalseTarget: Int,
) {
    fun inspectItems(): List<Pair<Int, Int>> {
        return items.map { worryLevel ->
            getBoredWith(operation.apply(worryLevel))
                .let(::getTossTarget)
        }.also { items.clear() }
    }

    fun getBoredWith(worryLevel: Int) = worryLevel / 3

    fun getTossTarget(worryLevel: Int) = when (worryLevel % testDivisibleBy == 0) {
        true -> worryLevel to ifTrueTarget
        false -> worryLevel to ifFalseTarget
    }
}