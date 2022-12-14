package day14

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day14Test {
    @Nested
    @Suppress("ClassName")
    inner class `String#toLines() should` {
        @Test
        fun `parse the input into a series of points`() {
            val input = "498,4 -> 498,6 -> 496,6"

            input.toLines() shouldBe listOf(
                Point(498, 4),
                Point(498, 6),
                Point(496, 6),
            )
        }
    }
}
