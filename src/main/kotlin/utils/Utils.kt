import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String) = "$day".padStart(2, '0').let { dir ->
    {}.javaClass.getResourceAsStream("day$dir/$name.txt")!!.bufferedReader().readLines()
}

fun List<String>.groupByBlanks(): List<List<String>> {
    var current: List<String> = listOf()
    var groups: List<List<String>> = listOf()
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

fun runDay(day: Int, part1: (List<String>) -> Int, part1Check: Int, part2: (List<String>) -> Int, part2Check: Int) {
    val testInput = readInput(day, "test")
    val input = readInput(day, "input")

    checkAndRun(testInput, input, part1Check, part1)
    checkAndRun(testInput, input, part2Check, part2)
}

fun checkAndRun(
    testInput: List<String>,
    input: List<String>,
    checkValue: Int,
    partFn: (List<String>) -> Int
) {
    val testValue = partFn(testInput)
    check(partFn(testInput) == checkValue) {
        "Expected $checkValue but saw $testValue"
    }
    println(partFn(input))
}

fun <T> List<T>.toPair() = Pair(this[0], this[1])

fun <T> stackOf(vararg items: T): ArrayDeque<T> = ArrayDeque(items.toList())