package day15

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day15Test {
    @Nested
    @Suppress("ClassName")
    inner class `String#parse() should` {
        @Test
        fun `parse a line into the coordinates for a sensor and its relative beacon`() {
            val line = "Sensor at x=2, y=18: closest beacon is at x=-2, y=15"

            line.parse() shouldBe ParsedLine(
                Point(2, 18),
                Point(0, 33),
            )
        }
    }
    @Nested
    @Suppress("ClassName")
    inner class `String#toPoint() should` {
        @Test
        fun `parse a string into a point`() {
            val line = "Something at x=9, y=6"

            line.toPoint() shouldBe Point(9, 6)
        }

        @Test
        fun `parse a string with negative coordinates`() {
            val line = "Something at x=-3, y=-5"

            line.toPoint() shouldBe Point(-3, -5)
        }
    }
}
