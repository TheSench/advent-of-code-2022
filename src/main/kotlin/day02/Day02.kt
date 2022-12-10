package day02

import readInput

private fun main() {
  fun part1(input: List<String>): Int {
    return input.mapToCombinations().toScores().also(::println).sum()
  }

  fun part2(input: List<String>): Int {
    return input.mapToCombinationsBasedOnResult().toScores().also(::println).sum()
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput(2, "test")
  check(part1(testInput) == 15)

  val input = readInput(2, "input")
  println(part1(input))

  check(part2(testInput) == 12)
  println(part2(input))
}

private fun List<String>.mapToCombinations() = map(String::toThrows).map { throws -> Combination(throws) }
private fun List<String>.mapToCombinationsBasedOnResult() = map(String::toCombinationFromThrowAndResult)

private fun List<Combination>.toScores() = map(Combination::toScore)

private fun Combination.toScore() = yours versus theirs

private fun String.toThrows() = split(" ").map(String::toThrow)

private fun String.toCombinationFromThrowAndResult() = split(" ").let { (theirs, result) ->
  Combination(theirs.toThrow(), result.toResult())
}

data class Combination(val yours: Throw, val theirs: Throw) {
  companion object {
    operator fun invoke(throws: List<Throw>): Combination {
      val (theirs, yours) = throws
      return Combination(yours, theirs)
    }

    operator fun invoke(theirs: Throw, result: Result) = Combination(
      result against theirs,
      theirs
    )
  }
}

private fun roundScore(yours: Throw, theirs: Throw) = when {
  yours == theirs -> 3
  yours beats theirs -> 6
  else -> 0
}

infix private fun Throw.versus(other: Throw) = this.shapeScore + roundScore(this, other)


private fun Throw.losingThrow() = Throw.winningCombinations[this]!!
private fun Throw.winningThrow() = Throw.winningCombinations.entries.find { (_, loser) -> loser == this }?.key!!

private infix fun Result.against(theirs: Throw) = when (this) {
  Result.DRAW -> theirs
  Result.WIN -> theirs.winningThrow()
  Result.LOSE -> theirs.losingThrow()
}

private fun String.toThrow() = when (this) {
  "A", "X" -> Throw.ROCK
  "B", "Y" -> Throw.PAPER
  "C", "Z" -> Throw.SCISSORS
  else -> throw IllegalArgumentException()
}

enum class Throw(val shapeScore: Int) {
  ROCK(1),
  PAPER(2),
  SCISSORS(3);

  infix fun beats(other: Throw) = winningCombinations[this] == other

  companion object {
    val winningCombinations = mapOf(
      ROCK to SCISSORS,
      PAPER to ROCK,
      SCISSORS to PAPER,
    )
  }
}

private fun String.toResult() = when (this) {
  "X" -> Result.LOSE
  "Y" -> Result.DRAW
  "Z" -> Result.WIN
  else -> throw IllegalArgumentException()
}

enum class Result {
  LOSE,
  DRAW,
  WIN;
}