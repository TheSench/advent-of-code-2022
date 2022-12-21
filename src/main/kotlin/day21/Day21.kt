package day21

import runDay
import java.lang.RuntimeException

fun main() {

    fun part1(input: List<String>): Int = input.map { it.toMonkey() }
        .let { monkeys ->
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
                    "/" -> numbers[0] - numbers[1]
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
                    println(seenMonkeys["root"]!!.toEquation(seenMonkeys))
                    return rootNumber
                }
            }
            throw RuntimeException("Did not find root number")
        }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 152,
        part2 = ::part2,
        part2Check = -1,
    )
}

private fun Monkey.toEquation(allMonkeys: Map<String, Monkey>): String = when (this.operation) {
    null -> this.number.toString()
    else -> {
        val first = allMonkeys[this.waitingOn[0]]!!.toEquation(allMonkeys)
        val second = allMonkeys[this.waitingOn[1]]!!.toEquation(allMonkeys)
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
    var number: Int? = null,
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
        number = value.toInt()
    )
}

fun OperationMonkey(name: String, firstMonkey: String, operation: String, secondMonkey: String): Monkey {
    return Monkey(
        name = name,
        operation = operation,
        waitingOn = listOf(firstMonkey, secondMonkey)
    )
}