package com.bpl.martianrobots

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JumpTest {
    private val grid = Grid(5, 3)

    @Test
    fun `jump moves two points in the current direction without turning`() {
        val cases = mapOf(
            Orientation.N to Position(2, 3),
            Orientation.E to Position(4, 1),
            Orientation.W to Position(0, 1),
        )
        cases.forEach { (orientation, expected) ->
            assertEquals(Robot(expected, orientation), Jump.execute(Robot(Position(2, 1), orientation), grid))
        }
        assertEquals(Robot(Position(2, 0), Orientation.S), Jump.execute(Robot(Position(2, 2), Orientation.S), grid))
    }

    @Test
    fun `jump landing exactly on the edge is not lost`() {
        val robot = Jump.execute(Robot(Position(3, 1), Orientation.E), grid)
        assertEquals(Robot(Position(5, 1), Orientation.E), robot)
        assertFalse(grid.hasScent(Position(5, 1)))
    }

    @Test
    fun `falling off on the second step loses the robot at the middle point`() {
        val robot = Jump.execute(Robot(Position(4, 1), Orientation.E), grid)
        assertEquals(Robot(Position(5, 1), Orientation.E, lost = true), robot)
        assertTrue(grid.hasScent(Position(5, 1)))
        assertFalse(grid.hasScent(Position(4, 1)))
    }

    @Test
    fun `falling off on the first step loses the robot at its start point`() {
        val robot = Jump.execute(Robot(Position(5, 1), Orientation.E), grid)
        assertEquals(Robot(Position(5, 1), Orientation.E, lost = true), robot)
        assertTrue(grid.hasScent(Position(5, 1)))
    }

    @Test
    fun `a scent on the middle point stops the second step`() {
        grid.leaveScent(Position(5, 1))

        val robot = Jump.execute(Robot(Position(4, 1), Orientation.E), grid)
        assertEquals(Robot(Position(5, 1), Orientation.E), robot)
    }

    @Test
    fun `a scent on the start point ignores both steps off the grid`() {
        grid.leaveScent(Position(5, 1))

        val robot = Jump.execute(Robot(Position(5, 1), Orientation.E), grid)
        assertEquals(Robot(Position(5, 1), Orientation.E), robot)
    }

    @Test
    fun `a scent on the start point does not block a jump that stays on the grid`() {
        grid.leaveScent(Position(5, 1))

        val robot = Jump.execute(Robot(Position(5, 1), Orientation.W), grid)
        assertEquals(Robot(Position(3, 1), Orientation.W), robot)
    }

    @Test
    fun `a jump on a single-point grid is lost immediately`() {
        val tiny = Grid(0, 0)
        val robot = Jump.execute(Robot(Position(0, 0), Orientation.N), tiny)
        assertEquals(Robot(Position(0, 0), Orientation.N, lost = true), robot)
        assertTrue(tiny.hasScent(Position(0, 0)))
    }

    @Test
    fun `J is parsed as the jump command`() {
        assertEquals(Jump, Commands.fromChar('J'))
    }

    @Test
    fun `jump works end to end with other instructions and shared scents`() {
        // Robot 1: J to 3 1, J off the east edge from 5 1 -> lost, remaining F ignored.
        // Robot 2: same route, saved by the scent at 5 1, then turns and jumps north to 5 3.
        // Robot 3: F onto the scented point 5 1, then J east is ignored.
        val output = MartianRobots.run("5 3\n1 1 E\nJJF\n1 1 E\nJJLJ\n4 1 E\nFJ")
        assertEquals("5 1 E LOST\n5 3 N\n5 1 E", output)
    }
}
