package utils

import groupByBlanks
import io.kotest.matchers.shouldBe
import mapGroups
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import readInput
import stackOf
import toStack

class UtilsTest {
  @Nested
  @Suppress("ClassName")
  inner class `readInput should` {

    @Test
    fun `load a test file in the given day directory from resources`() {
      readInput(12, "test") shouldBe listOf(
        "1",
        "43",
        "",
        "3",
      )
    }

    @Test
    fun `load an input file in the given day directory from resources`() {
      readInput(12, "input") shouldBe listOf(
        "1",
        "3",
        "4",
        "1",
        "4",
        "",
        "12",
        "4",
        "1",
        "4",
        "",
        "412",
      )
    }

    @Test
    fun `pad single-digit days with a leading 0 to find the target file`() {
      readInput(4, "test") shouldBe listOf(
        "1",
        "3",
        "6",
      )
    }
  }

  @Nested
  @Suppress("ClassName")
  inner class `groupByBlanks should` {
    @Test
    fun `separate a list of strings into groups by splitting on blank lines`() {
      val input = listOf(
        "1", "2",
        "",
        "3",
        "",
        "43", "2", "7",
      )

      input.groupByBlanks() shouldBe listOf(
        listOf("1", "2"),
        listOf("3"),
        listOf("43", "2", "7"),
      )
    }

    @Test
    fun `ignore trailing blanks`() {
      val input = listOf(
        "1", "2",
        "",
        "3",
        "",
        "43", "2", "7",
        ""
      )

      input.groupByBlanks() shouldBe listOf(
        listOf("1", "2"),
        listOf("3"),
        listOf("43", "2", "7"),
      )
    }

    @Test
    fun `treat consecutive blank lines as separate groups`() {
      val input = listOf(
        "1", "2",
        "",
        "",
        "3",
        "",
        "43", "2", "7",
      )

      input.groupByBlanks() shouldBe listOf(
        listOf("1", "2"),
        emptyList(),
        listOf("3"),
        listOf("43", "2", "7"),
      )
    }
  }

  @Nested
  @Suppress("ClassName")
  inner class `mapGroups should` {
    @Test
    fun `map a list of lists by transforming the children of the inner list`() {
      val input = listOf(
        listOf(1, 2, 3),
        listOf(3, 2, 1)
      )

      input.mapGroups { it * 2 } shouldBe listOf(
        listOf(2, 4, 6),
        listOf(6, 4, 2)
      )
    }
  }

  @Nested
  @Suppress("ClassName")
  inner class `stackOf should` {
    @Test
    fun `produce an empty stack when given empty input`() {
      stackOf<String>() shouldBe ArrayDeque()
    }

    @Test
    fun `produce a stack containing the given items`() {
      stackOf("a", "b") shouldBe ArrayDeque<String>().apply {
        add("a")
        add("b")
      }

      stackOf("a", "b").first()
    }
  }

  @Nested
  @Suppress("ClassName")
  inner class `List#toStack should` {
    @Test
    fun `produce an empty stack when given empty list`() {
      emptyList<String>().toStack() shouldBe ArrayDeque()
    }

    @Test
    fun `produce a stack containing the given items`() {
      listOf("a", "b").toStack() shouldBe ArrayDeque<String>().apply {
        add("a")
        add("b")
      }

      stackOf("a", "b").first()
    }
  }
}