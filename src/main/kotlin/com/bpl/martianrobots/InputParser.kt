package com.bpl.martianrobots

/**
 * A robot representation with its initial position and instructions.
 */
data class RobotMission(val robot: Robot, val commands: List<Command>)

/**
 * Immutable container for the shared Grid and the ordered list of robot missions to execute on it.
 */
data class Mission(val grid: Grid, val robots: List<RobotMission>)

/**
 * Parses mission input:
 * ```
 * 5 3          <- upper-right grid coordinates(0,0 is lower left)
 * 1 1 E        <- robot start position and orientation
 * RFRFRFRF     <- robot instructions
 * ```
 * Robot position/instruction pairs repeat. Blank lines are ignored.
 */
object InputParser {
    const val MAX_INSTRUCTION_LENGTH = 99

    fun parse(input: String): Mission {
        val lines = input.lines()
            .mapIndexed { index, line -> index + 1 to line.trim() }
            .filter { (_, line) -> line.isNotEmpty() }

        require(lines.isNotEmpty()) { "Input is empty: expected the grid size on the first line" }

        val grid = parseGrid(lines.first())
        val robotLines = lines.drop(1)
        require(robotLines.size % 2 == 0) {
            "Line ${robotLines.last().first}: robot position has no instruction line"
        }

        val robots = robotLines.chunked(2).map { (positionLine, instructionLine) ->
            RobotMission(parseRobot(positionLine, grid), parseInstructions(instructionLine))
        }
        return Mission(grid, robots)
    }

    private fun parseGrid(line: Pair<Int, String>): Grid {
        val (lineNo, text) = line
        val parts = text.split(WHITESPACE)
        val coords = parts.map { it.toIntOrNull() }
        require(parts.size == 2 && coords.all { it != null }) {
            "Line $lineNo: expected grid size as 'X Y', got '$text'"
        }
        return try {
            Grid(coords[0]!!, coords[1]!!)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Line $lineNo: ${e.message}", e)
        }
    }

    private fun parseRobot(line: Pair<Int, String>, grid: Grid): Robot {
        val (lineNo, text) = line
        val parts = text.split(WHITESPACE)
        val x = parts.getOrNull(0)?.toIntOrNull()
        val y = parts.getOrNull(1)?.toIntOrNull()
        require(parts.size == 3 && x != null && y != null) {
            "Line $lineNo: expected robot position as 'X Y O', got '$text'"
        }
        val position = Position(x, y)
        require(position in grid) {
            "Line $lineNo: robot start position $x $y is outside the grid (0 0 to ${grid.maxX} ${grid.maxY})"
        }
        val orientation = try {
            Orientation.fromSymbol(parts[2])
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Line $lineNo: ${e.message}", e)
        }
        return Robot(position, orientation)
    }

    private fun parseInstructions(line: Pair<Int, String>): List<Command> {
        val (lineNo, text) = line
        require(text.length <= MAX_INSTRUCTION_LENGTH) {
            "Line $lineNo: instructions must be fewer than ${MAX_INSTRUCTION_LENGTH + 1} characters, got ${text.length}"
        }
        return text.map {
            try {
                Commands.fromChar(it)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Line $lineNo: ${e.message}", e)
            }
        }
    }

    private val WHITESPACE = Regex("\\s+")
}
