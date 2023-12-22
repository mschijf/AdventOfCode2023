package adventofcode2023

import tool.coordinate.threedimensional.Point3D
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day22(test=false).showResult()
}

//45214 --> wrong

//95059 --> goede antwoord

class Day22(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val brickList = inputLines.mapIndexed{index, s ->  Brick.of(index, s)}
    private val heightMap: Map<Int, MutableList<Brick>>// = mapOf<Int, MutableList<Brick>>()
    private val supportMap: Map<Brick, MutableList<Brick>>// = brickList.associateWith{mutableListOf<Brick>()}
    private val supportedByMap: Map<Brick, MutableList<Brick>>// = brickList.associateWith{mutableListOf<Brick>()}

    init {
        val result = brickList.fallDown()
        heightMap = result.first
        supportMap = result.second

        supportedByMap = brickList.map { it to mutableListOf<Brick>() }.toMap().toMutableMap()
        supportMap.forEach { (keyBrick, supportsList) ->
            supportsList.forEach { supportedBrick ->
                supportedByMap[supportedBrick]!!.add(keyBrick)
            }
        }
    }



    override fun resultPartOne(): Any {
        return safeDeletes().size
    }

    override fun resultPartTwo(): Any {
        return brickList.sumOf{countRemovals(it)}
//        return (countRemovals(brickList[1]))
    }

    private fun countRemovals(brick: Brick): Int {
        var succ = determineSuccessors(brick)
        var newSucc = succ.filter{rc -> (supportedByMap[rc]!!-succ).isEmpty()}.toSet() + brick
        while (succ.size != newSucc.size) {
            succ = newSucc
            newSucc = succ.filter{rc -> (supportedByMap[rc]!!-succ).isEmpty()}.toSet() + brick
        }

        var c = 0
        (succ-brick).forEach {rc ->
            if ((supportedByMap[rc]!! - succ).isEmpty()) {
                c++
            }
        }
        return c
    }



    private fun determineSuccessors(brick: Brick): Set<Brick> {

        val result = mutableSetOf<Brick>()
        supportMap[brick]!!.forEach {
            result.addAll(determineSuccessors(it))
        }
        return result + brick

    }



    private fun List<Brick>.fallDown(): Pair<Map<Int, MutableList<Brick>>, Map<Brick, MutableList<Brick>>>  {
        val heightMap = mutableMapOf<Int, MutableList<Brick>>()
        val supportMap = this.associateWith{mutableListOf<Brick>()}

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

    private fun safeDeletes(): Set<Brick> {
        val result = mutableListOf<Brick>()
        heightMap.values.forEach {brickList ->
            brickList.forEach {brickOnHeight ->
                if (brickOnHeight.canBeDeletedSafely(brickList)) {
                    result.add(brickOnHeight)
                }
            }
        }
        return result.toSet()
    }

    private fun Brick.canBeDeletedSafely(levelBrickList: List<Brick>): Boolean {
        if (supportMap[this]!!.isEmpty()) {
            return true
        } else {
            val union = mutableSetOf<Brick>()
            (levelBrickList-this).forEach {
                union += supportMap[it]!!.toSet()
            }
            if (union.containsAll(supportMap[this]!!)) {
                return true
            }
        }
        return false
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
        val result = mutableListOf<Brick>()
        rectList.forEach { other->
            if (this.overlaps(other.rectangle))
                result.add(other)
        }
        return result
    }

}


data class Brick(val name: String, val p1: Point3D, val p2: Point3D) {
    val rectangle = Rectangle(pos(p1.x, p1.y), pos(p2.x, p2.y) )
    val height = p1.z - p2.z + 1

    override fun toString(): String {
        return name
    }
    companion object {
        fun of(index: Int, raw: String): Brick {
            val points = listOf(Point3D.of(raw.split("~")[0]), Point3D.of(raw.split("~")[1]))
            if (points[0].z >= points[1].z) {
                return Brick(
                    name = ('A' + index).toString(),
                    p1 = points[0],
                    p2 = points[1]
                )
            } else {
                return Brick(
                    name = ('A' + index).toString(),
                    p1 = points[1],
                    p2 = points[0]
                )
            }
        }
    }
}



