package utils

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(
        x = this.x + other.x,
        y = this.y + other.y,
    )

    operator fun minus(other: Point) = Point(
        x = this.x - other.x,
        y = this.y - other.y,
    )
}