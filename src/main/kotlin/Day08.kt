fun main() {
    fun part1(input: List<String>) =
        input.toGrid().visibilityMap.sumOf { row -> row.count { it } }

    fun part2(input: List<String>) =
        input.toGrid().let { grid ->
            grid.mapCellsIndexed { x, y, value ->
                grid.scenicScore(x, y, value)
            }
        }.maxOf { it.max() }


    runDay(
        day = 8,
        part1 = ::part1,
        part1Check = 21,
        part2 = ::part2,
        part2Check = 8,
    )
}

private typealias Grid<T> = List<List<T>>
private typealias MutableGrid<T> = MutableList<MutableList<T>>

private fun Grid<Int>.scenicScore(x: Int, y: Int, value: Int) =
    this[y].countHigher(value, (0 until x).reversed()) *
            this[y].countHigher(value, (x + 1 until this[y].size)) *
            this.countHigher(x, value, (0 until y).reversed()) *
            this.countHigher(x, value, (y + 1 until this[y].size))

private fun Grid<Int>.countHigher(x: Int, value: Int, yVals: IntProgression): Int {
    var count = 0
    for (i in yVals) {
        count++
        if (this[x, i] >= value) break
    }
    return count
}

private fun List<Int>.countHigher(value: Int, xVals: IntProgression): Int {
    var count = 0
    for (i in xVals) {
        count++
        if (this[i] >= value) break
    }
    return count
}

private fun List<String>.toGrid() = map(String::toHeights)

private val Grid<Int>.visibilityMap
    get() = visibleFromTop()
        .or(visibleFromBottom())
        .or(visibleFromLeft())
        .or(visibleFromRight())

private fun Grid<Int>.emptyVisibilityMap() = MutableList(this.size) {
    MutableList(this[0].size) { false }
}

private fun Grid<Boolean>.or(other: Grid<Boolean>) =
    this.mapCellsIndexed { x, y, cell ->
        cell || other[x, y]
    }

private operator fun <T> Grid<T>.get(x: Int, y: Int): T = this[y][x]
private operator fun <T> MutableGrid<T>.set(x: Int, y: Int, value: T) {
    this[y][x] = value
}

private fun <T, U> Grid<T>.mapCellsIndexed(transform: (x: Int, y: Int, value: T) -> U) =
    this.mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            transform(x, y, cell)
        }
    }

private fun Grid<Int>.visibleFromTop(): Grid<Boolean> =
    emptyVisibilityMap()
        .also { visibilityMap ->
            List(this[0].size) { x ->
                this.foldIndexed(-1) { y, highest, row ->
                    visibilityMap[x, y] = row[x] > highest
                    maxOf(row[x], highest)
                }
            }
        }

private fun Grid<Int>.visibleFromBottom(): Grid<Boolean> =
    emptyVisibilityMap()
        .also { visibilityMap ->
            List(this[0].size) { x ->
                this.foldRightIndexed(-1) { y, row, highest ->
                    visibilityMap[x, y] = row[x] > highest
                    maxOf(row[x], highest)
                }
            }
        }

private fun Grid<Int>.visibleFromLeft(): Grid<Boolean> =
    emptyVisibilityMap()
        .also { visibilityMap ->
            this.mapIndexed { y, row ->
                row.foldIndexed(-1) { x, highest, height ->
                    visibilityMap[x, y] = height > highest
                    maxOf(height, highest)
                }
            }
        }

private fun Grid<Int>.visibleFromRight(): Grid<Boolean> =
    emptyVisibilityMap()
        .also { visibilityMap ->
            this.mapIndexed { y, row ->
                row.foldRightIndexed(-1) { x, height, highest ->
                    visibilityMap[x, y] = height > highest
                    maxOf(height, highest)
                }
            }
        }

private const val ZERO = '0'.code
private fun String.toHeights() = map {
    it.code - ZERO
}
