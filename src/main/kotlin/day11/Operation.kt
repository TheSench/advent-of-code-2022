package day11

sealed interface Operation {
    val value: Int
    fun apply(otherValue: Int): Int
}

data class Add(
    override val value: Int
) : Operation {
    override fun apply(otherValue: Int) = otherValue + this.value
}

data class Multiply(
    override val value: Int
) : Operation {
    override fun apply(otherValue: Int) = otherValue * this.value
}