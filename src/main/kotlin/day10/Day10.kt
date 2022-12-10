package day10

import runDay

fun main() {
    fun part1(input: List<String>): Int {
        val counter = Counter(20)
        input.toInstructions()
            .fold(Signals(counter::track)) { signals, processor ->
                signals.processInstruction(processor)
            }
        return counter.toStrengths()
            .filter { (cycle) ->
                cycle in setOf(20, 60, 100, 140, 180, 220)
            }.sumOf { it.second }
    }

    fun part2(input: List<String>): String {
        val crt = CRT()
        input.toInstructions()
            .fold(Signals(crt::print)) { signals, processor ->
                signals.processInstruction(processor)
            }
        return "$crt"
    }

    runDay(
        day = 10,
        part1 = ::part1,
        part1Check = 13140,
        part2 = ::part2,
        part2Check = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent(),
    )
}

private class Signals(private val signalProcessor: (Signal, Signal) -> Unit) {
    var lastSignal = Signal()
        private set

    fun processInstruction(processor: InstructionProcessor): Signals {
        val nextSignal = processor.invoke(lastSignal)
        signalProcessor(lastSignal, nextSignal)
        lastSignal = nextSignal
        return this
    }
}

private class Counter(val checkFrequency: Int = 20) {
    var signals = listOf<Signal>()
        private set

    fun track(lastSignal: Signal, nextSignal: Signal) {
        if (nextSignal.cycle / checkFrequency > lastSignal.cycle / checkFrequency) {
            signals += lastSignal
        }
    }

    fun toStrengths() = signals.mapIndexed { i, signal ->
        val cycle = (i + 1) * 20
        val strength = cycle * signal.x
        cycle to strength
    }
}

private data class Cursor(val left: Int = 0, val row: Int = 0) {
    fun move() = left.let {
        if (it < 39) {
            Cursor(left + 1, row)
        } else {
            Cursor(0, row + 1)
        }
    }
}

private class CRT {
    val screen = MutableList(6) {
        MutableList(40) { '.' }
    }

    var cursor = Cursor()

    fun print(lastSignal: Signal, nextSignal: Signal) {
        repeat(nextSignal.cycle - lastSignal.cycle) {
            lastSignal.x.let { x ->
                screen[cursor.row][cursor.left] = if (cursor.left in (x - 1..x + 1)) {
                    '#'
                } else {
                    '.'
                }
            }
            cursor = cursor.move()
        }
    }

    override fun toString() = screen.joinToString("\n") {
        it.joinToString("")
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
