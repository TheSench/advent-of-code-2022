package day11

import java.math.BigInteger

sealed interface Operation {
    fun apply(otherValue: BigInteger): BigInteger
}

data class Add(
    val value: Int
) : Operation {
    private val bigValue = value.toBigInteger()
    override fun apply(otherValue: BigInteger) = bigValue + otherValue
}

data class Multiply(
    val value: Int
) : Operation {
    private val bigValue = value.toBigInteger()
    override fun apply(otherValue: BigInteger) = bigValue * otherValue
}

object SquareIt : Operation {
    override fun apply(otherValue: BigInteger) = otherValue * otherValue
}

object DoubleIt : Operation {
    override fun apply(otherValue: BigInteger) = otherValue + otherValue
}