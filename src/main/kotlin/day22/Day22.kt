package day22

import groupByBlanks
import runDay
import toPair
import java.lang.IllegalArgumentException

fun main() {

    fun part1(input: List<String>) = input.parse()

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun List<String>.parse() = groupByBlanks().toPair().let { (mapLines, instructionLines) ->
    mapLines.toMap() to instructionLines.toInstructions()
}

fun List<String>.toMap(): Grid = map { row ->
    row.map {
        when (it) {
            ' ' -> MapTile.VOID
            '.' -> MapTile.OPEN
            '#' -> MapTile.WALL
            else -> throw IllegalArgumentException("Invalid Map Tile: $it")
        }
    }
}

fun List<String>.toInstructions() = flatMap { line ->
    line.fold(mutableListOf<String>()) { chars, next ->
        val last = chars.lastOrNull()
        if (next.isDigit() && last?.lastOrNull()?.isDigit() == true) {
            chars[chars.size - 1] = last + next
        } else {
            chars.add("$next")
        }
        chars
    }
}.map {
    it.toInstruction()
}

fun String.toInstruction() = when (this) {
    "R" -> TurnRight
    "L" -> TurnLeft
    else -> Move(this.toInt())
}

enum class MapTile {
    OPEN,
    WALL,
    VOID
}
typealias Grid = List<List<MapTile>>

sealed interface Instruction
object TurnLeft : Instruction
object TurnRight : Instruction
data class Move(val spaces: Int) : Instruction
