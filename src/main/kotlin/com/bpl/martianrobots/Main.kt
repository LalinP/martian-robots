package com.bpl.martianrobots

import java.io.File
import kotlin.system.exitProcess

/** Reads mission input from the file given as the first argument, or from standard input. */
fun main(args: Array<String>) {
    try {
        val input = when (args.size) {
            0 -> generateSequence(::readLine).joinToString("\n")
            1 -> File(args[0]).readText()
            else -> throw IllegalArgumentException("Usage: martian-robots [input-file]")
        }
        println(MartianRobots.run(input))
    } catch (e: Exception) {
        System.err.println("Error: ${e.message}")
        exitProcess(1)
    }
}
