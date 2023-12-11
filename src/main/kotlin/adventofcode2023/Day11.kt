package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.mylambdas.collectioncombination.allOfCombinedItems
import tool.mylambdas.collectioncombination.mapCombinedItems
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day11(test=false).showResult()
}

class Day11(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val galaxyMap = inputAsGrid().filter { it.value != '.' }.keys
    private val emptyVerticalLines = galaxyMap.emptyVerticalLines()
    private val emptyHorizontalLines = galaxyMap.emptyHorizontalLines()

    override fun resultPartOne(): Any {
        return galaxyMap.toList().mapCombinedItems { point, point2 ->  point.galaxyDistanceTo(point2)}.sum()
    }

    override fun resultPartTwo(): Any {
        return galaxyMap.toList().mapCombinedItems { point, point2 ->  point.galaxyDistanceToAfterManyYears(point2)}.sum()
    }

    private fun Point.galaxyDistanceTo(other: Point) : Int {
        val minX = min(this.x, other.x)
        val maxX = max(this.x, other.x)
        val minY = min(this.y, other.y)
        val maxY = max(this.y, other.y)

        val emptyVerticalLinesBetween = emptyVerticalLines.count{ it in minX..maxX}
        val emptyHorizontalLinesBetween = emptyHorizontalLines.count{ it in minY..maxY}
        return this.distanceTo(other) + emptyHorizontalLinesBetween + emptyVerticalLinesBetween
    }

    private val factor = if (test) 100 else 1_000_000
    private fun Point.galaxyDistanceToAfterManyYears(other: Point) : Long {
        val minX = min(this.x, other.x)
        val maxX = max(this.x, other.x)
        val minY = min(this.y, other.y)
        val maxY = max(this.y, other.y)

        val emptyVerticalLinesBetween = emptyVerticalLines.count{ it in minX..maxX}
        val emptyHorizontalLinesBetween = emptyHorizontalLines.count{ it in minY..maxY}
        return this.distanceTo(other) + (factor-1).toLong()*(emptyHorizontalLinesBetween + emptyVerticalLinesBetween)
    }



    private fun Set<Point>.emptyVerticalLines(): List<Int> {
        val result = mutableListOf<Int>()
        val minX = this.minOf { it.x }
        val maxX = this.maxOf { it.x }
        for (x in minX .. maxX) {
            if (this.isEmptyVerticalLine(x)) {
                result += x
            }
        }
        return result
    }

    private fun Set<Point>.emptyHorizontalLines(): List<Int> {
        val result = mutableListOf<Int>()
        val minY = this.minOf { it.y }
        val maxY = this.maxOf { it.y }
        for (y in minY .. maxY) {
            if (this.isEmptyHorizontalLine(y)) {
                result += y
            }
        }
        return result
    }


    private fun Set<Point>.isEmptyVerticalLine(x: Int): Boolean {
        return this.none{it.x == x}
    }
    private fun Set<Point>.isEmptyHorizontalLine(y: Int): Boolean {
        return this.none{it.y == y}
    }
}


