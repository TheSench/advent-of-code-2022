package day22

import groupByBlanks
import runDay
import toPair
import java.awt.Point

fun main() {

    fun part1(input: List<String>) =
        input.parse()
            .let { (grid, instructions) ->
                val initialX = grid[0].indexOf(MapTile.OPEN)
                val startPosition = Position(Point(initialX, 0), Direction.RIGHT)
                instructions.fold(startPosition) { position, instruction ->
                    when (instruction) {
                        is TurnLeft -> position.turnLeft()
                        is TurnRight -> position.turnRight()
                        is Move -> position.move(instruction.spaces, grid)
                    }
                }
            }

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

enum class Direction(unitOfMovement: Point) {
    RIGHT(Point(1, 0)),
    LEFT(Point(-1, 0)),
    UP(Point(0, -1)),
    DOWN(Point(0, 1));

    fun turnRight() = when (this) {
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        UP -> RIGHT
    }

    fun turnLeft() = when (this) {
        RIGHT -> UP
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
    }
}

data class Position(val point: Point, val direction: Direction) {
    fun turnRight() = copy(direction = direction.turnRight())
    fun turnLeft() = copy(direction = direction.turnLeft())
    fun move(spaces: Int, grid: Grid) = copy()
}