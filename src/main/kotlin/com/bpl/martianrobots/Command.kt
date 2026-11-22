package com.bpl.martianrobots

/**
 * A single robot instruction. New command types can be added by implementing
 * this interface and registering them in [Commands].
 */
fun interface Command {
    fun execute(robot: Robot, grid: Grid): Robot
}

object TurnLeft : Command {
    override fun execute(robot: Robot, grid: Grid) = robot.copy(orientation = robot.orientation.left())
}

object TurnRight : Command {
    override fun execute(robot: Robot, grid: Grid) = robot.copy(orientation = robot.orientation.right())
}

object MoveForward : Command {
    override fun execute(robot: Robot, grid: Grid) = move(robot, robot.orientation, grid)
}

// Move forward 2 points, applying the off-grid check to each step
object Jump : Command {
    private const val JUMPS = 2
    override fun execute(robot: Robot, grid: Grid): Robot {
        var current = robot
       repeat(JUMPS) {
           if (current.lost) return current
           current = move(current, robot.orientation, grid)
        }
        return current
    }

}

/** Steps one point backwards (opposite to the heading) without turning. */
object TurnBack : Command {
    override fun execute(robot: Robot, grid: Grid) = move(robot, robot.orientation.opposite(), grid)
}

/**
 * Moves [robot] one point in [direction], keeping its orientation. Stepping off the grid
 * loses the robot and leaves a scent at its current point, unless that point already has one,
 * in which case the move is ignored.
 */
private fun move(robot: Robot, direction: Orientation, grid: Grid): Robot {
    val pos = robot.position.step(direction)
    return when {
        pos in grid -> robot.copy(position = pos)
        grid.hasScent(robot.position) -> robot
        else -> {
            grid.leaveScent(robot.position)
            robot.copy(lost = true)
        }
    }
}

object Commands {
    private val registry: Map<Char, Command> = mapOf(
        'L' to TurnLeft,
        'R' to TurnRight,
        'F' to MoveForward,
        'B' to TurnBack,
        'J' to Jump
    )

    fun fromChar(symbol: Char): Command =
        registry[symbol]
            ?: throw IllegalArgumentException("Unknown instruction '$symbol' (expected one of ${registry.keys.joinToString()})")
}
