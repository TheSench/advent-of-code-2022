package day22

import groupByBlanks
import runDay
import toPair
import utils.Point

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
                    }/*.also {
                        println(it)
                    }*/
                }
            }.toScore()

    fun part2(input: List<String>) =
        input.parse().let {
            it.first.toCube()
        }

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 6032,
        part2 = ::part2,
        part2Check = -1,
    )
}

fun List<String>.parse() = groupByBlanks().toPair().let { (mapLines, instructionLines) ->
    mapLines.toMap() to instructionLines.toInstructions()
}

fun Grid.print(start: Position, end: Position, positions: List<Position>) = mapIndexed { y, row ->
    val positionDisplay = positions.associate {
        it.point to when (it) {
            start -> 'S'
            end -> 'E'
            else -> when (it.direction) {
                Direction.LEFT -> '<'
                Direction.RIGHT -> '>'
                Direction.DOWN -> 'v'
                Direction.UP -> '^'
            }
        }
    }
    row.mapIndexed { x, space ->
        when (space) {
            MapTile.VOID -> ' '
            MapTile.WALL -> '#'
            MapTile.OPEN -> if (positionDisplay.containsKey(Point(x, y))) {
                positionDisplay[Point(x, y)]
            } else {
                '.'
            }
        }
    }.joinToString("")
}.joinToString("\n").let { println(it) }

fun Grid.toCube() = this[0].count { it != MapTile.VOID }.let { sideLength ->
    Cube(
        sideLength,
        listOf(
            this.getFace(Point(sideLength * 2, 0), sideLength),
            this.getFace(Point(0, sideLength), sideLength),
            this.getFace(Point(sideLength, sideLength), sideLength),
            this.getFace(Point(sideLength * 2, sideLength), sideLength),
            this.getFace(Point(sideLength * 2, sideLength * 2), sideLength),
            this.getFace(Point(sideLength * 3, sideLength * 2), sideLength)
        )
    )
}

fun Grid.getFace(start: Point, sideLength: Int) =
    subList(start.y, start.y + sideLength).map {
        it.subList(start.x, start.x + sideLength)
    }

class Cube(val sideLength: Int, val faces: List<Grid>) {
    var currentSide = 0
    val currentFace = faces[currentSide]
    val end = sideLength - 1
    fun tryMove(position: Position): Position? {
        val next = position.next()
        val nextPoint = next.point
        return when {
            (nextPoint !in currentFace) -> wrapAround(position)
            currentFace[nextPoint] == MapTile.WALL -> null
            currentFace[nextPoint] == MapTile.OPEN -> next
            else -> wrapAround(position)
        }
    }

    fun wrapAround(position: Position): Position? {
        val nextSide = nextSide(position.direction)
        val sideChange = getSideChange(nextSide)
        val nextPoint = translate(position.point, sideChange)
        val nextFace = faces[nextSide]
        return if (nextFace[nextPoint] != MapTile.WALL) {
            position.copy(
                point = nextPoint
            )
        } else {
            null
        }
    }

    private fun nextSide(direction: Direction) = when (currentSide to direction) {
        (0 to Direction.UP) -> 1
        (0 to Direction.DOWN) -> 3
        (0 to Direction.LEFT) -> 2
        (0 to Direction.RIGHT) -> 5

        (1 to Direction.UP) -> 0
        (1 to Direction.DOWN) -> 4
        (1 to Direction.LEFT) -> 5
        (1 to Direction.RIGHT) -> 2

        (2 to Direction.UP) -> 0
        (2 to Direction.DOWN) -> 4
        (2 to Direction.LEFT) -> 1
        (2 to Direction.RIGHT) -> 3

        (3 to Direction.UP) -> 0
        (3 to Direction.DOWN) -> 4
        (3 to Direction.LEFT) -> 2
        (3 to Direction.RIGHT) -> 5

        (4 to Direction.UP) -> 3
        (4 to Direction.DOWN) -> 1
        (4 to Direction.LEFT) -> 2
        (4 to Direction.RIGHT) -> 5

        (5 to Direction.UP) -> 3
        (5 to Direction.DOWN) -> 1
        (5 to Direction.LEFT) -> 4
        (5 to Direction.RIGHT) -> 0

        else -> throw IllegalArgumentException("Invalid side combination")
    }

