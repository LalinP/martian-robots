package com.bpl.martianrobots

data class Position(val x: Int, val y: Int) {
    fun step(orientation: Orientation): Position = Position(x + orientation.dx, y + orientation.dy)
}
