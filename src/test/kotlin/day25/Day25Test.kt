package day25

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day25Test {
    @ParameterizedTest
    @CsvSource(
        "1,             1",
        "1-,            4",
        "1-0,           20",
        "1-0---0,       12345",
        "1-12,          107",
        "10,            5",
        "11,            6",
        "111,           31",
        "112,           32",
        "1121-1110-1=0, 314159265",
        "12,            7",
        "12,            7",
        "12111,         906",
        "122,           37",
        "1=,            3",
        "1=,            3",
        "1=-0-2,        1747",
        "1=-1=,         353",
        "1=0,           15",
        "1=11-2,        2022",
        "2,             2",
        "2-,            9",
        "20,            10",
        "20012,         1257",
        "21,            11",
        "2=,            8",
        "2=01,          201",
        "2=0=,          198",
    )
    fun `SNAFU to base10`(snafu: String, base10: Long) {
        snafu.toBase10() shouldBe base10
    }

    @ParameterizedTest
    @CsvSource(
        "1=-0-2,        1747",
        "12111,         906",
        "2=0=,          198",
        "21,            11",
        "2=01,          201",
        "111,           31",
        "20012,         1257",
        "112,           32",
        "1=-1=,         353",
        "1-12,          107",
        "12,            7",
        "1=,            3",
        "122,           37",
        "1,             1",
        "2,             2",
        "1=,            3",
        "1-,            4",
        "10,            5",
        "11,            6",
        "12,            7",
        "2=,            8",
        "2-,            9",
        "20,            10",
        "1=0,           15",
        "1-0,           20",
        "1=11-2,        2022",
        "1-0---0,       12345",
        "1121-1110-1=0, 314159265",
    )
    fun `base10 to SNAFU`(snafu: String, base10: Long) {
        base10.toSnafu() shouldBe snafu
    }
}