package com.bpl.martianrobots

/** Immutable robot state. A lost robot keeps the last position it held on the grid. */
data class Robot(
    val position: Position,
    val orientation: Orientation,
    val lost: Boolean = false,
) {
    fun report(): String =
        "${position.x} ${position.y} ${orientation.name}" + if (lost) " LOST" else ""
}
