package utils

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PointTest {
    @Nested
    @Suppress("ClassName")
    inner class `Point#plus(other) should` {
        @Test
        fun `create a new point with x equal to the sum of both x's and y equal to the sum of both y's`() {
            (Point(3, 4) + Point(7, 2)) shouldBe Point(10, 6)
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `Point#minus(other) should` {
        @Test
        fun `create a new point with x equal to the difference of both x's and y equal to the difference of both y's`() {
            (Point(3, 4) - Point(7, 2)) shouldBe Point(-4, 2)
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `Point#toAbsolute(other) should` {
        @ParameterizedTest
        @CsvSource(
            " 1, 1,   1, 1",
            " 2, 3,   2, 3",
            "-1,-1,   1, 1",
            "-4, 7,   4, 7"
        )
        fun `create a new point with from the absolute value of the original point's x and y`(
            oldX: Int, oldY: Int,
            newX: Int, newY: Int,
        ) {
            Point(oldX, oldY).toAbsolute() shouldBe Point(newX, newY)
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `Point#rangeTo(endInclusive) should` {
        @Test
        fun `creates horizontal ranges of points`() {
            Point(5, 7)..Point(9, 7) shouldContainExactly listOf(
                Point(5, 7),
                Point(6, 7),
                Point(7, 7),
                Point(8, 7),
                Point(9, 7),
            )
        }

        @Test
        fun `creates negative horizontal ranges of points`() {
            Point(9, 7)..Point(5, 7) shouldContainExactly listOf(
                Point(9, 7),
                Point(8, 7),
                Point(7, 7),
                Point(6, 7),
                Point(5, 7),
            )
        }

        @Test
        fun `creates vertical ranges of points`() {
            Point(3, 4)..Point(3, 8) shouldContainExactly listOf(
                Point(3, 4),
                Point(3, 5),
                Point(3, 6),
                Point(3, 7),
                Point(3, 8),
            )
        }

        @Test
        fun `creates negative vertical ranges of points`() {
            Point(3, 8)..Point(3, 4) shouldContainExactly listOf(
                Point(3, 8),
                Point(3, 7),
                Point(3, 6),
                Point(3, 5),
                Point(3, 4),
            )
        }
    }
}