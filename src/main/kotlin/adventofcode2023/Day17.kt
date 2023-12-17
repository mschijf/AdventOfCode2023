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

        return findMinimalHeatPath(ultra = false)
    }

    override fun resultPartTwo(): Any {
        return findMinimalHeatPath(ultra = true)
    }

    private fun findMinimalHeatPath(ultra: Boolean): Int {
        val heatSumMap = mutableMapOf<PointDirection, Int>()

        val compareByHeatTaken: Comparator<Pair<PointDirection, Int>> = compareBy{ it.second }
        val queue = PriorityQueue(compareByHeatTaken).apply { this.add(Pair(PointDirection(start, Direction.RIGHT, 0), 0)) }

        while (queue.isNotEmpty()) {
            val (currentPointDir, currentValue) = queue.remove()

            if (currentPointDir.point == end) {
                return currentValue
            }

            currentPointDir.nextStepsPlusValues(ultra).forEach { pd ->
                val newValue = currentValue + heatMap[pd.point]!!
                if (heatSumMap.getOrDefault(pd, 999_999_999) > newValue) {
                    heatSumMap[pd] = newValue
                    queue.add(Pair(pd, newValue))
                }
            }
        }
        return -1
    }

    private fun PointDirection.nextStepsPlusValues(ultra: Boolean): List<PointDirection> {
        val goLeft = this.direction.rotateLeft()
        val goRight = this.direction.rotateRight()
        val stepsDone = this.stepsDone

        if (!ultra) {
            return if (stepsDone < 3) {
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

        } else {
            val leftFourStepsPossible = this.point.moveSteps(goLeft, 4) in heatMap
            val rightFourStepsPossible = this.point.moveSteps(goRight, 4) in heatMap
            val straightFourStepsPossible = this.point.moveSteps(this.direction, 4) in heatMap
            val straightOneStepPossible = this.point.moveSteps(this.direction, 1) in heatMap

            return if (stepsDone == 0) {
                listOfNotNull(
                    if (leftFourStepsPossible) this.moveStep(goLeft) else null,
                    if (rightFourStepsPossible) this.moveStep(goRight) else null,
                    if (straightFourStepsPossible) this.moveStep(this.direction) else null
                )
            } else if (stepsDone < 4) {
                listOf(this.moveStep(this.direction))
            } else if (stepsDone < 10) {
                listOfNotNull(
                    if (leftFourStepsPossible) this.moveStep(goLeft) else null,
                    if (rightFourStepsPossible) this.moveStep(goRight) else null,
                    if (straightOneStepPossible) this.moveStep(this.direction) else null
                )
            } else { //stepsDone = 10
                listOfNotNull(
                    if (leftFourStepsPossible) this.moveStep(goLeft) else null,
                    if (rightFourStepsPossible) this.moveStep(goRight) else null,
                )
            }
        }


    }
}

data class PointDirection(val point: Point, val direction: Direction, val stepsDone: Int) {
    fun moveStep(dir: Direction): PointDirection {
        return PointDirection(point.moveOneStep(dir), dir, if (this.direction == dir) stepsDone+1 else 1)
    }
}