    private fun getSideChange(nextSide: Int) = when (currentSide to nextSide) {
        (0 to 1) -> (Edge.TOP to Edge.TOP)
        (0 to 3) -> (Edge.BOTTOM to Edge.TOP)
        (0 to 2) -> (Edge.LEFT to Edge.TOP)
        (0 to 5) -> (Edge.RIGHT to Edge.RIGHT)

        (1 to 0) -> (Edge.TOP to Edge.TOP)
        (1 to 4) -> (Edge.BOTTOM to Edge.BOTTOM)
        (1 to 5) -> (Edge.LEFT to Edge.BOTTOM)
        (1 to 2) -> (Edge.RIGHT to Edge.LEFT)

        (2 to 0) -> (Edge.TOP to Edge.LEFT)
        (2 to 4) -> (Edge.BOTTOM to Edge.LEFT)
        (2 to 1) -> (Edge.LEFT to Edge.RIGHT)
        (2 to 3) -> (Edge.RIGHT to Edge.LEFT)

        (3 to 0) -> (Edge.TOP to Edge.BOTTOM)
        (3 to 4) -> (Edge.BOTTOM to Edge.TOP)
        (3 to 2) -> (Edge.LEFT to Edge.RIGHT)
        (3 to 5) -> (Edge.RIGHT to Edge.TOP)

        (4 to 3) -> (Edge.TOP to Edge.BOTTOM)
        (4 to 1) -> (Edge.BOTTOM to Edge.BOTTOM)
        (4 to 2) -> (Edge.LEFT to Edge.BOTTOM)
        (4 to 5) -> (Edge.RIGHT to Edge.LEFT)

        (5 to 3) -> (Edge.TOP to Edge.RIGHT)
        (5 to 1) -> (Edge.BOTTOM to Edge.LEFT)
        (5 to 4) -> (Edge.LEFT to Edge.RIGHT)
        (5 to 0) -> (Edge.RIGHT to Edge.RIGHT)

        else -> throw IllegalArgumentException("Invalid side combination")
    }

    private fun translate(point: Point, sideChange: Pair<Edge, Edge>) = with(point) {
        when (sideChange) {
            (Edge.TOP to Edge.TOP) -> Point(end - x, 0)
            (Edge.BOTTOM to Edge.TOP) -> Point(x, 0)
            (Edge.LEFT to Edge.TOP) -> Point(y, 0)
            (Edge.RIGHT to Edge.TOP) -> Point(end - y, 0)

            (Edge.TOP to Edge.BOTTOM) -> Point(x, end)
            (Edge.BOTTOM to Edge.BOTTOM) -> Point(end - x, end)
            (Edge.LEFT to Edge.BOTTOM) -> Point(end - y, end)
            (Edge.RIGHT to Edge.BOTTOM) -> Point(y, end)

            (Edge.TOP to Edge.LEFT) -> Point(0, x)
            (Edge.BOTTOM to Edge.LEFT) -> Point(0, end - x)
            (Edge.LEFT to Edge.LEFT) -> Point(0, end - y)
            (Edge.RIGHT to Edge.LEFT) -> Point(0, y)

            (Edge.TOP to Edge.RIGHT) -> Point(end, end - x)
            (Edge.BOTTOM to Edge.RIGHT) -> Point(end, x)
            (Edge.LEFT to Edge.RIGHT) -> Point(end, y)
            (Edge.RIGHT to Edge.RIGHT) -> Point(end, end - y)

            else -> throw IllegalArgumentException("Invalid side combination")
        }
    }

    enum class Edge {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT;
    }
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

enum class Direction(val unitOfMovement: Point, val value: Int) {
    RIGHT(Point(1, 0), 0),
    DOWN(Point(0, 1), 1),
    LEFT(Point(-1, 0), 2),
    UP(Point(0, -1), 3);

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

    fun opposite() = turnRight().turnRight()
}

data class Position(val point: Point, val direction: Direction) {
    fun turnRight() = copy(direction = direction.turnRight())
    fun turnLeft() = copy(direction = direction.turnLeft())
    fun move(spaces: Int, grid: Grid): Position {
        var lastValid = this
        for (i in (1..spaces)) {
            lastValid = grid.tryMove(lastValid) ?: break
        }
        return lastValid
    }

    fun move(spaces: Int, cube: Cube): Position {
        var lastValid = this
        for (i in (1..spaces)) {
            lastValid = cube.tryMove(lastValid) ?: break
        }
        return lastValid
    }

    fun next() = copy(
        point = point + direction.unitOfMovement
    )

    fun opposite() = copy(
        direction = direction.opposite()
    )

    fun toScore() = this.point.let { (x, y) ->
        1000L * (y + 1) + 4 * (x + 1) + this.direction.value
    }
}

operator fun Grid.get(point: Point) = this[point.y][point.x]

operator fun Grid.contains(point: Point) =
    point.y in this.indices && point.x in this[point.y].indices

fun Grid.tryMove(position: Position): Position? {
    val next = position.next()
    val nextPoint = next.point
    return when {
        (nextPoint !in this) -> wrapAround(position)
        this[nextPoint] == MapTile.WALL -> null
        this[nextPoint] == MapTile.OPEN -> next
        else -> wrapAround(position)
    }
}

fun Point.opposite() = copy(
    x = -x,
    y = -y,
)

fun Grid.wrapAround(position: Position): Position? {
    var nextOpposite = position.opposite()
    while (nextOpposite.point in this && this[nextOpposite.point] != MapTile.VOID) {
        nextOpposite = nextOpposite.next()
    }
    return nextOpposite.opposite().next().let {
        when (this[it.point]) {
            MapTile.WALL -> null
            else -> it
        }
    }
}