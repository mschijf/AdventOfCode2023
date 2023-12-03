package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test) {

    private val symbols = inputLines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, ch ->  if (ch != '.' && !ch.isDigit()) pos(x,y) else null}
        }
        .filterNotNull()
        .toSet()

    private val numbers = inputLines()
        .flatMapIndexed { y, line ->
            line.findNumbers(y)
        }

    private val gearCandidates = inputLines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, ch ->  if (ch == '*') pos(x,y) else null}
        }
        .filterNotNull()
        .toSet()

    override fun resultPartOne(): Any {
        return numbers
            .filter { (number, posSet) -> posSet.hasNeighborWithSymbol() }
            .sumOf{(number, posSet) -> number}
    }

    override fun resultPartTwo(): Any {
        return gearCandidates
            .map{it.numberNeighbors()}
            .filter { it.count() == 2 }
            .sumOf{it.first().toLong() * it.last()}
    }

    private fun Point.numberNeighbors() =
        numbers
            .filter { (number, set) -> this.allWindDirectionNeighbors().intersect(set).isNotEmpty() }
            .map { (number, set) -> number }

    private fun Set<Point>.hasNeighborWithSymbol() =
        this.any {
            it.allWindDirectionNeighbors().any { nb -> nb in symbols }
        }

    private fun String.findNumbers(y: Int) : List<Pair<Int, Set<Point>>> {
        var started = false
        var number = 0
        var set = mutableSetOf<Point>()
        val result = mutableListOf<Pair<Int, Set<Point>>>()
        for (i in this.indices) {
            if (this[i].isDigit()) {
                started = true
                number = number*10 + this[i].digitToInt()
                set += pos(i, y)
            } else if (started) {
                started = false
                result += Pair(number, set)
                number = 0
                set = mutableSetOf<Point>()
            }
        }
        if (started) {
            result += Pair(number, set)
        }
        return result
    }
}


