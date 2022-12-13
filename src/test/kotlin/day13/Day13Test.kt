package day13

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day13Test {
    @Nested
    @Suppress("ClassName")
    inner class `Line#toPackets() should` {
        @Test
        fun `parse integers`() {
            val line = "[1,1,3,1,1]"

            line.parse() shouldBe ListData(
                IntData(1),
                IntData(1),
                IntData(3),
                IntData(1),
                IntData(1)
            )
        }

        @Test
        fun `parse lists`() {
            val line = "[[1],[2,3,4]]"

            line.parse() shouldBe ListData(
                ListData(
                    IntData(1),
                ),
                ListData(
                    IntData(2),
                    IntData(3),
                    IntData(4),
                ),
            )
        }

        @Test
        fun `parse combination of list and integer`() {
            val line = "[[1],4]"

            line.parse() shouldBe ListData(
                ListData(IntData(1)),
                IntData(4),
            )
        }

        @Test
        fun `parse deeply nested lists`() {
            val line = "[1,[2,[3,[4,[5,6,0]]]],8,9]"

            line.parse() shouldBe ListData(
                IntData(1),
                ListData(
                    IntData(2),
                    ListData(
                        IntData(3),
                        ListData(
                            IntData(4),
                            ListData(
                                IntData(5),
                                IntData(6),
                                IntData(0)
                            )
                        )
                    )
                ),
                IntData(8),
                IntData(9)
            )
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `PacketData#compareTo() should` {

        @Nested
        @Suppress("ClassName")
        inner class `when comparing two IntData` {
            @Test
            fun `return that the left is smaller if its data is smaller`() {
                IntData(1) should beLessThan(IntData(2))
            }

            @Test
            fun `return that the left is larger if its data is larger`() {
                IntData(3) should beGreaterThan(IntData(1))
            }

            @Test
            fun `return 0 if both are equal`() {
                IntData(2).compareTo(IntData(2)) shouldBe 0
            }
        }

        @Nested
        @Suppress("ClassName")
        inner class `when comparing two ListData` {
            @Test
            fun `return that the left is smaller if its element is smaller`() {
                val left = ListData(IntData(1))
                val right = ListData(IntData(2))
                left should beLessThan(right)
            }

            @Test
            fun `return that the left is larger if its element is larger`() {
                val left = ListData(IntData(2))
                val right = ListData(IntData(1))
                left should beGreaterThan(right)
            }

            @Test
            fun `return 0 if both are equal`() {
                val left = ListData(IntData(1))
                val right = ListData(IntData(1))

                left.compareTo(right) shouldBe 0
            }

            @Test
            fun `return that the left is smaller when the first element not the same as the same one in right is smaller`() {
                val left = ListData(IntData(3), IntData(2), IntData(4))
                val right = ListData(IntData(3), IntData(4), IntData(2))
                left should beLessThan(right)
            }

            @Test
            fun `return that the left is larger when the first element not the same as the same one in right is larger`() {
                val left = ListData(IntData(3), IntData(4), IntData(1))
                val right = ListData(IntData(3), IntData(2), IntData(3))
                left should beGreaterThan(right)
            }

            @Test
            fun `return 0 if all elements are equal`() {
                val left = ListData(IntData(1), IntData(3), IntData(2))
                val right = ListData(IntData(1), IntData(3), IntData(2))

                left.compareTo(right) shouldBe 0
            }

            @Test
            fun `return that the left is smaller if all paired elements are equal and the left is shorter`() {
                val left = ListData(IntData(1), IntData(3))
                val right = ListData(IntData(1), IntData(3), IntData(2))

                left should beLessThan(right)
            }

            @Test
            fun `return that the left is larger if all paired elements are equal and the left is longer`() {
                val left = ListData(IntData(1), IntData(3), IntData(2))
                val right = ListData(IntData(1), IntData(3))

                left should beGreaterThan(right)
            }
        }

        @Nested
        @Suppress("ClassName")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class `when comparing a ListData and an IntData` {


            fun lessThanPackets() = listOf(
                arguments(
                    ListData(IntData(1)),
                    IntData(2),
                ),
                arguments(
                    IntData(3),
                    ListData(IntData(4)),
                ),
                arguments(
                    IntData(5),
                    ListData(IntData(5), IntData(1)),
                )
            )

            @ParameterizedTest
            @MethodSource("lessThanPackets")
            fun `treat the IntData as a ListData and return left is smaller than right if the first value is smaller`(
                left: PacketData,
                right: PacketData,
            ) {
                left should beLessThan(right)
            }

            fun greaterThanPackets() = listOf(
                arguments(
                    IntData(2),
                    ListData(IntData(1)),
                ),
                arguments(
                    ListData(IntData(4)),
                    IntData(3),
                ),
            )

            @ParameterizedTest
            @MethodSource("greaterThanPackets")
            fun `treat the IntData as a ListData and return left is larger than right if the first value is larger`(
                left: PacketData,
                right: PacketData,
            ) {
                left should beGreaterThan(right)
            }

            fun equivalentPackets() = listOf(
                arguments(
                    ListData(IntData(1)),
                    IntData(1),
                ),
                arguments(
                    IntData(3),
                    ListData(IntData(3)),
                ),
                arguments(
                    IntData(5),
                    ListData(IntData(5)),
                )
            )

            @ParameterizedTest
            @MethodSource("equivalentPackets")
            fun `return 0 if the ListData has one element and it is the same as the IntData`(
                left: PacketData,
                right: PacketData,
            ) {
                left.compareTo(right) shouldBe 0
            }

            @Test
            fun `return that left is smaller than right if the first value is the same and right is longer`() {
                val left = IntData(1)
                val right = ListData(IntData(1), IntData(3))
                left.compareTo(right) shouldBe 0
            }

            @Test
            fun `return that left is larger than right if the first value is the same and right is shorter`() {
                val left = ListData(IntData(1), IntData(3))
                val right = IntData(1)
                left.compareTo(right) shouldBe 0
            }
        }
    }
}

private fun beLessThan(x: PacketData) = object : Matcher<PacketData> {
    override fun test(value: PacketData) =
        MatcherResult(
            value < x,
            { "$value should be < $x" },
            { "$value should not be < $x" })
}

private fun beGreaterThan(x: PacketData) = object : Matcher<PacketData> {
    override fun test(value: PacketData) =
        MatcherResult(
            value > x,
            { "$value should be > $x" },
            { "$value should not be > $x" })
}