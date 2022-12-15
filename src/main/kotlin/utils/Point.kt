package utils

import toUnit

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    operator fun plus(other: Point) = Point(
        x = this.x + other.x,
        y = this.y + other.y,
    )

    operator fun minus(other: Point) = Point(
        x = this.x - other.x,
        y = this.y - other.y,
    )

    override fun compareTo(other: Point): Int =
        comparator.compare(this, other)

    operator fun rangeTo(endInclusive: Point) =
        OrthogonalPointRange(this, endInclusive)

    fun toUnit() = Point(x.toUnit(), y.toUnit())

    companion object {
        val comparator = compareBy<Point> { it.x }.thenBy { it.y }
    }
}

class OrthogonalPointRange(
    override val start: Point,
    override val endInclusive: Point,
) : ClosedRange<Point>, Iterable<Point> {
    val step = (endInclusive - start).toUnit()
    override operator fun iterator(): Iterator<Point> {
        return PointIterator(start, endInclusive, step)
    }
}

class PointIterator(
    start: Point,
    private val endInclusive: Point,
    private val step: Point,
) : Iterator<Point> {
    private var currentValue = start
    override fun hasNext() = (currentValue - step) != endInclusive

    override fun next(): Point {
        return currentValue.also {
            currentValue += step
        }
    }
}
