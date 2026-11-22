package com.bpl.martianrobots

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InputParserTest {
    @Test
    fun `parses grid, robots and instructions`() {
        val mission = InputParser.parse("5 3\n1 1 E\nRFL\n\n3 2 N\nF\n")

        assertEquals(5, mission.grid.maxX)
        assertEquals(3, mission.grid.maxY)
        assertEquals(
            listOf(Robot(Position(1, 1), Orientation.E), Robot(Position(3, 2), Orientation.N)),
            mission.robots.map { it.robot },
        )
        assertEquals(listOf(TurnRight, MoveForward, TurnLeft), mission.robots[0].commands)
    }

    @Test
    fun `accepts a grid with no robots`() {
        assertEquals(0, InputParser.parse("5 3").robots.size)
    }

    @Test
    fun `rejects invalid input with the offending line number`() {
        val cases = mapOf(
            "" to "empty",
            "5" to "Line 1",
            "51 3" to "Line 1",
            "5 3\n1 1 E" to "Line 2",
            "5 3\n1 1" to "Line 2",
            "5 3\n6 1 E\nF" to "Line 2",
            "5 3\n1 1 Q\nF" to "Line 2",
            "5 3\n1 1 E\nFX" to "Line 3",
            "5 3\n1 1 E\n" + "F".repeat(100) to "Line 3",
        )
        cases.forEach { (input, expected) ->
            val error = assertFailsWith<IllegalArgumentException>("input: $input") { InputParser.parse(input) }
            assertContains(error.message.orEmpty(), expected)
        }
    }

    @Test
    fun `accepts instructions up to 99 characters`() {
        assertEquals(99, InputParser.parse("5 3\n1 1 E\n" + "L".repeat(99)).robots[0].commands.size)
    }
}
