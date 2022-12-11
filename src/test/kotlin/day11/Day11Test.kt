package day11

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day11Test {
    @Nested
    @Suppress("ClassName")
    inner class `#toMonkey should` {
        @Test
        fun `parse an addition monkey`() {
            val input = listOf(
                "Monkey 1:",
                "  Starting items: 54, 65, 75, 74",
                "  Operation: new = old + 6",
                "  Test: divisible by 19",
                "    If true: throw to monkey 3",
                "    If false: throw to monkey 5",
            )

            input.toMonkey() shouldBe Monkey(
                items = mutableListOf(54, 65, 75, 74),
                operation = Add(6),
                testDivisibleBy = 19,
                ifTrueTarget = 3,
                ifFalseTarget = 5,
            )
        }

        @Test
        fun `parse a multiplication monkey`() {
            val input = listOf(
                "Monkey 0:",
                "  Starting items: 79, 98",
                "  Operation: new = old * 19",
                "  Test: divisible by 23",
                "    If true: throw to monkey 2",
                "    If false: throw to monkey 3",
            )

            input.toMonkey() shouldBe Monkey(
                items = mutableListOf(79, 98),
                operation = Multiply(19),
                testDivisibleBy = 23,
                ifTrueTarget = 2,
                ifFalseTarget = 3,
            )
        }
    }
}