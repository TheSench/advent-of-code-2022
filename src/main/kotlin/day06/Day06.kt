package day06

import runDay
import kotlin.collections.ArrayDeque

fun main() {
    fun part1(input: List<String>): List<Int> = input.map {
        it.findFirstDistinct(4)
    }

    fun part2(input: List<String>): List<Int> =input.map {
        it.findFirstDistinct(14)
    }

    runDay(
        day = 6,
        part1 = ::part1,
        part1Check = listOf(7, 5, 6, 10, 11),
        part2 = ::part2,
        part2Check = listOf(19, 23, 23, 29, 26),
    )
}

private fun String.findFirstDistinct(requiredLength: Int) =
    Buffer(this.subSequence(0 until requiredLength))
        .also { buffer ->
            this.asSequence()
                .drop(4)
                .takeWhile { !buffer.allUnique() }
                .forEach(buffer::addChar)
        }.end

private class Buffer(chars: CharSequence) {
    var start = 1
        private set
    var end = 4
        private set
    val charStack = ArrayDeque(chars.toList())

    fun addChar(char: Char) {
        charStack.removeFirst()
        charStack.addLast(char)
        start++
        end++
    }

    fun allUnique() = charStack.distinct().size == charStack.size
}