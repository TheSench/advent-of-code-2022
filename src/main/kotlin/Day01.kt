fun main() {
  fun part1(input: List<String>): Int {
    return input.mapToTotalCalories().max()
  }

  fun part2(input: List<String>): Int {
    return input.mapToTotalCalories().sortedDescending().take(3).sum()
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput(1, "test")
  check(part1(testInput) == 24000)

  val input = readInput(1, "input")
  println(part1(input))

  check(part2(testInput) == 45000)
  println(part2(input))
}

private fun List<String>.mapToTotalCalories() = groupByBlanks()
  .mapGroups { it.toInt() }
  .map { it.sum() }
