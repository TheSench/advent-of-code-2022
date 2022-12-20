package day19

import runDay
import kotlin.math.max

fun main() {

    fun part1(input: List<String>) = input
        .map(String::toBlueprint)
        .maxOf { it.findBest(24) }

    fun part2(input: List<String>) = 0

    (object {}).runDay(
        part1 = ::part1,
        part1Check = 9,
        part2 = ::part2,
        part2Check = -1,
    )
}

private fun Blueprint.findBest(iterations: Int): Int {
    var states = listOf(State())
    for (i in (1 until iterations)) {
        states = states.flatMap {
            it.toOptions(this)
        }.fold(mutableMapOf<Int, MutableSet<State>>()) { cache, state ->
            val best = cache.computeIfAbsent(state.signature) { mutableSetOf() }
            best.forEach { next ->
                if (next hasStrictlyMoreThan state) {
                    return@fold cache
                } else if (state hasStrictlyMoreThan next) {
                    best.remove(next)
                    best.add(state)
                    return@fold cache
                }
            }
            best.add(state)
            cache
        }.values.flatten()
            .sortedWith(
                compareByDescending<State> { it.geodes }.thenByDescending { it.geodeCollectingRobots }
                    .thenByDescending { it.obsidian }.thenByDescending { it.obsidianCollectingRobots }
                    .thenByDescending { it.clay }.thenByDescending { it.clayCollectingRobots }
                    .thenByDescending { it.ore }.thenByDescending { it.obsidianCollectingRobots }
            ).take(2000)
        println(states.size)
    }
    return states.maxOf { it.geodes }
}

data class State(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,

    val oreCollectingRobots: Int = 1,
    val clayCollectingRobots: Int = 0,
    val obsidianCollectingRobots: Int = 0,
    val geodeCollectingRobots: Int = 0,
) {
    val signature =
        geodeCollectingRobots * 20 + obsidianCollectingRobots * 20 + clayCollectingRobots * 20 + oreCollectingRobots
}

infix fun State.hasStrictlyMoreThan(other: State) =
    ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geodes >= other.geodes

fun State.toOptions(blueprint: Blueprint): List<State> {
    val afterCollecting = this.copy(
        ore = ore + oreCollectingRobots,
        clay = clay + clayCollectingRobots,
        obsidian = obsidian + obsidianCollectingRobots,
        geodes = geodes + geodeCollectingRobots,
    )
    var canSave = false
    if (canBuy(blueprint.geode)) {
        return listOf(afterCollecting.purchase(blueprint.geode))
    } else if (obsidianCollectingRobots > 0) {
        canSave = true
    }
    if (canBuy(blueprint.obsidian) && obsidianCollectingRobots < blueprint.geode.obsidian) {
        return if (canSave) {
            listOf(afterCollecting.purchase(blueprint.obsidian)) + afterCollecting
        } else {
            listOf(afterCollecting.purchase(blueprint.obsidian))
        }
    } else if (clayCollectingRobots > 0) {
        canSave = true
    }

    val options = mutableListOf<State>()
    if (canBuy(blueprint.clay) && clayCollectingRobots < blueprint.obsidian.clay) {
        options.add(afterCollecting.purchase(blueprint.clay))
    } else {
        canSave = true
    }
    if (canBuy(blueprint.ore)
    ) {
        options.add(afterCollecting.purchase(blueprint.ore))
    } else if (oreCollectingRobots < max(
            blueprint.clay.ore,
            max(blueprint.obsidian.ore, blueprint.geode.ore)
        )
    ) {
        canSave = true
    }
    if (canSave) {
        options.add(afterCollecting)
    }
    return options
}

fun State.mustBuy(blueprint: Blueprint): Boolean {
    if (!canBuy(blueprint.ore) || !canBuy(blueprint.clay)) {
        return false
    }
    if (!canBuy(blueprint.obsidian) && clayCollectingRobots > 0) {
        return false
    }
    if (!canBuy(blueprint.geode) && obsidianCollectingRobots > 0) {
        return false
    }
    return true
}

fun State.canBuy(cost: Cost) = ore >= cost.ore && clay >= cost.clay && obsidian >= cost.obsidian

fun State.purchase(cost: Cost) = copy(
    ore = ore - cost.ore,
    clay = clay - cost.clay,
    obsidian = obsidian - cost.obsidian,

    oreCollectingRobots = oreCollectingRobots + cost.oreCollectingRobots,
    clayCollectingRobots = clayCollectingRobots + cost.clayCollectingRobots,
    obsidianCollectingRobots = obsidianCollectingRobots + cost.obsidianCollectingRobots,
    geodeCollectingRobots = geodeCollectingRobots + cost.geodeCollectingRobots,
)

val oreRegex = Regex("""Each ore robot costs (\d+) ore.""")
val clayRegex = Regex("""Each clay robot costs (\d+) ore.""")
val obsidianRegex = Regex("""Each obsidian robot costs (\d+) ore and (\d+) clay.""")
val geodeRegex = Regex("""Each geode robot costs (\d+) ore and (\d+) obsidian""")
fun String.toBlueprint() = Blueprint(
    ore = oreRegex.find(this)!!.destructured.let { (ore) ->
        Cost(
            ore = ore.toInt(),
            oreCollectingRobots = 1,
        )
    },
    clay = clayRegex.find(this)!!.destructured.let { (ore) ->
        Cost(
            ore = ore.toInt(),
            clayCollectingRobots = 1,
        )
    },
    obsidian = obsidianRegex.find(this)!!.destructured.let { (ore, clay) ->
        Cost(
            ore = ore.toInt(),
            clay = clay.toInt(),
            obsidianCollectingRobots = 1,
        )
    },
    geode = geodeRegex.find(this)!!.destructured.let { (ore, obsidian) ->
        Cost(
            ore = ore.toInt(),
            obsidian = obsidian.toInt(),
            geodeCollectingRobots = 1,
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

    val oreCollectingRobots: Int = 0,
    val clayCollectingRobots: Int = 0,
    val obsidianCollectingRobots: Int = 0,
    val geodeCollectingRobots: Int = 0,
)