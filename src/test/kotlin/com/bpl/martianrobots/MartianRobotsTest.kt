package com.bpl.martianrobots

import kotlin.test.Test
import kotlin.test.assertEquals

class MartianRobotsTest {
    @Test
    fun `sample input produces the expected output`() {
        val input = """
            5 3
            1 1 E
            RFRFRFRF

            3 2 N
            FRRFLLFFRRFLL

            0 3 W
            LLFFFLFLFL
        """.trimIndent()

        val expected = """
            1 1 E
            3 3 N LOST
            2 3 S
        """.trimIndent()

        assertEquals(expected, MartianRobots.run(input))
    }

    @Test
    fun `a lost robot ignores its remaining instructions`() {
        assertEquals("0 0 S LOST", MartianRobots.run("1 1\n0 0 S\nFLLFF"))
    }

    @Test
    fun `scents only protect robots that run later`() {
        val output = MartianRobots.run("1 1\n1 1 N\nF\n1 1 E\nFL\n0 0 W\nF")
        assertEquals("1 1 N LOST\n1 1 N\n0 0 W LOST", output)
    }

    @Test
    fun `back instructions move the robot backwards and can lose it`() {
        // 1 1 N -> B to 1 0 -> B off the bottom edge: lost at 1 0, scent left.
        // Second robot reverses onto the scented point and is protected, then drives forward.
        val output = MartianRobots.run("5 3\n1 1 N\nBBF\n1 1 N\nBBBRF")
        assertEquals("1 0 N LOST\n2 0 E", output)
    }
}
