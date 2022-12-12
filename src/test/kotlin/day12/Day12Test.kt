package day12

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day12Test {
    @Nested
    @Suppress("ClassName")
    inner class `Lines#toGrid() should` {
        @Test
        fun `parse characters into elevations`() {
            val grid = listOf(
                "abcdef",
                "ghijkl",
                "mnopqr",
                "stuvwx",
                "yzSEcd",
            ).toGrid()

            grid.elevations shouldBe listOf(
                listOf(0, 1, 2, 3, 4, 5),
                listOf(6, 7, 8, 9, 10, 11),
                listOf(12, 13, 14, 15, 16, 17),
                listOf(18, 19, 20, 21, 22, 23),
                listOf(24, 25, 0, 25, 2, 3),
            )
        }

        @Test
        fun `parse starting and ending positions`() {
            val grid = listOf(
                "abcdef",
                "ghijkl",
                "mSopqr",
                "stuvwx",
                "yzabEd",
            ).toGrid()

            grid.start shouldBe Point(1, 2)
            grid.end shouldBe Point(4, 4)
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `Elevations#get should` {
        @Test
        fun `get the point that is x from the left and y from the top`() {
            val elevations = listOf(
                listOf(1, 4, 3, 9),
                listOf(2, 3, 7, 1),
                listOf(5, 8, 6, 2),
                listOf(3, 0, 4, 1),
            )

            elevations[Point(2, 2)] shouldBe 6
        }
    }
}