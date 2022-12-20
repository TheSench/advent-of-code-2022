package day20

import runDay

fun main() {
  fun part1(input: List<String>) = WrappedList(input.mapIndexed { index, it -> index to it.toInt() }.toMutableList())
    .mix()
    .getPositionsFromOriginalZero(listOf(1000, 2000, 3000))
    .sum()

  fun part2(input: List<String>) = 0

  (object {}).runDay(
    part1 = ::part1,
    part1Check = 3,
    part2 = ::part2,
    part2Check = -1,
  )
}

fun WrappedList.mix() = let { wrappedList ->
  (0 until wrappedList.size).forEach { originalIndex ->
    val position = wrappedList.getPosition(originalIndex)!!
    wrappedList.move(position)
  }
  wrappedList
}

fun WrappedList.getPositionsFromOriginalZero(positions: List<Int>) = let { list ->
  val originalZero = list.find { it.second == 0 }!!
  val newPosition = list.getPosition(originalZero.first)
  listOf(1000, 2000, 3000)
    .map { it + newPosition }
    .map { list[it].second }
}

class WrappedList(private val innerList: MutableList<Pair<Int, Int>>) : MutableList<Pair<Int, Int>> by innerList {
  private val positions: MutableMap<Int, Int> = List(innerList.size) { index -> index to index }.toMap().toMutableMap()

  fun getPosition(originalIndex: Int) = positions[originalIndex]!!
  override fun get(index: Int): Pair<Int, Int> = innerList[wrap(index)]

  override fun set(index: Int, element: Pair<Int, Int>): Pair<Int, Int> {
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

  private fun wrapMoveTo(index: Int) = (index % (size - 1)).let {
    if (it >= 0) it else (size - 1) + it
  }

  private fun wrap(index: Int) = (index % size).let {
    if (it >= 0) it else size + it
  }

  private fun Int.adjustForBoundaries() = when (this) {
    0 -> size - 1
    else -> this
  }

  override fun toString() = innerList.map { it.second }.joinToString(", ")
}