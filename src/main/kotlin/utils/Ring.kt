package utils

class RingNode<T> private constructor(val value: T) {
    lateinit var previous: RingNode<T>
    lateinit var next: RingNode<T>

    companion object {
        operator fun <T> invoke(vararg values: T): RingNode<T> {
            val nodes = values.map(::RingNode)
            val head = nodes.first()
            nodes.reduce { previous, next ->
                previous.next = next
                next.previous = previous
                next
            }
            val last = nodes.last()
            last.next = head
            head.previous = last
            return head
        }
    }

    fun find(value: T): RingNode<T> {
        return if (this.value == value) {
            this
        } else {
            next.find(value)
        }
    }

    operator fun iterator(): Iterator<T> {
        return RingIterator(this)
    }
}

class RingIterator<T>(private val start: RingNode<T>) : Iterator<T> {

    private var current = start
    private var first = true

    override fun hasNext(): Boolean {
        return current != start || first
    }

    override fun next(): T {
        first = false
        return current.value.also {
            current = current.next
        }
    }
}

typealias Ring<T> = RingNode<T>