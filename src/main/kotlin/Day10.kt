fun main() {
    fun part1(input: List<String>) =
        input.toInstructions()
            .fold(Signals()) { signals, processor ->
                signals.processInstruction(processor)
            }.toStrengths()
            .filter { (cycle) ->
                cycle in setOf(20, 60, 100, 140, 180, 220)
            }.sumOf { it.second }

    fun part2(input: List<String>) = 0


    runDay(
        day = 10,
        part1 = ::part1,
        part1Check = 13140,
        part2 = ::part2,
        part2Check = 36,
    )
}

private class Signals(val checkFrequency: Int = 20) {
    var lastSignal = Signal()
        private set

    var signals = listOf<Signal>()
        private set

    fun processInstruction(processor: InstructionProcessor): Signals {
        val nextSignal = processor.invoke(lastSignal)
        if (nextSignal.cycle / checkFrequency > lastSignal.cycle / checkFrequency) {
            signals += lastSignal
        }
        lastSignal = nextSignal
        return this
    }

    fun toStrengths() = signals.mapIndexed { i, signal ->
        val cycle = (i+1) * 20
        val strength = cycle * signal.x
        cycle to strength
    }
}

private data class Signal(val x: Int = 1, val cycle: Int = 0)

private typealias InstructionProcessor = (Signal) -> Signal

private val noop: InstructionProcessor = { (x, cycle) -> Signal(x, cycle + 1) }

private fun List<String>.toInstructions() =
    map {
        when (it) {
            "noop" -> noop
            else -> parseAdd(it)
        }
    }

private fun parseAdd(raw: String): InstructionProcessor =
    raw.split(' ', limit = 2)
        .last()
        .let {
            { (x, cycle) ->
                val addedX = it.toInt()
                Signal(x + addedX, cycle + 2)
            }
        }