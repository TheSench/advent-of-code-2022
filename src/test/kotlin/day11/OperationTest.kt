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
        value: Long,
        otherValue: Long,
        expectedResult: Long,
    ) {
        val operation = Add(value)

        operation.apply(otherValue) shouldBe expectedResult
    }

    @ParameterizedTest
    @CsvSource(
        "3, 5, 15",
        "2, 3, 6"
    )
    fun `Multiply should multiply the given value by its value`(
        value: Long,
        otherValue: Long,
        expectedResult: Long,
    ) {
        val operation = Multiply(value)

        operation.apply(otherValue) shouldBe expectedResult
    }

    @ParameterizedTest
    @CsvSource(
        "3, 9",
        "2, 4",
        "4, 16",
    )
    fun `Square should square the given value`(
        value: Long,
        expectedResult: Long,
    ) {
        val operation = SquareIt

        operation.apply(value) shouldBe expectedResult
    }

    @ParameterizedTest
    @CsvSource(
        "3, 6",
        "2, 4",
        "4, 8",
    )
    fun `Double should double the given value`(
        value: Long,
        expectedResult: Long,
    ) {
        val operation = DoubleIt

        operation.apply(value) shouldBe expectedResult
    }
}