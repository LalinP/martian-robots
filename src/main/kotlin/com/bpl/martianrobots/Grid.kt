package com.bpl.martianrobots

/**
 * The rectangular surface of Mars, from (0, 0) to ([maxX], [maxY]) inclusive.
 *
 * Tracks "scents": grid points from which a robot has fallen off. A robot on a
 * scented point ignores any instruction that would move it off the grid.
 */
class Grid(val maxX: Int, val maxY: Int) {

    private val scents = mutableSetOf<Position>()

    init {
        require(maxX in 0..MAX_COORDINATE && maxY in 0..MAX_COORDINATE) {
            "Grid coordinates must be between 0 and $MAX_COORDINATE, got $maxX $maxY"
        }
    }

    operator fun contains(position: Position): Boolean =
        position.x in 0..maxX && position.y in 0..maxY

    fun hasScent(position: Position): Boolean = position in scents

    fun leaveScent(position: Position) {
        scents += position
    }

    companion object {
        const val MAX_COORDINATE = 50
    }
}
