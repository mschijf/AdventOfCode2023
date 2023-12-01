package adventofcode2023

import java.io.File

abstract class PuzzleSolverAbstract (
    val test: Boolean) {

    private val dayOfMonth = getDayOfMonthFromSubClassName()

    private val fileName = if (test) "example" else "input"
    private val path = String.format("data/december%02d", dayOfMonth)
    val inputLines = getInputLines(path, fileName)

    open fun resultPartOne(): Any = "NOT IMPLEMENTED"
    open fun resultPartTwo(): Any = "NOT IMPLEMENTED"

    fun showResult() {


        println("Day          : $dayOfMonth")
        println("Version      : ${if (test) "test" else "real"} input")
        println("Input lines  : ${if (inputLines.isEmpty()) "NO INPUT!!" else inputLines.count()} ")
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

    fun inputLinesAlternative(testFile: String, liveFile: String) =
        if (test) getInputLines(path, testFile) else getInputLines(path, liveFile)

    private fun getInputLines(path: String, fileName: String): List<String> {
        val file = File("$path/$fileName")
        return if (file.exists()) file.bufferedReader().readLines() else emptyList()
    }

}