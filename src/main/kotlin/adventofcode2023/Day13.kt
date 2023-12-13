package adventofcode2023

import tool.mylambdas.splitByCondition

fun main() {
    Day13(test=false).showResult()
}

class Day13(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val patterns = inputLines.splitByCondition { it.isEmpty() }.map{Pattern.of(it)}
    override fun resultPartOne(): Any {
//        return patterns.map{ 100*it.findMirrorHorizontal() + it.findMirrorVertical() }.sum()
        return patterns.map{ it.findMirrorValue() }.sum()
    }

    override fun resultPartTwo(): Any {
        return patterns.map{it.doeIets()}.sum()
    }
}

data class Pattern(val grid: List<MutableList<Char>>) {
    companion object {
        fun of (raw: List<String>): Pattern {
            val xx = raw.mapIndexed { y, line ->
                line.mapIndexed { x, ch ->  ch}.toMutableList()
            }
            return Pattern(xx)
        }
    }

    fun findMirrorVertical(excludeLine: Int=-1) : Int {
        val max = grid[0].size-1
        for (reflectionLine in 1..max) {
            if (reflectionLine != excludeLine) {
                if (grid.indices.all { y -> isReflectedHorizontal(y, reflectionLine, max) }) {
                    return reflectionLine
                }
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

    fun findMirrorHorizontal(excludeLine: Int = -1) : Int {
        val max = grid.size-1
        for (reflectionLine in 1..max) {
            if (reflectionLine != excludeLine) {
                if (grid[0].indices.all { y -> isReflectedVertical(y, reflectionLine, max) }) {
                    return reflectionLine
                }
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

    fun findMirrorValue(): Int {
        val x = findMirrorVertical()
        val y = findMirrorHorizontal()

        if (x > 0)
            return x

        if (y > 0)
            return 100*y

        return 0
    }

    fun findNewMirrorValue(oldValue: Int): Int {
        val x = findMirrorVertical(excludeLine = oldValue)
        val y = findMirrorHorizontal(excludeLine = oldValue / 100)

        if (x > 0)
            return x

        if (y > 0)
            return 100*y

        return 0
    }


    fun doeIets(): Int {
        val org = findMirrorValue()
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                val ch = grid[y][x]

                //swap
                grid[y][x] = if (ch == '.') '#' else '.'
                val new = findNewMirrorValue(org)
                if (org != new && new != 0)
                    return new

                //swap back
                grid[y][x] = ch
            }
        }
        return 0
    }

    //different Line!



}


