package adventofcode2023

import com.tool.math.lcm
import tool.mylambdas.substringBetween

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private fun List<String>.inputToInstructions() = this.first().toList()
    private fun List<String>.inputToDesertMap() =
        this.drop(2).associate { line ->
            line.substringBefore(" ").trim() to
                    Pair(
                        line.substringBetween("= (", ", ").trim(),
                        line.substringBetween(", ", ")").trim()
                    )
        }

    private var instructions = emptyList<Char>()
    private var desertMap = emptyMap<String, Pair<String, String>>()

    override fun resultPartOne(): Any {
        //we have a different example in part 2, therefore we read the input in this method, instead of object variables
        instructions = inputLines.inputToInstructions()
        desertMap = inputLines.inputToDesertMap()

        return loopUntil("AAA") {step -> step == "ZZZ"}
    }

    override fun resultPartTwo(): Any {
        //start overriding input with special second example
        instructions = inputLines(testFile = "example2").inputToInstructions()
        desertMap = inputLines(testFile = "example2").inputToDesertMap()

        val start = desertMap.keys.filter { it.endsWith("A") }
        val countsPerPath = start.map{ loopUntil(it){ step -> step.endsWith("Z") } }
        return countsPerPath.fold(1L) { acc, i ->  lcm(acc, i.toLong()) }
    }

    private fun loopUntil(start: String, stopCondition: (String) -> Boolean): Int {
        var count=0
        var next = start
        while (!stopCondition(next)) {
            val instruction = instructions[count % instructions.size]
            next = if (instruction == 'L') {
                desertMap[next]!!.first
            } else {
                desertMap[next]!!.second
            }
            count++
        }
        return count
    }
}


