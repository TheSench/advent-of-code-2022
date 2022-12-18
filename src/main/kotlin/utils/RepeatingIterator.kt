package utils

class RepeatingIterator<T>(private val pattern: List<T>) : Iterator<T> {
    var current = 0
        private set

    val size: Int get() = pattern.size

    override fun hasNext() = true

    override fun next(): T {
        if (current >= pattern.size) {
            current = 0
        }
        return pattern[current++]
    }
}