package adventofcode2023

import tool.coordinate.threedimensional.Point3D
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day22(test=false).showResult()
}

class Day22(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val brickList = inputLines.map{Brick.of(it)}
    val supportMap = brickList.associateWith{mutableListOf<Brick>()}
    val heightMap = mutableMapOf<Int, MutableList<Brick>>()

    override fun resultPartOne(): Any {
        brickList.fallDown()
        return safeDeletes()
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun List<Brick>.fallDown(): Pair<Map<Int, MutableList<Brick>>, Map<Brick, MutableList<Brick>>>  {
//        val supportMap = this.associateWith{mutableListOf<Brick>()}
//        val heightMap = mutableMapOf<Int, MutableList<Brick>>()
        this.sortedBy { it.p2.z }.forEach {aBrick ->
            var nothingHappened = true
            for (z in aBrick.p2.z downTo 1) {
                if (heightMap.contains(z)) {
                    val rectList = heightMap[z]!!
                    val overlappedWith = aBrick.rectangle.overlaps(rectList.map{it})
                    if (overlappedWith.isNotEmpty()) {
                        if (!heightMap.contains(z+aBrick.height)) {
                            heightMap[z+aBrick.height] = mutableListOf()
                        }
                        heightMap[z+aBrick.height]!!.add(aBrick)
                        nothingHappened = false

                        overlappedWith.forEach { overlapper -> supportMap[overlapper]!!.add(aBrick) }
                        break;
                    }
                }
            }
            if (nothingHappened) {
                if (!heightMap.contains(aBrick.height)) {
                    heightMap[aBrick.height] = mutableListOf()
                }
                heightMap[aBrick.height]!!.add(aBrick)
            }
        }
        return Pair(heightMap, supportMap)
    }

    private fun safeDeletes(): Int {
        var result = mutableListOf<Brick>()
        heightMap.values.forEach {brickList ->
            brickList.forEach {brickOnHeight ->
                if (supportMap[brickOnHeight]!!.isEmpty()) {
                    result.add(brickOnHeight)
                } else {
                    var union = mutableSetOf<Brick>()
                    (brickList-brickOnHeight).forEach {
                        union += supportMap[it]!!.toSet()
                    }
                    if (union.containsAll(supportMap[brickOnHeight]!!)) {
                        result.add(brickOnHeight)
                    }
                }
            }

        }
        return result.size
    }
}

fun IntRange.overlaps(other:IntRange): Boolean {
    return (this.first in other || this.last in other || other.first in this || other.last in this)
}

data class Rectangle(val p1: Point, val p2: Point) {
    val minX = min(p1.x, p2.x)
    val maxX = max(p1.x, p2.x)
    val minY = min(p1.y, p2.y)
    val maxY = max(p1.y, p2.y)

    fun overlaps(other: Rectangle): Boolean {
        val i1 = (minX..maxX).overlaps(other.minX..other.maxX)
        val i2 = (minY..maxY).overlaps(other.minY..other.maxY)
        return i1 && i2
    }

    fun overlaps(rectList: List<Brick>): List<Brick> {
        var result = mutableListOf<Brick>()
        rectList.forEach { other->
            if (this.overlaps(other.rectangle))
                result.add(other)
        }
        return result
    }

}


data class Brick(val p1: Point3D, val p2: Point3D) {
    val rectangle = Rectangle(pos(p1.x, p1.y), pos(p2.x, p2.y) )
    val height = p1.z - p2.z + 1

    companion object {
        fun of(raw: String): Brick {
            val points = listOf(Point3D.of(raw.split("~")[0]), Point3D.of(raw.split("~")[1]))
            if (points[0].z >= points[1].z) {
                return Brick(
                    p1 = points[0],
                    p2 = points[1]
                )
            } else {
                return Brick(
                    p1 = points[1],
                    p2 = points[0]
                )
            }
        }
    }
}

