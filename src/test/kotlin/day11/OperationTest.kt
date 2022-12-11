package day11

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class OperationTest {
    @ParameterizedTest
    @CsvSource(
        "3, 5, 8",
        "1, 3, 4"
    )
    fun `Add should add the given value to its value`(
        value: Int,
        otherValue: Int,
        expectedSum: Int
    ) {
        val operation = Add(value)

        operation.apply(otherValue) shouldBe expectedSum
    }

    @ParameterizedTest
    @CsvSource(
        "3, 5, 15",
        "2, 3, 6"
    )
    fun `Multiply should multiply the given value to its value`(
        value: Int,
        otherValue: Int,
        expectedProduct: Int
    ) {
        val operation = Multiply(value)

        operation.apply(otherValue) shouldBe expectedProduct
    }
}