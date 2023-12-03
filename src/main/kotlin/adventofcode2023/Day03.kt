package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test) {

    private val grid = inputLines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, ch ->  pos(x,y) to ch}
        }
        .filter{(pos, ch) -> ch != '.'}
        .toMap()

    private val symbols = inputLines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, ch ->  pos(x,y) to ch}
        }
        .filter{(pos, ch) -> ch != '.' && !ch.isDigit()}
        .map{it.first}
        .toSet()

    private val numbers = inputLines()
        .mapIndexed { y, line ->
            line.findNumbers(y)
        }
        .flatten()

    private val gearCandidates = inputLines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, ch ->  pos(x,y) to ch}
        }
        .filter{(pos, ch) -> ch == '*'}
        .map{it.first}
        .toSet()

    override fun resultPartOne(): Any {
        return numbers.filter { (number, posSet) -> posSet.hasNeighborWithSymbol() }.sumOf{(number, posSet) -> number}
    }

    override fun resultPartTwo(): Any {
        return gearCandidates.map{it.countNumberNeighbors()}.filter { it.count() == 2 }.map{it.first().toLong() * it.last()}.sum()
    }

    private fun Point.countNumberNeighbors(): List<Int> {
        var count = 0
        val result = mutableListOf<Int>()
        numbers.forEach { (number, set) ->
            if (this.allWindDirectionNeighbors().intersect(set).isNotEmpty()) {
                result += number
            }
        }
        return result
    }

    private fun Set<Point>.hasNeighborWithSymbol(): Boolean {
        return this.any { it.allWindDirectionNeighbors().any { nb -> nb in symbols } }
    }

    private fun String.findNumbers(y: Int) : List<Pair<Int, Set<Point>>> {
        var started = false
        var number = 0
        var set = mutableSetOf<Point>()
        var result = mutableListOf<Pair<Int, Set<Point>>>()
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


