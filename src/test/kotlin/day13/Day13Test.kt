package day13

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}