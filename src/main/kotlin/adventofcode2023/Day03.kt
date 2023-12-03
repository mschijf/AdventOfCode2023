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
            .filter { number -> number.hasNeighborWithSymbol() }
            .sumOf{ number -> number.value }
    }

    override fun resultPartTwo(): Any {
        return gearCandidates
            .map{it.numberNeighbors()}
            .filter { it.count() == 2 }
            .sumOf{it.first().toLong() * it.last()}
    }

    private fun Point.numberNeighbors() =
        numbers
            .filter { number -> this.allWindDirectionNeighbors().intersect(number.posSet).isNotEmpty() }
            .map { number -> number.value }

    private fun Number.hasNeighborWithSymbol() =
        this.posSet.any {
            posSetPoint -> posSetPoint.allWindDirectionNeighbors().any { nb -> nb in symbols }
        }

    private fun String.findNumbers(y: Int) : List<Number> {
        var started = false
        var number = 0
        var set = mutableSetOf<Point>()
        val result = mutableListOf<Number>()
        for (i in this.indices) {
            if (this[i].isDigit()) {
                started = true
                number = number*10 + this[i].digitToInt()
                set += pos(i, y)
            } else if (started) {
                started = false
                result += Number(number, set)
                number = 0
                set = mutableSetOf()
            }
        }
        if (started) {
            result += Number(number, set)
        }
        return result
    }
}

data class Number(val value: Int, val posSet: Set<Point>)


