package day05

import groupByBlanks
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import readInput
import stackOf
import java.util.*


class Day05Test {
    @Nested
    @Suppress("ClassName")
    inner class `ListOfString#toStacks should`() {
        @Test
        fun `convert the test input to the expected list of stacks`() {
            val input = readInput(5, "test").groupByBlanks()[0]

            input.toStacks() shouldBe listOf(
                stackOf('Z', 'N'),
                stackOf('M', 'C', 'D'),
                stackOf('P'),
            )
        }
    }

    @Nested
    @Suppress("ClassName")
    inner class `ListOfString#toInstructions should`() {

    }
}