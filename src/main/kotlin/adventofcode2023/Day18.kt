package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import tool.mylambdas.substringBetween
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

fun main() {
    Day18(test=true).showResult()
}

class Day18(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Lavaduct Lagoon", hasInputFile = true) {

    private val digPlan = inputLines.map {DigAction.of(it)}
    private val digPlanPart2 = inputLines.map {DigAction.ofPart2(it)}

    override fun resultPartOne(): Any {
        val borderLines = digPlan.digIt()
        val borderLinesVertical = borderLines.filter {it.isVertical}.toSet()
        val horLines = borderLines.divideIntoHorizontalLines()
        val verLines = borderLines.divideIntoVerticalLines()
        val rectangles = makeRectangles(horLines, verLines)

        val rectanglesToCount = rectangles
            .filter{ rc -> rc.countLinesToTheRight(borderLinesVertical) % 2 == 1 }
            .sortedBy { 100* it.topLeft.y + it.topLeft.x}

//        println(rectanglesToCount[2].countLinesToTheRight(borderLinesVertical))
//        println(rectanglesToCount[9].countLinesToTheRight(borderLinesVertical))
//
        val totalArea = rectanglesToCount.sumOf { rc -> rc.area()}
        val allLines = rectanglesToCount.flatMap { rc -> rc.toLines()}
        val allUniqueLines = allLines.distinct()

        val totalLineLength = allLines.sumOf { l -> l.length() }
        val uniqueLineLength = allUniqueLines.sumOf { l -> l.length() }

        val allEdgePoints = allUniqueLines.flatMap { listOf(it.from,it.to) }
        val allUniqueEdgePoints = allEdgePoints.distinct()

        return totalArea - totalLineLength + uniqueLineLength - allEdgePoints.size + allUniqueEdgePoints.size


//        val all = border.floodGrid() + border
////        all.printAsGrid()
//        return all.size
    }

    override fun resultPartTwo(): Any {
//        val border = setOf(
//            Line(pos(0,0), pos(0,2)),
//            Line(pos(0,2), pos(2,2)),
//            Line(pos(2,2), pos(2,0)),
//            Line(pos(2,0), pos(0,0)),
//        )
//        return border.area()

        return "todo"
    }

    private fun List<DigAction>.digIt(): Set<Line> {
        val result = mutableSetOf<Line>()
        var current = pos(0,0)
        this.forEach {action ->
            val next = current.moveSteps(action.direction, action.steps)
            result.add(Line(current, next))
            current = next
        }
        return result
    }



//    private fun between(p1: Point, p2: Point): List<Point> {
//        if (p1.x  == p2.x) {
//            return (min(p1.y, p2.y) .. max(p1.y, p2.y)).map{ y -> pos(p1.x, y)}
//        } else {
//            return (min(p1.x, p2.x) .. max(p1.x, p2.x)).map{ x -> pos(x, p1.y)}
//        }
//    }
//
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
    val xValue = this.topLeft.x
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
    fun length() = from.distanceTo(to)+1
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

    fun length() : Int {
        return toLines().sumOf { it.length() } - 3
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


/**
 * voor alle hor lines: bepaal begin en eindpunten --> sorteer op x. aantal horlijntjes = aantal punten - 1
 * voor alle ver lines : bepaal begin en eindpunten --> sorteer op y. aantal verlijntjes = aantal punten - 1
 * blokken zijn alle hor en verlijntjes ccombineren :
 *     horlijn 1 met verlijn1. horlijn2 met verlijn 1, ...
 *     horlijn 1 met verlijn2. horlijn2 met verlijn 2, ..
 *     ...
 *
 *     voor ieder blok, pak een punt in het blok en tel het aantal snijnpunten met verticale lijnen groter dan x
 *     als dat oneven is, dan hele blok in de polygon
 */


