fun main() {
    fun part1(input: List<String>): Int {
        return input.mapToRucksacks()
            .map { rucksack ->
                rucksack.first.find { rucksack.second.contains(it) }!!
            }
            .map(Char::priority)
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.toSet() }
            .chunked(3)
            .map { it.reduce(Set<Char>::intersect).first() }
            .map { it.priority }
            .sum()
    }

    runDay(
        day = 3,
        part1 = ::part1,
        part1Check = 157,
        part2 = ::part2,
        part2Check = 70
    )
}

private fun List<String>.mapToRucksacks() = map(String::toRucksack)
private fun String.toRucksack() =
    length.let { length ->
        val half = length / 2
        chunked(half).let {
            Pair(it[0], it[1])
        }
    }

val Char.priority: Int
    get() = when (this) {
        in 'a'..'z' -> code - ('a'.code) + 1
        in 'A'..'Z' -> code - ('A'.code) + 27
        else -> throw IllegalArgumentException()
    }