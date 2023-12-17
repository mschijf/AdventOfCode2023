package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import java.util.*

fun main() {
    Day17(test=false).showResult()
}

class Day17(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val heatMap = inputAsGrid().mapValues { it.value.digitToInt() }
    private val maxX = heatMap.keys.maxOf {it.x}
    private val maxY = heatMap.keys.maxOf {it.y}
    private val start = pos(0,0)
    private val end = pos(maxX, maxY)

    override fun resultPartOne(): Any {

        return findMinimalHeatPath(start, end)
    }

    override fun resultPartTwo(): Any {
        return findMinimalHeatPathPart2(start, end)
    }

    private fun findMinimalHeatPath(start: Point, end: Point): Int {
        val heatSumMap = mutableMapOf<PointDirection, Int>()

        val compareByHeatTaken: Comparator<Pair<PointDirection, Int>> = compareBy{ it.second }
        val queue = PriorityQueue(compareByHeatTaken).apply { this.add(Pair(PointDirection(start, Direction.RIGHT, 0), 0)) }

        while (queue.isNotEmpty()) {
            val (currentPointDir, currentValue) = queue.remove()

            if (currentPointDir.point == end) {
                return currentValue
            }

            currentPointDir.nextStepsPlusValues().forEach { pd ->
                val newValue = currentValue + heatMap[pd.point]!!
                if (heatSumMap.getOrDefault(pd, 999_999_999) > newValue) {
                    heatSumMap[pd] = newValue
                    queue.add(Pair(pd, newValue))
                }
            }
        }
        return -1
    }

    private fun PointDirection.nextStepsPlusValues(): List<PointDirection> {
        val goLeft = this.direction.rotateLeft()
        val goRight = this.direction.rotateRight()

        return if (this.stepsDone < 3) {
            listOf(
                this.moveStep(this.direction),
                this.moveStep(goLeft),
                this.moveStep(goRight),
            )
        } else {
            listOf(
                this.moveStep(goLeft),
                this.moveStep(goRight),
            )
        }.filter{it.point in heatMap}
    }

    private fun findMinimalHeatPathPart2(start: Point, end: Point): Int {
        val heatSumMap = mutableMapOf<PointDirection, Int>()

        val compareByHeatTaken: Comparator<Pair<PointDirection, Int>> = compareBy{ it.second }
        val queue = PriorityQueue(compareByHeatTaken).apply { this.add(Pair(PointDirection(start, Direction.RIGHT, 0), 0)) }

        while (queue.isNotEmpty()) {
            val (currentPointDir, currentValue) = queue.remove()

            if (currentPointDir.point == end) {
                return currentValue
            }

            currentPointDir.nextStepsPlusValuesPart2().forEach { pd ->
                val newValue = currentValue + heatMap[pd.point]!!
                if (heatSumMap.getOrDefault(pd, 999_999_999) > newValue) {
                    heatSumMap[pd] = newValue
                    queue.add(Pair(pd, newValue))
                }
            }
        }
        return -1
    }

    private fun PointDirection.nextStepsPlusValuesPart2(): List<PointDirection> {
        val goLeft = this.direction.rotateLeft()
        val goRight = this.direction.rotateRight()
        val stepsDone = this.stepsDone

        val leftFourStepsPossible = this.point.moveSteps(goLeft, 4) in heatMap
        val rightFourStepsPossible = this.point.moveSteps(goRight, 4) in heatMap
        val straightFourStepsPossible = this.point.moveSteps(this.direction, 4) in heatMap
        val straightOneStepPossible = this.point.moveSteps(this.direction, 1) in heatMap

        val xx = if (stepsDone == 0) {
            val ll = if (leftFourStepsPossible) listOf(this.moveStep(goLeft)) else emptyList()
            val rr = if (rightFourStepsPossible) listOf(this.moveStep(goRight)) else emptyList()
            val ss = if (straightFourStepsPossible) listOf(this.moveStep(this.direction)) else emptyList()
            ll+rr+ss

        } else if (stepsDone < 4) {
            val ss = listOf(this.moveStep(this.direction))
            ss

        } else if (stepsDone < 10) {
            val ll = if (leftFourStepsPossible) listOf(this.moveStep(goLeft)) else emptyList()
            val rr = if (rightFourStepsPossible) listOf(this.moveStep(goRight)) else emptyList()
            val ss = if (straightOneStepPossible) listOf(this.moveStep(this.direction)) else emptyList()
            ll+rr+ss

        } else { //stepsDone = 10
            val ll = if (leftFourStepsPossible) listOf(this.moveStep(goLeft)) else emptyList()
            val rr = if (rightFourStepsPossible) listOf(this.moveStep(goRight)) else emptyList()
            ll+rr

        }
        return xx
    }


}

data class PointDirection(val point: Point, val direction: Direction, val stepsDone: Int) {
    fun moveStep(dir: Direction): PointDirection {
        return PointDirection(point.moveOneStep(dir), dir, if (this.direction == dir) stepsDone+1 else 1)
    }
}