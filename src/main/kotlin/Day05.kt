import kotlin.collections.ArrayDeque

fun main() {
    fun part1(input: List<String>): String = input.parsed()
        .moveCrates { stacks, instruction ->
            repeat(instruction.count) {
                val container = stacks[instruction.from].removeLast()
                stacks[instruction.to].add(container)
            }
        }
        .getTopCrate()

    fun part2(input: List<String>): String = input.parsed()
        .moveCrates { stacks, instruction ->
            (1..instruction.count)
                .map {
                    stacks[instruction.from].removeLast()
                }
                .reversed()
                .let { stacks[instruction.to].addAll(it) }
        }
        .getTopCrate()

    runDay(
        day = 5,
        part1 = ::part1,
        part1Check = "CMZ",
        part2 = ::part2,
        part2Check = "MCD"
    )
}

private fun Stacks.getTopCrate() = map { it.last() }
    .let {
        String(it.toCharArray())
    }

private fun ParsedInput.moveCrates(processInstruction: (Stacks, Instruction) -> Unit): Stacks = let { pair ->
    val instructions = pair.second
    val stacks = pair.first
    instructions.forEach { processInstruction(stacks, it) }
    stacks
}

private typealias ParsedInput = Pair<Stacks, List<Instruction>>

private fun Lines.parsed() =
    groupByBlanks().let {
        Pair(it[0].toStacks(), it[1].toInstructions())
    }

private typealias Stacks = List<ArrayDeque<Char>>

fun List<String>.toStacks(): Stacks = reversed().let {
    Pair(
        it.first().toColumns(),
        it.drop(1)
    )
}.let { pair ->
    pair.first.map { column ->
        pair.second.map {
            if (it.length > column) {
                it[column]
            } else {
                ' '
            }
        }.filter { it != ' ' }.toStack()
    }
}.toList()

private val columnRegex = Regex("""\d+""")
private fun String.toColumns() =
    columnRegex.findAll(this)
        .map { it.range.first }

private data class Instruction(
    val count: Int,
    val from: Int,
    val to: Int,
)

private const val COUNT = "count"
private const val FROM = "from"
private const val TO = "to"
private val instructionRegex =
    Regex("""move (?<$COUNT>\d+) from (?<$FROM>\d+) to (?<$TO>\d+)""")

private fun List<String>.toInstructions(): List<Instruction> =
    mapNotNull {
        instructionRegex.find(it)
    }.map {
        Instruction(
            count = it.groups[COUNT]!!.value.toInt(),
            from = it.groups[FROM]!!.value.toInt() - 1,
            to = it.groups[TO]!!.value.toInt() - 1,
        )
    }
