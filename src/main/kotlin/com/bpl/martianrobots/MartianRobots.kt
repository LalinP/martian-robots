package com.bpl.martianrobots

object MartianRobots {
    /** Runs every robot in order on a shared grid, so scents carry over to later robots. */
    fun run(mission: Mission): List<Robot> =
        mission.robots.map { (robot, commands) -> drive(robot, commands, mission.grid) }

    fun run(input: String): String =
        run(InputParser.parse(input)).joinToString("\n") { it.report() }

    private fun drive(start: Robot, commands: List<Command>, grid: Grid): Robot {
        var robot = start
        for (command in commands) {
            robot = command.execute(robot, grid)
            if (robot.lost) break
        }
        return robot
    }
}
