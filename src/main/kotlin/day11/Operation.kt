package day11

sealed interface Operation {
    fun apply(otherValue: Long): Long
}

data class Add(
    val value: Long
) : Operation {
    override fun apply(otherValue: Long) = value + otherValue
}

data class Multiply(
    val value: Long
) : Operation {
    override fun apply(otherValue: Long) = value * otherValue
}

object SquareIt : Operation {
    override fun apply(otherValue: Long) = otherValue * otherValue
}

object DoubleIt : Operation {
    override fun apply(otherValue: Long) = otherValue + otherValue
}