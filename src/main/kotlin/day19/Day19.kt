package day19

import runDay
import stackOf
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun part1(input: List<String>) = input
        .map(String::toBlueprint)

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = -1,
        part2 = ::part2,
        part2Check = -1,
    )
}


val oreRegex = Regex("""Each ore robot costs (\d+) ore.""")
val clayRegex = Regex("""Each clay robot costs (\d+) ore.""")
val obsidianRegex = Regex("""Each obsidian robot costs (\d+) ore and (\d+) clay.""")
val geodeRegex = Regex("""Each geode robot costs (\d+) ore and (\d+) obsidian""")
fun String.toBlueprint() = Blueprint(
    ore = oreRegex.matchEntire(this)!!.destructured.let { (ore) ->
        Cost(ore = ore.toInt())
    },
    clay = clayRegex.matchEntire(this)!!.destructured.let { (ore) ->
        Cost(ore = ore.toInt())
    },
    obsidian = obsidianRegex.matchEntire(this)!!.destructured.let { (ore, clay) ->
        Cost(
            ore = ore.toInt(),
            clay = clay.toInt()
        )
    },
    geode = geodeRegex.matchEntire(this)!!.destructured.let { (ore, obsidian) ->
        Cost(
            ore = ore.toInt(),
            obsidian = obsidian.toInt()
        )
    }
)

data class Blueprint(
    val ore: Cost,
    val clay: Cost,
    val obsidian: Cost,
    val geode: Cost,
)

data class Cost(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
)