package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import tool.mylambdas.substringBetween
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day18(test=true).showResult()
}

class Day18(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Lavaduct Lagoon", hasInputFile = true) {

    private val digPlan = inputLines.map {DigAction.of(it)}
    private val digPlanPart2 = inputLines.map {DigAction.ofPart2(it)}

    override fun resultPartOne(): Any {
        val border = digPlan.digIt()
        val all = border.floodGrid() + border
//        all.printAsGrid()
        return all.size
    }

    override fun resultPartTwo(): Any {
        return digPlanPart2
    }

    private fun List<DigAction>.digIt(): Set<Point> {
        val result = mutableSetOf<Point>()
        var current = pos(0,0)
        this.forEach {action ->
            val next = current.moveSteps(action.direction, action.steps)
            result.addAll(between(current, next))
            current = next
        }
        return result
    }

    private fun between(p1: Point, p2: Point): List<Point> {
        if (p1.x  == p2.x) {
            return (min(p1.y, p2.y) .. max(p1.y, p2.y)).map{ y -> pos(p1.x, y)}
        } else {
            return (min(p1.x, p2.x) .. max(p1.x, p2.x)).map{ x -> pos(x, p1.y)}
        }
    }

    private fun Set<Point>.floodGrid(): Set<Point> {
        val result = mutableSetOf<Point>()
        val startFrom: Point = pos(1,1)

        val queue = ArrayDeque<Point>().apply { this.add(startFrom) }

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            currentPos.neighbors().forEach { p ->
                if (p !in this && p !in result) {
                    result.add(p)
                    queue.add(p)
                }
            }
        }
        return result
    }
}

data class Line(val from: Point, val to: Point) {

}

data class DigAction(val direction: Direction, val steps: Int, val color: String) {
    companion object {
        //U 2 (#7a21e3)
        fun of(raw: String) : DigAction {
            return DigAction(
                direction = when (raw.substringBefore(" ")) {
                    "U" -> Direction.UP
                    "D" -> Direction.DOWN
                    "L" -> Direction.LEFT
                    "R" -> Direction.RIGHT
                    else -> throw Exception("UnknownDirection") },
                steps = raw.drop(2).substringBefore(" ").toInt(),
                color = raw.substringBetween("(", ")")
            )
        }

        fun ofPart2(raw: String) : DigAction {
            val hexString = "0x" + raw.substringBetween("(#", ")")
            val steps = Integer.decode(hexString.take(7))
            val direction = when(hexString.last())  {
                '0' -> Direction.RIGHT
                '1' -> Direction.DOWN
                '2' -> Direction.LEFT
                '3' -> Direction.UP
                else -> throw Exception("UnknownDirection")
            }
            return DigAction(
                direction = direction,
                steps = steps,
                color = hexString
            )
        }

//        private fun String.hexToInt(): Int {
//            return Integer.decode(this)
//        }

    }
}




