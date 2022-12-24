package day22

import groupByBlanks
import runDay
import toPair
import utils.Point
import java.util.function.Predicate

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
        input.parse().let { (grid, instructions) ->
            val cube = grid.toCube()
            val startPosition = Position(Point(0, 0), Direction.RIGHT)
            cube.printPosition(startPosition)
            instructions.fold(startPosition) { position, instruction ->
                when (instruction) {
                    is TurnLeft -> position.turnLeft()
                    is TurnRight -> position.turnRight()
                    is Move -> position.move(instruction.spaces, cube)
                }.also {
                    cube.printPosition(it)
                }
            }.let(cube::absoluteLocation).also {
                println(it)
            }
        }.toScore()

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 6032,
        part2 = ::part2,
        part2Check = 5031,
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

fun Grid.toCube() = this.getSideLength().let { sideLength ->
    Cube(
        sideLength,
        this.findFaces(sideLength)
    )
}

fun Grid.getSideLength(): Int {
    val checkForVoids = this[0][0] == MapTile.VOID
    val tileCheck = if (this[0][0] == MapTile.VOID) {
        Predicate<MapTile> { it == MapTile.VOID }
    } else {
        Predicate<MapTile> { it != MapTile.VOID }
    }
    return kotlin.math.min(
        this.takeWhile { tileCheck.test(it[0]) }.count(),
        this[0].takeWhile { tileCheck.test(it) }.count(),
    )
}

fun Grid.findFaces(sideLength: Int): List<Face> {
    val faces = mutableListOf<Pair<Point, Face>>()
    val start = Point(0, 0)
    (indices step sideLength).forEach { yOffset ->
        (this[yOffset].indices step sideLength).forEach { xOffset ->
            val sideStart = start + Point(xOffset, yOffset)
            if (sideStart in this && this[sideStart] != MapTile.VOID) {
                faces.add(
                    sideStart to Face(
                        getFace(sideStart, sideLength)
                    )
                )
            }
        }
    }
    return faces
        .sortedWith(
            compareBy<Pair<Point, Face>> { (point) -> point.y }
                .thenBy { (point) -> point.x }
        ).map { it.second }
}

fun Grid.getFace(start: Point, sideLength: Int) =
    subList(start.y, start.y + sideLength).map {
        it.subList(start.x, start.x + sideLength)
    }

class Cube(private val sideLength: Int, private val faces: List<Face>) {
    private var currentSide = 0
    private var currentFace = faces[currentSide]
    private val end = sideLength - 1
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

    private fun wrapAround(position: Position): Position? {
        val nextSide = nextSide(position.direction)
        val sideChange = getSideChange(nextSide)
        val nextPoint = translate(position.point, sideChange)
        val nextFace = faces[nextSide]
        return if (nextFace[nextPoint] != MapTile.WALL) {
            val newDirection = when (sideChange.second) {
                Direction.UP -> Direction.DOWN
                Direction.DOWN -> Direction.UP
                Direction.LEFT -> Direction.RIGHT
                Direction.RIGHT -> Direction.LEFT
            }
            currentFace = nextFace
            currentSide = nextSide
            Position(nextPoint, newDirection)
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
        (0 to 1) -> (Direction.UP to Direction.UP)
        (0 to 3) -> (Direction.DOWN to Direction.UP)
        (0 to 2) -> (Direction.LEFT to Direction.UP)
        (0 to 5) -> (Direction.RIGHT to Direction.RIGHT)

        (1 to 0) -> (Direction.UP to Direction.UP)
        (1 to 4) -> (Direction.DOWN to Direction.DOWN)
        (1 to 5) -> (Direction.LEFT to Direction.DOWN)
        (1 to 2) -> (Direction.RIGHT to Direction.LEFT)

        (2 to 0) -> (Direction.UP to Direction.LEFT)
        (2 to 4) -> (Direction.DOWN to Direction.LEFT)
        (2 to 1) -> (Direction.LEFT to Direction.RIGHT)
        (2 to 3) -> (Direction.RIGHT to Direction.LEFT)

        (3 to 0) -> (Direction.UP to Direction.DOWN)
        (3 to 4) -> (Direction.DOWN to Direction.UP)
        (3 to 2) -> (Direction.LEFT to Direction.RIGHT)
        (3 to 5) -> (Direction.RIGHT to Direction.UP)

        (4 to 3) -> (Direction.UP to Direction.DOWN)
        (4 to 1) -> (Direction.DOWN to Direction.DOWN)
        (4 to 2) -> (Direction.LEFT to Direction.DOWN)
        (4 to 5) -> (Direction.RIGHT to Direction.LEFT)

        (5 to 3) -> (Direction.UP to Direction.RIGHT)
        (5 to 1) -> (Direction.DOWN to Direction.LEFT)
        (5 to 4) -> (Direction.LEFT to Direction.RIGHT)
        (5 to 0) -> (Direction.RIGHT to Direction.RIGHT)

        else -> throw IllegalArgumentException("Invalid side combination")
    }

    private fun translate(point: Point, sideChange: Pair<Direction, Direction>) = with(point) {
        when (sideChange) {
            (Direction.UP to Direction.UP) -> Point(end - x, 0)
            (Direction.DOWN to Direction.UP) -> Point(x, 0)
            (Direction.LEFT to Direction.UP) -> Point(y, 0)
            (Direction.RIGHT to Direction.UP) -> Point(end - y, 0)

            (Direction.UP to Direction.DOWN) -> Point(x, end)
            (Direction.DOWN to Direction.DOWN) -> Point(end - x, end)
            (Direction.LEFT to Direction.DOWN) -> Point(end - y, end)
            (Direction.RIGHT to Direction.DOWN) -> Point(y, end)

            (Direction.UP to Direction.LEFT) -> Point(0, x)
            (Direction.DOWN to Direction.LEFT) -> Point(0, end - x)
            (Direction.LEFT to Direction.LEFT) -> Point(0, end - y)
            (Direction.RIGHT to Direction.LEFT) -> Point(0, y)

            (Direction.UP to Direction.RIGHT) -> Point(end, end - x)
            (Direction.DOWN to Direction.RIGHT) -> Point(end, x)
            (Direction.LEFT to Direction.RIGHT) -> Point(end, y)
            (Direction.RIGHT to Direction.RIGHT) -> Point(end, end - y)

            else -> throw IllegalArgumentException("Invalid side combination")
        }
    }

    fun absoluteLocation(position: Position) = position.copy(
        point = position.point + when (currentSide) {
            0 -> Point(sideLength * 2, 0)
            1 -> Point(0, sideLength)
            2 -> Point(sideLength, sideLength)
            3 -> Point(sideLength * 2, sideLength)
            4 -> Point(sideLength * 2, sideLength * 2)
            5 -> Point(sideLength * 3, sideLength * 2)
            else -> throw IllegalArgumentException()
        }
    )

    fun printPosition(position: Position) {
        println("($currentSide) : $position")
    }
}

class Face(
    val tiles: Grid,
) {
    operator fun contains(point: Point) = point in tiles
    operator fun get(point: Point) = tiles[point]
}

class FaceEdge(

)

class RingNode<T>(val value: T) {
    lateinit var previous: RingNode<T>
    lateinit var next: RingNode<T>

