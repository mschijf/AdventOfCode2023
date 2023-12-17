package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.mylambdas.collectioncombination.filterCombinedItems
import tool.mylambdas.collectioncombination.mapCombinedItems
import tool.mylambdas.collectioncombination.toCombinedItemsList
import java.util.*
import kotlin.collections.ArrayDeque

fun main() {
    Day17(test=true).showResult()
}

class Day17(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val heatMap = inputAsGrid().mapValues { it.value.digitToInt() }
    private val maxX = heatMap.keys.maxOf {it.x}
    private val maxY = heatMap.keys.maxOf {it.y}

    override fun resultPartOne(): Any {
        val start = pos(0,0)
        val end = pos(maxX, maxY)

        return findMinimalHeatPath(start, end)
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun findMinimalHeatPath(start: Point, end: Point): Int {
        val compareByHeatTaken: Comparator<Pair<PointDirection, Int>> = compareBy{ it.second }
        val queue = PriorityQueue(compareByHeatTaken).apply { this.add(Pair(PointDirection(start, Direction.RIGHT), 0)) }

        val heatSumMap = mutableMapOf<Point, Int>()


        while (queue.isNotEmpty()) {
            val (currentPointDir, currentValue) = queue.remove()

            if (currentPointDir.point == end) {
                return currentValue
            }

            currentPointDir.nextStepsPlusValues().forEach { pdv ->
                val newValue = currentValue+pdv.second
                if (heatSumMap.getOrDefault(pdv.first.point, 999_999_999) > newValue) {
                    heatSumMap[pdv.first.point] = newValue
                    queue.add(Pair(pdv.first, currentValue + pdv.second))
                }
            }
        }
        return -1
    }

    private fun PointDirection.nextStepsPlusValues(): List<Pair<PointDirection, Int>> {
    val goLeft = this.direction.rotateLeft()
    val goRight = this.direction.rotateRight()

    val p1=this.moveSteps(this.direction, 1)
    val p2=this.moveSteps(this.direction, 2)
    val p3=this.moveSteps(this.direction, 3)
    val v1 = heatMap.getOrDefault(p1.point, 999_999)
    val v2 = heatMap.getOrDefault(p2.point, 999_999) + v1
    val v3 = heatMap.getOrDefault(p3.point, 999_999) + v2

    val p11=this.moveSteps(goLeft, 1)
    val p12=this.moveSteps(goLeft, 2)
    val p13=this.moveSteps(goLeft, 3)
    val v11 = heatMap.getOrDefault(p11.point, 999_999)
    val v12 = heatMap.getOrDefault(p12.point, 999_999) + v11
    val v13 = heatMap.getOrDefault(p13.point, 999_999) + v12

    val p21=this.moveSteps(goRight, 1)
    val p22=this.moveSteps(goRight, 2)
    val p23=this.moveSteps(goRight, 3)
    val v21 = heatMap.getOrDefault(p21.point, 999_999)
    val v22 = heatMap.getOrDefault(p22.point, 999_999) + v21
    val v23 = heatMap.getOrDefault(p23.point, 999_999) + v22

    return listOf(
        Pair(p1, v1), Pair(p2, v2), Pair(p3, v3),
        Pair(p11, v11), Pair(p12, v12), Pair(p13, v13),
        Pair(p21, v21), Pair(p22, v22), Pair(p23, v23),
    ).filter{it.first.point in heatMap}
    }

}

data class PointDirection(val point: Point, val direction: Direction, val stespDone: Int) {
    fun moveSteps(dir: Direction, steps: Int): PointDirection {
        return PointDirection(point.moveSteps(dir, steps), dir)
    }
}


//private fun PointDirection.nextSteps(): List<PointDirection> {
//    val goLeft = this.direction.rotateLeft()
//    val goRight = this.direction.rotateRight()
//    return listOf(
//        this.moveSteps(this.direction, 1), this.moveSteps(this.direction, 2), this.moveSteps(this.direction, 3),
//        this.moveSteps(goLeft, 1), this.moveSteps(goLeft, 2), this.moveSteps(goLeft, 3),
//        this.moveSteps(goRight, 1), this.moveSteps(goRight, 2), this.moveSteps(goRight, 3),
//    ).filter{it.point in heatMap}
//}
//

//private fun PointDirection.nextStepsPlusValues(): List<Pair<PointDirection, Int>> {
//    val goLeft = this.direction.rotateLeft()
//    val goRight = this.direction.rotateRight()
//
//    val p1=this.moveSteps(this.direction, 1)
//    val p2=this.moveSteps(this.direction, 2)
//    val p3=this.moveSteps(this.direction, 3)
//    val v1 = heatMap.getOrDefault(p1.point, 999_999)
//    val v2 = heatMap.getOrDefault(p2.point, 999_999) + v1
//    val v3 = heatMap.getOrDefault(p3.point, 999_999) + v2
//
//    val p11=this.moveSteps(goLeft, 1)
//    val p12=this.moveSteps(goLeft, 2)
//    val p13=this.moveSteps(goLeft, 3)
//    val v11 = heatMap.getOrDefault(p11.point, 999_999)
//    val v12 = heatMap.getOrDefault(p12.point, 999_999) + v11
//    val v13 = heatMap.getOrDefault(p13.point, 999_999) + v12
//
//    val p21=this.moveSteps(goRight, 1)
//    val p22=this.moveSteps(goRight, 2)
//    val p23=this.moveSteps(goRight, 3)
//    val v21 = heatMap.getOrDefault(p21.point, 999_999)
//    val v22 = heatMap.getOrDefault(p22.point, 999_999) + v21
//    val v23 = heatMap.getOrDefault(p23.point, 999_999) + v22
//
//    return listOf(
//        Pair(p1, v1), Pair(p2, v2), Pair(p3, v3),
//        Pair(p11, v11), Pair(p12, v12), Pair(p13, v13),
//        Pair(p21, v21), Pair(p22, v22), Pair(p23, v23),
//    ).filter{it.first.point in heatMap}
//}

