package day01

import groupByBlanks
import mapGroups
import runDay

fun main() {
  fun part1(input: List<String>): Int {
    return input.mapToTotalCalories().max()
  }

  fun part2(input: List<String>): Int {
    return input.mapToTotalCalories().sortedDescending().take(3).sum()
  }

  (object {}).runDay(
    part1 = ::part1,
    part1Check = 24000,
    part2 = ::part2,
    part2Check = 45000,
  )
}

private fun List<String>.mapToTotalCalories() = groupByBlanks()
  .mapGroups { it.toInt() }
  .map { it.sum() }
