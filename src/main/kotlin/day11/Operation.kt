package day11

sealed interface Operation {
    fun apply(otherValue: Int): Int
}

data class Add(
    val value: Int
) : Operation {
    override fun apply(otherValue: Int) = this.value + otherValue
}

data class Multiply(
    val value: Int
) : Operation {
    override fun apply(otherValue: Int) = this.value * otherValue
}

object SquareIt : Operation {
    override fun apply(otherValue: Int) = otherValue * otherValue
}

object DoubleIt : Operation {
    override fun apply(otherValue: Int) = otherValue + otherValue
}