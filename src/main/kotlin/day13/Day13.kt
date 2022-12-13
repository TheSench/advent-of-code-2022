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

sealed interface PacketData {
    operator fun compareTo(other: PacketData): Int
}

@JvmInline
value class ListData(val data: List<PacketData>) : PacketData {
    constructor(vararg values: PacketData) : this(values.toList())

    override operator fun compareTo(other: PacketData): Int = when (other) {
        is IntData -> this.compareTo(ListData(other))
        is ListData -> this.data.zip(other.data)
            .fold(0) { _, (left, right) ->
                val comparison = left.compareTo(right)
                if (comparison != 0) return comparison
                0
            }.let { comparison ->
                when (comparison) {
                    0 -> this.data.size.compareTo(other.data.size)
                    else -> comparison
                }
            }
    }
}

@JvmInline
value class IntData(val data: Int) : PacketData {
    override operator fun compareTo(other: PacketData): Int = when (other) {
        is IntData -> this.data.compareTo(other.data)
        is ListData -> ListData(this).compareTo(other)
    }
}