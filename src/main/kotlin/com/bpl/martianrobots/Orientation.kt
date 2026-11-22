package com.bpl.martianrobots

/** A compass heading. The order of entries is clockwise, which [left], [right] and [opposite] rely on. */
enum class Orientation(val dx: Int, val dy: Int) {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    fun left(): Orientation = entries[(ordinal + entries.size - 1) % entries.size]

    fun right(): Orientation = entries[(ordinal + 1) % entries.size]

    /** Two steps clockwise is the reverse heading: N <-> S, E <-> W. */
    fun opposite(): Orientation = entries[(ordinal + 2) % entries.size]

    companion object {
        fun fromSymbol(symbol: String): Orientation =
            entries.firstOrNull { it.name == symbol }
                ?: throw IllegalArgumentException("Unknown orientation '$symbol' (expected one of N, E, S, W)")
    }
}
