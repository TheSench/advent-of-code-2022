package day22

import groupByBlanks
import runDay
import stackOf
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
                    }
                }
            }.toScore()

    fun part2(input: List<String>) =
        input.parse().let { (grid, instructions) ->
            val cube = grid.toCube()
            val startPosition = Position(Point(0, 0), Direction.RIGHT)
            instructions.fold(startPosition) { position, instruction ->
                when (instruction) {
                    is TurnLeft -> position.turnLeft()
                    is TurnRight -> position.turnRight()
                    is Move -> position.move(instruction.spaces, cube)
                }
            }.let(cube::absoluteLocation)
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

fun Grid.toCube() = this.getSideLength().let { sideLength ->
    val faces = this.findFaces(sideLength)
    val box = BoxTemplate.from(faces)
    Cube(
        sideLength,
        faces,
        List(faces.size) { index -> box.getEdges(index) }
    )
}

fun Grid.getSideLength(): Int {
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
                val offset = Point(xOffset / sideLength, yOffset / sideLength)
                faces.add(
                    sideStart to Face(
                        getFace(sideStart, sideLength),
                        offset,
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

class Cube(
    private val sideLength: Int,
    private val faces: List<Face>,
    private val allEdges: List<Map<Direction, FaceSide>>
) {
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
        val edges = allEdges[currentSide]
        val nextSide = edges[position.direction]!!
        val sideChange = position.direction to nextSide.edge
        val nextPoint = translate(position.point, sideChange)
        val nextFace = faces[nextSide.region]
        return if (nextFace[nextPoint] != MapTile.WALL) {
            val newDirection = when (sideChange.second) {
                Direction.UP -> Direction.DOWN
                Direction.DOWN -> Direction.UP
                Direction.LEFT -> Direction.RIGHT
                Direction.RIGHT -> Direction.LEFT
            }
            currentFace = nextFace
            currentSide = nextSide.region
            Position(nextPoint, newDirection)
        } else {
            null
        }
    }

    private fun translate(point: Point, sideChange: Pair<Direction, Direction>) = with(point) {
        when (sideChange) {
            (Direction.UP to Direction.UP) -> {
                Point(end - x, 0)
            }

            (Direction.DOWN to Direction.UP) -> {
                Point(x, 0)
            }

            (Direction.LEFT to Direction.UP) -> {
                Point(y, 0)
            }

            (Direction.RIGHT to Direction.UP) -> {
                Point(end - y, 0)
            }

            (Direction.UP to Direction.DOWN) -> {
                Point(x, end)
            }

            (Direction.DOWN to Direction.DOWN) -> {
                Point(end - x, end)
            }

            (Direction.LEFT to Direction.DOWN) -> {
                Point(end - y, end)
            }

            (Direction.RIGHT to Direction.DOWN) -> {
                Point(y, end)
            }

            (Direction.UP to Direction.LEFT) -> {
                Point(0, x)
            }

            (Direction.DOWN to Direction.LEFT) -> {
                Point(0, end - x)
            }

            (Direction.LEFT to Direction.LEFT) -> {
                Point(0, end - y)
            }

            (Direction.RIGHT to Direction.LEFT) -> {
                Point(0, y)
            }

            (Direction.UP to Direction.RIGHT) -> {
                Point(end, end - x)
            }

            (Direction.DOWN to Direction.RIGHT) -> {
                Point(end, x)
            }

            (Direction.LEFT to Direction.RIGHT) -> {
                Point(end, y)
            }

            (Direction.RIGHT to Direction.RIGHT) -> {
                Point(end, end - y)
            }

            else -> throw IllegalArgumentException("Invalid side combination")
        }
    }

    fun absoluteLocation(position: Position) = (faces[currentSide].offset * sideLength)
        .let { offset ->
            position.copy(
                point = position.point + offset
            )
        }
}

operator fun Point.times(value: Int) = Point(
    x = x * value,
    y = y * value,
)

open class Face(
    private val tiles: Grid,
    val offset: Point,
) {
    operator fun contains(point: Point) = point in tiles
    operator fun get(point: Point) = tiles[point]
}

class RingNode<T>(val value: T) {
    lateinit var previous: RingNode<T>
    lateinit var next: RingNode<T>

    companion object {
        operator fun <T> invoke(vararg values: T): RingNode<T> {
            val nodes = values.map(::RingNode)
            val head = nodes.first()
            nodes.reduce { previous, next ->
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

    fun find(value: T): RingNode<T> {
        return if (this.value == value) {
            this
        } else {
            next.find(value)
        }
    }

    operator fun iterator(): Iterator<T> {
        return RingIterator(this)
    }
}

class RingIterator<T>(private val start: RingNode<T>) : Iterator<T> {

    private var current = start
    private var first = true

    override fun hasNext(): Boolean {
        return current != start || first
    }

    override fun next(): T {
        first = false
        current = current.next
        return current.value
    }
}

typealias Ring<T> = RingNode<T>

data class FaceSide(val region: Int, val edge: Direction)

val sides = Ring(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)

class BoxTemplate {
    enum class Side {
        A, B, C, D, E, F;
    }

    private val Side.edges
        get() = when (this) {
            Side.A -> Ring(Edge.AD, Edge.AF, Edge.AB, Edge.AE)
            Side.B -> Ring(Edge.AB, Edge.BF, Edge.BC, Edge.BE)
            Side.C -> Ring(Edge.BC, Edge.CF, Edge.CD, Edge.CE)
            Side.D -> Ring(Edge.CD, Edge.DF, Edge.AD, Edge.DE)
            Side.E -> Ring(Edge.AE, Edge.BE, Edge.CE, Edge.DE)
            Side.F -> Ring(Edge.AF, Edge.DF, Edge.CF, Edge.BF)
        }

    fun getEdgeDirections(side: Side) =
        side.edges.iterator().asSequence().associateBy { edge -> edgeDirections[side to edge]!! }

    enum class Edge(vararg sides: Side) {
        AB(Side.A, Side.B), AD(Side.A, Side.D), AE(Side.A, Side.E), AF(Side.A, Side.F),
        BC(Side.B, Side.C), BE(Side.B, Side.E), BF(Side.B, Side.F),
        CD(Side.C, Side.D), CE(Side.C, Side.E), CF(Side.C, Side.F),
        DE(Side.D, Side.E), DF(Side.D, Side.F);

        private val sides: List<Side> = sides.toList()

        fun otherSide(side: Side) = sides.find { it != side }!!
    }

    private val sidesToRegions = mutableMapOf<Side, Int>()
    private val regionsToSides = mutableMapOf<Int, Side>()
    private val edgesToFaceSides = mutableMapOf<Edge, Pair<FaceSide, FaceSide?>>()
    private val edgeDirections = mutableMapOf<Pair<Side, Edge>, Direction>()

    fun addFaceToSide(mapping: FaceMapping) {
        val (edge, side, faceSide) = mapping
        sidesToRegions[side] = faceSide.region
        regionsToSides[faceSide.region] = side
        val firstEdge = sides.find(faceSide.edge)
        (1..4).fold(side.edges.find(edge) to firstEdge) { (boxEdge, faceEdge), _ ->
            val newFaceSide = faceSide.copy(edge = faceEdge.value)
            edgeDirections[side to boxEdge.value] = newFaceSide.edge
            edgesToFaceSides[boxEdge.value] =
                edgesToFaceSides[boxEdge.value]?.copy(second = newFaceSide) ?: (newFaceSide to null)
            boxEdge.next to faceEdge.next
        }
    }

    fun getEdges(region: Int) = regionsToSides[region]!!.let { side ->
        side.edges.iterator().asSequence().associate {
            val faceSides = edgesToFaceSides[it]!!
            if (faceSides.first.region == region) {
                faceSides.first.edge to faceSides.second!!
            } else {
                faceSides.second!!.edge to faceSides.first
            }
        }
    }

    data class FaceMapping(
        val edge: Edge,
        val side: Side,
        val faceSide: FaceSide,
    )

    companion object {
        fun from(faces: List<Face>): BoxTemplate {
            val template = BoxTemplate()
            val seen = mutableSetOf(0)
            val queue = stackOf(FaceMapping(Edge.AD, Side.A, FaceSide(0, Direction.UP)))
            while (queue.size > 0) {
                val mapping = queue.removeFirst()
                template.addFaceToSide(mapping)
                seen.add(mapping.faceSide.region)
                val edgeDirections = template.getEdgeDirections(mapping.side)
                val face = faces[mapping.faceSide.region]
                val potentialNeighbors = face.offset.neighbors
                val neighbors = faces.mapIndexed { region, neighbor ->
                    region to neighbor
                }.filter { (region, neighbor) ->
                    region !in seen && neighbor.offset in potentialNeighbors
                }.map { (region, neighbor) ->
                    val edgeDirection = when (neighbor.offset) {
                        face.offset.copy(x = face.offset.x + 1) -> Direction.RIGHT
                        face.offset.copy(x = face.offset.x - 1) -> Direction.LEFT
                        face.offset.copy(y = face.offset.y + 1) -> Direction.DOWN
                        face.offset.copy(y = face.offset.y - 1) -> Direction.UP
                        else -> throw IllegalArgumentException()
                    }
                    val edge = edgeDirections[edgeDirection]!!
                    val otherSide = edge.otherSide(mapping.side)
                    FaceMapping(edge, otherSide, FaceSide(region, edgeDirection.opposite()))
                }
                queue.addAll(neighbors)
            }
            return template
        }
    }
}

val Point.neighbors
    get() = setOf(
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x, y + 1),
        Point(x, y - 1)
    )

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