    companion object {
        operator fun <T> invoke(vararg values: T) : RingNode<T> {
            val nodes = values.map(::RingNode)
            val head = nodes.first()
            nodes.reduce() { previous, next ->
                previous.next = next
                next.previous = previous
                next
            }
            val last = nodes.last()
            last.next = head
            head.previous = last
            return head
        }
    }

    fun find(value: T) {

    }
}

typealias Ring<T> = RingNode<T>
data class FaceSide(val region: Int, val edge: Direction)
val sides = Ring(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)

class BoxTemplate {
    enum class Side(vararg edgeList: Edge) {
        A(Edge.AD, Edge.AF, Edge.AB, Edge.AE),
        B(Edge.AB, Edge.BF, Edge.BC, Edge.BE),
        C(Edge.BC, Edge.CF, Edge.CD, Edge.CE),
        D(Edge.CD, Edge.DF, Edge.AD, Edge.DE),
        E(Edge.AE, Edge.BE, Edge.CE, Edge.DE),
        F(Edge.AF, Edge.DF, Edge.CF, Edge.BF);
    }

    val Side.edges
        get() = when (this) {
            Side.A -> Ring(Edge.AD, Edge.AF, Edge.AB, Edge.AE)
            Side.B -> Ring(Edge.AB, Edge.BF, Edge.BC, Edge.BE)
            Side.C -> Ring(Edge.BC, Edge.CF, Edge.CD, Edge.CE)
            Side.D -> Ring(Edge.CD, Edge.DF, Edge.AD, Edge.DE)
            Side.E -> Ring(Edge.AE, Edge.BE, Edge.CE, Edge.DE)
            Side.F -> Ring(Edge.AF, Edge.DF, Edge.CF, Edge.BF)
        }

    enum class Edge(vararg sides: Side) {
        AB(Side.A, Side.B), AD(Side.A, Side.D), AE(Side.A, Side.E), AF(Side.A, Side.F),
        BC(Side.B, Side.C), BE(Side.B, Side.E), BF(Side.B, Side.F),
        CD(Side.C, Side.D), CE(Side.C, Side.E), CF(Side.C, Side.F),
        DE(Side.D, Side.E), DF(Side.D, Side.F);

        val sides: List<Side> = sides.toList()

        fun otherSide(side: Side) = sides.find { it != side }!!
    }

    val sidesToRegions = mutableMapOf<Side, Int>()
    val regionsToSides = mutableMapOf<Int, Side>()
    val edgesToFaceSides = mutableMapOf<Edge, Pair<FaceSide?, FaceSide?>>()

    fun Side.addNeighbor(edge: Edge, newSide: FaceSide) {
        val otherSide = edge.otherSide(this)
        sidesToRegions[otherSide] = newSide.region
        regionsToSides[newSide.region] = otherSide
//        otherSide.edges
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