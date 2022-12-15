import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

fun resource(day: Int, name: String): InputStream? = "$day".padStart(2, '0').let { dir ->
    {}.javaClass.getResourceAsStream("day$dir/$name.txt")
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String) = resource(day, name)!!.bufferedReader().readLines()

fun exists(day: Int, name: String) = resource(day, name) != null

typealias Lines = List<String>

fun Lines.groupByBlanks(): List<Lines> {
    var current: Lines = listOf()
    var groups: List<Lines> = listOf()
    for (line: String in this) {
        if (line.isBlank()) {
            groups = groups.plusElement(current)
            current = emptyList()
        } else {
            current = current + line
        }
    }
    if (!current.isEmpty()) {
        groups = groups.plusElement(current)
    }
    return groups
}

fun <T, R> List<List<T>>.mapGroups(transform: (T) -> R) = this.map {
    it.map(transform)
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

inline fun <reified Day, T, U> Day.runDay(
    noinline part1: (Lines) -> T,
    part1Check: T,
    noinline part2: (Lines) -> U,
    part2Check: U,
) {
    runDay(
        this.day,
        part1,
        part1Check,
        part2,
        part2Check,
    )
}

inline val <reified Day> Day.day: Int
    get() = Day::class.java.packageName.replace("day", "").toInt()

fun <T, U> runDay(
    day: Int,
    part1: (Lines) -> T,
    part1Check: T,
    part2: (Lines) -> U,
    part2Check: U,
) {
    val testInput = readInput(day, "test")
    val input = readInput(day, "input")
    val testInput2 = if (exists(day, "test2")) readInput(day, "test2") else testInput

    checkAndRun(testInput, input, part1Check, part1)
    checkAndRun(testInput2, input, part2Check, part2)
}

fun <T> checkAndRun(
    testInput: Lines,
    input: Lines,
    checkValue: T,
    partFn: (Lines) -> T
) {
    val testValue = partFn(testInput)
    check(testValue == checkValue) {
        "Expected $checkValue but saw $testValue"
    }
    println(partFn(input))
}

fun <T> List<T>.toPair() = Pair(this[0], this[1])

fun <T> stackOf(vararg items: T): ArrayDeque<T> = ArrayDeque(items.toList())

fun <T> List<T>.toStack() = ArrayDeque(this)

fun Int.toUnit() = when {
    (this < 0) -> -1
    (this == 0) -> 0
    else -> 1
}