package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point

fun main() {
    Day23(test=false).showResult()
}

class Day23(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val totalMap = inputAsGrid()
    private val trail = totalMap.filterValues { it in ".<>^v" }.keys
    private val start = trail.first{it.y==0}
    private val end = trail.maxBy{it.y}

    override fun resultPartOne(): Any {
        return findLongest(start)
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun findLongest(current: Point, onPath: Set<Point> = emptySet()): Int {
        if (current == end) {
            return 0
        }

        var max = -1
        current.nextMoves().filter{it !in onPath}.forEach { nextStep->
            val lengthPath = 1+findLongest(nextStep, onPath+current)
            if (lengthPath > max) {
                max = lengthPath
            }
        }
        return max
    }

    private fun Point.nextMoves(): List<Point> {
        return when (totalMap[this]!!) {
            '>' -> listOf(this.moveOneStep(Direction.RIGHT))
            '<' -> listOf(this.moveOneStep(Direction.LEFT))
            '^' -> listOf(this.moveOneStep(Direction.UP))
            'v' -> listOf(this.moveOneStep(Direction.DOWN))
            '.' -> this.neighbors()
            else -> throw Exception("Unknown Trail Thing")
        }.filter { it in trail }
    }
}


