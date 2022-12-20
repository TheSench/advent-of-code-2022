package day20

import runDay

fun main() {
  fun part1(input: List<String>) = input.toCoordinates(
    decryptionKey = 1,
    mixCount = 1,
  )

  fun part2(input: List<String>) = input.toCoordinates(
    decryptionKey = 811589153,
    mixCount = 10,
  )

  (object {}).runDay(
    part1 = ::part1,
    part1Check = 3L,
    part2 = ::part2,
    part2Check = 1623178306L,
  )
}

fun List<String>.toCoordinates(decryptionKey: Int = 1, mixCount: Int = 1) = this.toLongs()
  .map { it * decryptionKey }
  .toWrappedList()
  .let { list ->
    repeat(mixCount) { list.mix() }
    list
  }
  .getPositionsFromOriginalZero(listOf(1000, 2000, 3000))
  .sum()

fun List<String>.toLongs() = this.map { it.toLong() }

fun List<Long>.toWrappedList() = WrappedList(this.mapIndexed { index, it -> index to it }.toMutableList())

fun WrappedList.mix() = let { wrappedList ->
  (0 until wrappedList.size).forEach { originalIndex ->
    val position = wrappedList.getPosition(originalIndex)
    wrappedList.move(position)
  }
  wrappedList
}

fun WrappedList.getPositionsFromOriginalZero(positions: List<Int>) = let { list ->
  val originalZero = list.find { it.second == 0L }!!
  val newPosition = list.getPosition(originalZero.first)
  positions
    .map { it + newPosition }
    .map { list[it].second }
}

class WrappedList(private val innerList: MutableList<Pair<Int, Long>>) : MutableList<Pair<Int, Long>> by innerList {
  private val positions: MutableMap<Int, Int> = List(innerList.size) { index -> index to index }.toMap().toMutableMap()

  fun getPosition(originalIndex: Int) = positions[originalIndex]!!
  override fun get(index: Int): Pair<Int, Long> = innerList[wrap(index)]

  override fun set(index: Int, element: Pair<Int, Long>): Pair<Int, Long> {
    return innerList.set(wrap(index), element)
  }

  fun move(index: Int) {
    val element = get(index)
    val oldPosition = wrap(index)
    val newPosition = wrapMoveTo(index + element.second).adjustForBoundaries()
    val rangeToShift = if (oldPosition < newPosition) {
      (newPosition downTo oldPosition)
    } else {
      (newPosition..oldPosition)
    }
    rangeToShift.forEach { positionToSwap ->
      val old = get(oldPosition)
      val new = set(positionToSwap, get(oldPosition))
      set(oldPosition, new)
      positions[old.first] = positionToSwap
      positions[new.first] = oldPosition
    }
  }

  private fun wrapMoveTo(index: Long) = (index % (size - 1)).let {
    if (it >= 0) it else (size - 1) + it
  }

  private fun wrap(index: Int) = (index % size).let {
    if (it >= 0) it else size + it
  }

  private fun Long.adjustForBoundaries() = when (this) {
    0L -> size - 1
    else -> this.toInt()
  }

  override fun toString() = innerList.map { it.second }.joinToString(", ")
}