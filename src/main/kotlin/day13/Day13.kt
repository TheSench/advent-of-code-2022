package day13

import runDay

fun main() {
    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun String.parse(): PacketData {
    val stack = ArrayDeque<MutableList<PacketData>>()
    var current = mutableListOf<PacketData>()
    var num = ""
    for (char in this) {
        when (char) {
            '[' -> {
                stack.addFirst(current)
                current = mutableListOf()
            }
            ']' -> {
                if (num.isNotBlank()) {
                    current.add(IntData(num.toInt()))
                    num = ""
                }
                val next = ListData(current)
                current = stack.removeFirst()
                current.add(next)
            }
            ',' -> {
                if (num.isNotBlank()) {
                    current.add(IntData(num.toInt()))
                    num = ""
                }
            }
            else -> num += char
        }
    }
    return current.first()
}

sealed interface PacketData
@JvmInline
value class ListData(val data: List<PacketData>) : PacketData {
    constructor(vararg data: PacketData) : this(data.toList())
}
@JvmInline
value class IntData(val data: Int) : PacketData