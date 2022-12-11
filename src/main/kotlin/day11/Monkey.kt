package day11

data class Monkey(
    val items: MutableList<Int>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val ifTrueTarget: Int,
    val ifFalseTarget: Int,
)