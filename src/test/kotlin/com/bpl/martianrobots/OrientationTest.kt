package com.bpl.martianrobots

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrientationTest {
    @Test
    fun `turning left cycles anticlockwise`() {
        assertEquals(listOf(Orientation.W, Orientation.S, Orientation.E, Orientation.N),
            generateSequence(Orientation.N.left()) { it.left() }.take(4).toList())
    }

    @Test
    fun `turning right cycles clockwise`() {
        assertEquals(listOf(Orientation.E, Orientation.S, Orientation.W, Orientation.N),
            generateSequence(Orientation.N.right()) { it.right() }.take(4).toList())
    }

    @Test
    fun `unknown symbol is rejected`() {
        assertFailsWith<IllegalArgumentException> { Orientation.fromSymbol("X") }
    }
}
