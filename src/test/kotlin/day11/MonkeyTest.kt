package day11

import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MonkeyTest {
    @Nested
    @Suppress("ClassName")
    inner class `#inspectItems() should` {
        @Test
        fun `toss all items to other monkeys`() {
            val monkey = buildMonkey(
                startingItems = listOf(1, 2, 3),
            )

            monkey.inspectItems()

            monkey.heldItems should beEmpty()
        }

        @Test
        fun `return a monkey for each item it had`() {
            val monkey = buildMonkey(
                startingItems = listOf(1, 2, 3),
            )

            val monkeys = monkey.inspectItems()

            monkeys.size shouldBe 3
        }

        @Test
        fun `return a correct monkey for each item it had`() {
            val monkey = buildMonkey(
                startingItems = listOf(54, 65, 75, 74, 51),
                operation = Add(6),
                testDivisibleBy = 19,
                ifTrueTarget = 2,
                ifFalseTarget = 0
            )

            val monkeys = monkey.inspectItems()

            monkeys shouldBe listOf(
                20L to 0, // 54+6 -> 60/3 -> (20) % 19 ❌
                23L to 0, // 65+6 -> 71/3 -> (23) % 19 ❌
                27L to 0, // 75+6 -> 81/3 -> (27) % 19 ❌
                26L to 0, // 74+6 -> 80/3 -> (26) % 19 ❌
                19L to 2, // 51+6 -> 57/3 -> (19) % 19 ✅
            )
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `#getTossTarget() should` {
        @ParameterizedTest
        @CsvSource(
            "15, 5, 7",
            "24, 8, 9",
            "30, 10, 4",
            "10, 5, 2"
        )
        fun `return ifTrueTarget when worry level is divisible by testDivisibleBy`(
            worryLevel: Long,
            testDivisibleBy: Int,
            ifTrueTarget: Int
        ) {
            val monkey = buildMonkey(
                testDivisibleBy = testDivisibleBy,
                ifTrueTarget = ifTrueTarget,
                ifFalseTarget = -1,
            )

            monkey.getTossTarget(worryLevel).second shouldBe ifTrueTarget
        }

        @ParameterizedTest
        @CsvSource(
            "16, 5, 7",
            "23, 8, 9",
            "31, 10, 4",
            "8, 5, 2"
        )
        fun `return ifFalseTarget when worry level is NOT divisible by testDivisibleBy`(
            worryLevel: Long,
            testDivisibleBy: Int,
            ifFalseTarget: Int
        ) {
            val monkey = buildMonkey(
                testDivisibleBy = testDivisibleBy,
                ifTrueTarget = -1,
                ifFalseTarget = ifFalseTarget,
            )

            monkey.getTossTarget(worryLevel).second shouldBe ifFalseTarget
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `#getBoredWith() should` {
        @ParameterizedTest
        @CsvSource(
            "15, 5",
            "24, 8",
            "31, 10",
            "17, 5"
        )
        fun `divide the worry level by 3 and round down`(
            worryLevel: Long,
            expectedResult: Long
        ) {
            val monkey = buildMonkey(
                listOf(1, 2, 3),
            )

            monkey.getBoredWith(worryLevel) shouldBe expectedResult
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `#catchItem() should` {
        @Test
        fun `add the item to the end of the monkey's list`() {
            val monkey = buildMonkey(
                listOf(1, 2, 3),
            )

            monkey.catchItem(7L)
            monkey.catchItem(4L)

            monkey.heldItems shouldBe listOf(1, 2, 3, 7, 4)
        }
    }
}

private fun buildMonkey(
    startingItems: List<Long> = emptyList(),
    operation: Operation = Add(6),
    testDivisibleBy: Int = 19,
    ifTrueTarget: Int = 3,
    ifFalseTarget: Int = 5,
) = Monkey(
    startingItems = startingItems,
    operation = operation,
    testDivisibleBy = testDivisibleBy,
    ifTrueTarget = ifTrueTarget,
    ifFalseTarget = ifFalseTarget,
)