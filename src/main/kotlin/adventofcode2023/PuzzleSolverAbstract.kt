package adventofcode2023

import java.io.File

abstract class PuzzleSolverAbstract (
    private val test: Boolean) {

    private val dayOfMonth = getDayOfMonthFromSubClassName()

    open fun resultPartOne(): Any = "NOT IMPLEMENTED"
    open fun resultPartTwo(): Any = "NOT IMPLEMENTED"

    fun showResult() {
        println("Day          : $dayOfMonth")
        println("Version      : ${if (test) "test" else "real"} input")
        println("---------------------------------")

        printResult(1) { resultPartOne().toString() }
        printResult(2) { resultPartTwo().toString() }
    }

    private fun printResult(puzzlePart: Int, getResult: () -> String ) {
        val startTime = System.currentTimeMillis()
        val result = getResult()
        val timePassed = System.currentTimeMillis() - startTime
        print("Result part $puzzlePart: $result (after %d.%03d sec)".format(timePassed / 1000, timePassed % 1000))
        println()
    }

    private fun getDayOfMonthFromSubClassName(): Int {
        val className = this.javaClass.name.lowercase()
        val dayOfMonth = if (className.contains("day")) {
            className.substringAfter("day").take(2)
        } else if (className.contains("december")) {
            className.substringAfter("december").take(2)
        } else {
            className.takeLast(2)
        }
        return dayOfMonth.toInt()
    }

    fun inputLines(testFile: String="example", liveFile: String="input", path:String = defaultPath()) =
        if (test) getInputLines(path, testFile) else getInputLines(path, liveFile)

    private fun defaultPath() = String.format("data/december%02d", dayOfMonth)

    private fun getInputLines(path: String, fileName: String): List<String> {
        val file = File("$path/$fileName")
        val inputLines = if (file.exists()) file.bufferedReader().readLines() else emptyList()
        if (inputLines.isEmpty())
            throw Exception("No input lines!!")
        return inputLines
    }
}