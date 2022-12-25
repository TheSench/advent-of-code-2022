package day24

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day24Test {
    @Nested
    @Suppress("ClassName")
    inner class `Blizzard tests` {

        @Nested
        @Suppress("ClassName")
        inner class `Blizzard#move should` {
            @ParameterizedTest
            @CsvSource(
                "3, 4,  3, 3",
                "1, 6,  1, 5"
            )
            fun `move up blizzards one space up`(
                x: Int,
                y: Int,
                dX: Int,
                dY: Int
            ) {
                val blizzard = Blizzard.up(x, y)

                blizzard.move() shouldBe Blizzard.up(dX, dY)
            }

            @ParameterizedTest
            @CsvSource(
                "3, 4,  3, 5",
                "1, 6,  1, 7"
            )
            fun `move down blizzards one space down`(
                x: Int,
                y: Int,
                dX: Int,
                dY: Int
            ) {
                val blizzard = Blizzard.down(x, y)

                blizzard.move() shouldBe Blizzard.down(dX, dY)
            }

            @ParameterizedTest
            @CsvSource(
                "3, 4,  2, 4",
                "1, 6,  0, 6"
            )
            fun `move left blizzards one space left`(
                x: Int,
                y: Int,
                dX: Int,
                dY: Int
            ) {
                val blizzard = Blizzard.left(x, y)

                blizzard.move() shouldBe Blizzard.left(dX, dY)
            }

            @ParameterizedTest
            @CsvSource(
                "3, 4,  4, 4",
                "1, 6,  2, 6"
            )
            fun `move right blizzards one space right`(
                x: Int,
                y: Int,
                dX: Int,
                dY: Int
            ) {
                val blizzard = Blizzard.right(x, y)

                blizzard.move() shouldBe Blizzard.right(dX, dY)
            }
        }

        @Nested
        @Suppress("ClassName")
        inner class `ListOfBlizzard#move should` {
            private val bounds = Bounds(7, 7)

            @Test
            fun `reset up blizzards that hit a wall`() {
                val blizzards = listOf(Blizzard.up(5, 1))

                blizzards.move(bounds) shouldBe listOf(
                    Blizzard.up(5, 6)
                )
            }

            @Test
            fun `reset down blizzards that hit a wall`() {
                val blizzards = listOf(Blizzard.down(4, 6))

                blizzards.move(bounds) shouldBe listOf(
                    Blizzard.down(4, 1)
                )
            }

            @Test
            fun `reset left blizzards that hit a wall`() {
                val blizzards = listOf(Blizzard.left(1, 2))

                blizzards.move(bounds) shouldBe listOf(
                    Blizzard.left(6, 2)
                )
            }

            @Test
            fun `reset right blizzards that hit a wall`() {
                val blizzards = listOf(Blizzard.right(6, 3))

                blizzards.move(bounds) shouldBe listOf(
                    Blizzard.right(1, 3)
                )
            }
        }
    }
}
