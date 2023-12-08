package adventofcode2023

import com.tool.math.lcm
import tool.mylambdas.substringBetween

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val instructions = inputLines.first().toList()
    private val desertMap = inputLines.drop(2).map { line ->
            line.substringBefore(" ").trim() to
                Pair(
                    line.substringBetween("= (", ", ").trim(),
                    line.substringBetween(", ", ")").trim()
                )
    }.toMap()

    override fun resultPartOne(): Any {
        return loopUntil("AAA") {step -> step == "ZZZ"}
    }

    override fun resultPartTwo(): Any {
        val start = desertMap.keys.filter { it.endsWith("A") }
        val result = start.map{ loopUntil(it){ step -> step.endsWith("Z") } }
        return result.fold(1L) { acc, i ->  lcm(acc, i.toLong()) }
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


