package day25

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day25Test {
    @ParameterizedTest
    @CsvSource(
        "1=-0-2, 1747",
        "12111,  906",
        "2=0=,   198",
        "21,     11",
        "2=01,   201",
        "111,    31",
        "20012,  1257",
        "112,    32",
        "1=-1=,  353",
        "1-12,   107",
        "12,     7",
        "1=,     3",
        "122,    37",
    )
    fun `SNAFU to base10`(snafu: String, base10: Int) {
        snafu.toBase10() shouldBe base10
    }

    @ParameterizedTest
    @CsvSource(
        "1=-0-2, 1747",
        "12111,  906",
        "2=0=,   198",
        "21,     11",
        "2=01,   201",
        "111,    31",
        "20012,  1257",
        "112,    32",
        "1=-1=,  353",
        "1-12,   107",
        "12,     7",
        "1=,     3",
        "122,    37",
    )
    fun `base10 to SNAFU`(snafu: String, base10: Int) {
        base10.toSnafu() shouldBe snafu
    }
}