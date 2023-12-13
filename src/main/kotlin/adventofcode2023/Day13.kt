package adventofcode2023

import tool.mylambdas.splitByCondition

fun main() {
    Day13(test=false).showResult()
}

class Day13(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val patterns = inputLines.splitByCondition { it.isEmpty() }.map{Pattern.of(it)}
    override fun resultPartOne(): Any {
        return patterns.map{100*it.findMirrorHorizontal() + it.findMirrorVertical() }.sum()
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }
}

data class Pattern(val grid: List<List<Char>>) {
    companion object {
        fun of (raw: List<String>): Pattern {
            val xx = raw.mapIndexed { y, line ->
                line.mapIndexed { x, ch ->  ch}
            }
            return Pattern(xx)
        }
    }

    fun findMirrorVertical() : Int {
        val max = grid[0].size-1
        for (reflectionLine in 1..max) {
            if (grid.indices.all { y -> isReflectedHorizontal(y, reflectionLine, max)} ) {
                return reflectionLine
            }
        }
        return 0
    }

    private fun isReflectedHorizontal(y: Int, reflectionPoint: Int, max: Int) : Boolean {
        for (i in reflectionPoint-1 downTo 0) {
            val otherSide = reflectionPoint + (reflectionPoint-i)-1
            if (otherSide > max)
                return true
            if (grid[y][i] != grid[y][otherSide]) {
                return false
            }
        }
        return true
    }

    fun findMirrorHorizontal() : Int {
        val max = grid.size-1
        for (reflectionLine in 1..max) {
            if (grid[0].indices.all { y -> isReflectedVertical(y, reflectionLine, max)} ) {
                return reflectionLine
            }
        }
        return 0
    }

    private fun isReflectedVertical(x: Int, reflectionPoint: Int, max: Int) : Boolean {
        for (i in reflectionPoint-1 downTo 0) {
            val otherSide = reflectionPoint + (reflectionPoint-i)-1
            if (otherSide > max)
                return true
            if (grid[i][x] != grid[otherSide][x]) {
                return false
            }
        }
        return true
    }

}


