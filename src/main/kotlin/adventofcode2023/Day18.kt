package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.mylambdas.substringBetween
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day18(test=false).showResult()
}

class Day18(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Lavaduct Lagoon", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val digPlan = inputLines.map {DigAction.of(it)}
        val borderLines = digPlan.digIt()
        return borderLines.calculateArea()
    }

    override fun resultPartTwo(): Any {
        val digPlanPart2 = inputLines.map {DigAction.ofPart2(it)}
        val borderLines = digPlanPart2.digIt()
        return borderLines.calculateArea()
    }

    private fun Set<Line>.calculateArea(): Long {
        val borderLinesVertical = this.filter {it.isVertical}.toSet()
        val horLines = this.divideIntoHorizontalLines()
        val verLines = this.divideIntoVerticalLines()
        val rectangles = makeRectangles(horLines, verLines)

        val rectanglesToCount = rectangles
            .filter{ rc -> rc.countLinesToTheRight(borderLinesVertical) % 2 == 1 }
            .sortedBy { 100* it.topLeft.y + it.topLeft.x}

        val totalArea = rectanglesToCount.sumOf { rc -> rc.area()}
        return totalArea
    }


    private fun List<DigAction>.digIt(): Set<Line> {
        val result = mutableSetOf<Line>()
        var current = pos(0,0)
        var lastCorner = CornerType.D90
        this.forEachIndexed {index, action ->

            val nextCorner = if (index == this.size-1) {
                expectedCorner(this[index].direction, this[0].direction)
            } else {
                expectedCorner(this[index].direction, this[index + 1].direction)
            }

            val extra = when (lastCorner) {
                CornerType.D90 -> when (nextCorner) {
                    CornerType.D90 -> 1
                    CornerType.D270 -> 0
                }

                CornerType.D270 -> when (nextCorner) {
                    CornerType.D90 -> 0
                    CornerType.D270 -> -1
                }
            }

            val next = current.moveSteps(action.direction, action.steps + extra)
            result.add(Line(current, next))

            current = next
            lastCorner = nextCorner
        }
        return result
    }

    enum class CornerType{
        D90, D270
    }
    private fun expectedCorner(dir1: Direction, dir2:Direction): CornerType {
        return when (dir1) {
            Direction.RIGHT -> when(dir2) {
                Direction.UP -> CornerType.D270
                Direction.DOWN -> CornerType.D90
                else -> throw Exception("WEEIRD")
            }
            Direction.LEFT -> when(dir2) {
                Direction.UP -> CornerType.D90
                Direction.DOWN -> CornerType.D270
                else -> throw Exception("WEEIRD")
            }
            Direction.UP -> when(dir2) {
                Direction.LEFT -> CornerType.D270
                Direction.RIGHT -> CornerType.D90
                else -> throw Exception("WEEIRD")
            }
            Direction.DOWN -> when(dir2) {
                Direction.LEFT -> CornerType.D90
                Direction.RIGHT -> CornerType.D270
                else -> throw Exception("WEEIRD")
            }
        }
    }

}


private fun Set<Line>.divideIntoHorizontalLines(): List<Line> {
    return this
        .filter { it.isHorizontal }
        .flatMap { listOf(it.from.x, it.to.x) }
        .distinct()
        .sortedBy { it }
        .zipWithNext { a, b -> Line(pos(a,0),pos(b,0) ) }
}

private fun Set<Line>.divideIntoVerticalLines(): List<Line> {
    return this
        .filter { it.isVertical }
        .flatMap { listOf(it.from.y, it.to.y) }
        .distinct()
        .sortedBy { it }
        .zipWithNext { a, b -> Line(pos(0, a),pos(0, b) ) }
}

private fun makeRectangles(horizontalLines: List<Line>, verticalLines: List<Line>) : List<Rectangle> {
    return horizontalLines.flatMap { hor ->
        verticalLines.map { ver ->
            val maxX = max(hor.from.x, hor.to.x)
            val minX = min(hor.from.x, hor.to.x)
            val maxY = max(ver.from.y, ver.to.y)
            val minY = min(ver.from.y, ver.to.y)
            Rectangle(
                pos(minX, minY),
                pos(maxX, minY),
                pos(minX, maxY),
                pos(maxX, maxY)
            )
        }
    }
}

private fun Rectangle.countLinesToTheRight(verticaLineSet: Set<Line>): Int {
    val xValue = this.topLeft.x + 0.5
    val yValue = this.topLeft.y + this.topLeft.distanceTo(this.bottomLeft)*1.0 / 2.0
    return verticaLineSet
        .count{vl ->
            vl.from.x > xValue  &&
                    yValue > min(vl.from.y.toDouble(), vl.to.y.toDouble()) &&
                    yValue < max(vl.from.y.toDouble(), vl.to.y.toDouble())
        }
}

data class Line(val from: Point, val to: Point) {
    val isHorizontal = from.y == to.y
    val isVertical = from.x == to.x
    fun length() = from.distanceTo(to)
}

data class Rectangle(val topLeft: Point, val topRight: Point, val bottomLeft: Point, val bottomRight: Point) {
    fun toLines() : List<Line> {
        return listOf(
            Line(topLeft, topRight),
            Line(topRight, bottomRight),
            Line(bottomLeft, bottomRight),
            Line(topLeft, bottomLeft),
        )
    }

    fun area() : Long {
        return Line(topLeft, topRight).length().toLong() * Line(topRight, bottomRight).length()
    }
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
    }
}
