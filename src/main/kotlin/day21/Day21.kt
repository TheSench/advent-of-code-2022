package day21

import runDay
import java.lang.RuntimeException

fun main() {

    fun part1(input: List<String>): Long = input.map { it.toMonkey() }
      .processMonkeys()
      .let {
        it["root"]?.number!!
      }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 152L,
        part2 = ::part2,
        part2Check = -1,
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

private fun Monkey.toEquation(allMonkeys: Map<String, Monkey>, maxDepth: Int = Int.MAX_VALUE, currentDepth: Int = 0): String = when {
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
    var waitingOn: List<String> = emptyList(),
)

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
        waitingOn = listOf(firstMonkey, secondMonkey)
    )
}