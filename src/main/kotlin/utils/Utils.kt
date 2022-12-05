import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String) = "$day".padStart(2, '0').let { dir ->
    {}.javaClass.getResourceAsStream("day$dir/$name.txt")!!.bufferedReader().readLines()
}

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

fun <T, U> runDay(day: Int, part1: (Lines) -> T, part1Check: T, part2: (Lines) -> U, part2Check: U) {
    val testInput = readInput(day, "test")
    val input = readInput(day, "input")

    checkAndRun(testInput, input, part1Check, part1)
    checkAndRun(testInput, input, part2Check, part2)
}

fun <T> checkAndRun(
    testInput: Lines,
    input: Lines,
    checkValue: T,
    partFn: (Lines) -> T
) {
    val testValue = partFn(testInput)
    check(partFn(testInput) == checkValue) {
        "Expected $checkValue but saw $testValue"
    }
    println(partFn(input))
}

fun <T> List<T>.toPair() = Pair(this[0], this[1])

fun <T> stackOf(vararg items: T): ArrayDeque<T> = ArrayDeque(items.toList())

fun <T> List<T>.toStack() = ArrayDeque(this)