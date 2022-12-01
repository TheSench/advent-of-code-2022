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
