package com.bpl.martianrobots

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandTest {
    private val grid = Grid(5, 3)

    @Test
    fun `turns change orientation but not position`() {
        val robot = Robot(Position(1, 1), Orientation.N)
        assertEquals(Robot(Position(1, 1), Orientation.W), TurnLeft.execute(robot, grid))
        assertEquals(Robot(Position(1, 1), Orientation.E), TurnRight.execute(robot, grid))
    }

    @Test
    fun `forward moves one point in the current direction`() {
        val cases = mapOf(
            Orientation.N to Position(2, 2),
            Orientation.E to Position(3, 1),
            Orientation.S to Position(2, 0),
            Orientation.W to Position(1, 1),
        )
        cases.forEach { (orientation, expected) ->
            assertEquals(expected, MoveForward.execute(Robot(Position(2, 1), orientation), grid).position)
        }
    }

    @Test
    fun `moving off the grid loses the robot at its last position and leaves a scent`() {
        val robot = MoveForward.execute(Robot(Position(5, 3), Orientation.N), grid)
        assertEquals(Robot(Position(5, 3), Orientation.N, lost = true), robot)
        assertTrue(grid.hasScent(Position(5, 3)))
    }

    @Test
    fun `a scent stops later robots falling off from the same point`() {
        MoveForward.execute(Robot(Position(0, 0), Orientation.S), grid)

        val survivor = MoveForward.execute(Robot(Position(0, 0), Orientation.W), grid)
        assertEquals(Robot(Position(0, 0), Orientation.W), survivor)
        assertFalse(survivor.lost)
    }

    @Test
    fun `back moves one point opposite to the current direction without turning`() {
        val cases = mapOf(
            Orientation.N to Position(2, 0),
            Orientation.E to Position(1, 1),
            Orientation.S to Position(2, 2),
            Orientation.W to Position(3, 1),
        )
        cases.forEach { (orientation, expected) ->
            assertEquals(Robot(expected, orientation), TurnBack.execute(Robot(Position(2, 1), orientation), grid))
        }
    }

    @Test
    fun `moving back off the grid loses the robot at its last position and leaves a scent`() {
        val robot = TurnBack.execute(Robot(Position(0, 0), Orientation.N), grid)
        assertEquals(Robot(Position(0, 0), Orientation.N, lost = true), robot)
        assertTrue(grid.hasScent(Position(0, 0)))
    }

    @Test
    fun `a scent stops a later robot falling off backwards from the same point`() {
        TurnBack.execute(Robot(Position(5, 3), Orientation.S), grid)

        val survivor = TurnBack.execute(Robot(Position(5, 3), Orientation.W), grid)
        assertEquals(Robot(Position(5, 3), Orientation.W), survivor)
    }

    @Test
    fun `a scent left by moving forward also protects a robot moving back`() {
        MoveForward.execute(Robot(Position(0, 3), Orientation.N), grid)

        val survivor = TurnBack.execute(Robot(Position(0, 3), Orientation.S), grid)
        assertEquals(Robot(Position(0, 3), Orientation.S), survivor)
    }

    @Test
    fun `B is parsed as the back command`() {
        assertEquals(TurnBack, Commands.fromChar('B'))
    }

    @Test
    fun `unknown instruction is rejected`() {
        assertFailsWith<IllegalArgumentException> { Commands.fromChar('X') }
    }
}
