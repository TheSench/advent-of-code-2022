package day21

import runDay
import stackOf
import kotlin.RuntimeException

fun main() {

  fun part1(input: List<String>): Long = input.map { it.toMonkey() }
    .processMonkeys()
    .let {
      it["root"]!!.number!!
    }

  fun part2(input: List<String>) = input.map { it.toMonkey() }
    .processMonkeys()
    .let { allMonkeys ->
      val root = allMonkeys["root"]!!
      val pathToHuman = root.findPath("humn", allMonkeys)
      allMonkeys.calculateHuman(root, pathToHuman)
    }

  (object {}).runDay(
    part1 = ::part1,
    part1Check = 152L,
    part2 = ::part2,
    part2Check = 301L,
  )
}

private fun List<Monkey>.processMonkeys(): MutableMap<String, Monkey> = let { monkeys ->
  val seenMonkeys = mutableMapOf<String, Monkey>()
  val waitList = mutableMapOf<String, List<Monkey>>()

  fun Monkey.register() {
    seenMonkeys[name] = this
  }

  fun Monkey.tryRunOperation() {
    val otherMonkeys = waitingOn.map { it to seenMonkeys[it]?.number }
    if (otherMonkeys.any { it.second == null }) {
      otherMonkeys.forEach { (other, value) ->
        if (value == null) {
          val listForMonkey = waitList.computeIfAbsent(other) { emptyList() }
          waitList[other] = listForMonkey + this
        }
      }
      return
    }
    val numbers = otherMonkeys.map { it.second!! }

    number = when (operation) {
      "+" -> numbers[0] + numbers[1]
      "-" -> numbers[0] - numbers[1]
      "*" -> numbers[0] * numbers[1]
      "/" -> numbers[0] / numbers[1]
      else -> throw IllegalArgumentException("Operation $operation not supported")
    }

    removeFromWaitList(waitList)
  }

  fun Monkey.triggerWaiters() {
    if (number == null) return

    waitList[name]?.forEach {
      it.tryRunOperation()
      if (it.number != null) it.triggerWaiters()
    }
  }

  fun Monkey.process() {
    register()
    if (number == null) tryRunOperation()
  }

  monkeys.forEach { monkey ->
    monkey.process()
    monkey.triggerWaiters()
    val rootNumber = seenMonkeys["root"]?.number
    if (rootNumber != null) {
      return seenMonkeys
    }
  }
  throw RuntimeException("Did not find root number")
}

private fun Map<String, Monkey>.calculateHuman(monkey: Monkey, pathToHuman: Set<String>): Long {
  val left = monkey.waitingOn.first()
  val right = monkey.waitingOn.last()
  val leftMonkey = this[left]!!
  val rightMonkey = this[right]!!
  return if (left in pathToHuman) {
    calculateHuman(rightMonkey.number!!, leftMonkey, pathToHuman)
  } else {
    calculateHuman(leftMonkey.number!!, rightMonkey, pathToHuman)
  }
}

private fun Map<String, Monkey>.calculateHuman(value: Long, monkey: Monkey, pathToHuman: Set<String>): Long {
  val left = monkey.waitingOn.first()
  val right = monkey.waitingOn.last()
  val leftMonkey = this[left]!!
  val rightMonkey = this[right]!!
  if (left in pathToHuman) {
    val newValue = when (monkey.operation) {
      "+" -> value - rightMonkey.number!!
      "-" -> value + rightMonkey.number!!
      "*" -> value / rightMonkey.number!!
      "/" -> value * rightMonkey.number!!
      else -> throw IllegalArgumentException("Illegal operation ${monkey.operation}")
    }
//    println("/*(L)*/ ${value}L == ${newValue}L ${monkey.operation} ${rightMonkey.number}L")
    return  if (left == "humn") {
      newValue
    } else {
      calculateHuman(newValue, leftMonkey, pathToHuman)
    }
  } else {
    val newValue = when (monkey.operation) {
      "+" -> value - leftMonkey.number!!
      "-" -> -(value - leftMonkey.number!!)
      "*" -> value / leftMonkey.number!!
      "/" -> leftMonkey.number!! / value
      else -> throw IllegalArgumentException("Illegal operation ${monkey.operation}")
    }
//    println("/*(R)*/ ${value}L == ${leftMonkey.number}L ${monkey.operation} ${newValue}L")
    return  if (right == "humn") {
      newValue
    } else {
      calculateHuman(newValue, rightMonkey, pathToHuman)
    }
  }
}

private fun Monkey.findPath(targetKey: String, allMonkeys: Map<String, Monkey>): Set<String> {
  val paths = stackOf(this.waitingOn.map { listOf(it) })
  while (paths.size > 0) {
    val path = paths.removeFirst()
    val key = path.last()
    if (key == targetKey) {
      return path.toSet()
    } else {
      allMonkeys[key]!!.waitingOn.forEach {
        paths.addLast(path + it)
      }
    }
  }
  throw RuntimeException("Did not find path")
}

private fun Monkey.toEquation(
  allMonkeys: Map<String, Monkey>,
  maxDepth: Int = Int.MAX_VALUE,
  currentDepth: Int = 0
): String = when {
  this.operation == null -> this.number.toString()
  currentDepth == maxDepth -> this.number.toString()
  else -> {
    val first = allMonkeys[this.waitingOn[0]]!!.toEquation(allMonkeys, maxDepth, currentDepth + 1)
    val second = allMonkeys[this.waitingOn[1]]!!.toEquation(allMonkeys, maxDepth, currentDepth + 1)
    "($first $operation $second)"
  }
}

private fun Monkey.removeFromWaitList(waitList: MutableMap<String, List<Monkey>>) {
  this.waitingOn.forEach {
    if (waitList.containsKey(it)) {
      waitList[it] = waitList[it]!!.filter { waiter -> waiter != this }
    }
  }
}

class Monkey(
  val name: String,
  var number: Long? = null,
  val operation: String? = null,
  val originalWaitingOn: List<String> = emptyList(),
) {
  var waitingOn: List<String> = originalWaitingOn
}

fun String.toMonkey() = split(": ").let { nameValue ->
  val name = nameValue.first()
  nameValue.last().split(" ").let {
    when (it.size) {
      1 -> NumberMonkey(name, it.first())
      else -> OperationMonkey(name, it[0], it[1], it[2])
    }
  }
}

fun NumberMonkey(name: String, value: String): Monkey {
  return Monkey(
    name = name,
    number = value.toLong()
  )
}

fun OperationMonkey(name: String, firstMonkey: String, operation: String, secondMonkey: String): Monkey {
  return Monkey(
    name = name,
    operation = operation,
    originalWaitingOn = listOf(firstMonkey, secondMonkey)
  )
}