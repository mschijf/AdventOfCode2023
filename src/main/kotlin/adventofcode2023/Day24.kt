package adventofcode2023

import adventofcode2023.Day24Hulp.Point3D
import tool.mylambdas.collectioncombination.filterCombinedItems

fun main() {
    Day24(test=false).showResult()
}

class Day24(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    val movingPointList = inputLines.map{MovingPoint.of(it)}

    val area = if (test) 7.0..27.0 else 200000000000000.0..400000000000000.0

    override fun resultPartOne(): Any {
        val xx = movingPointList.filterCombinedItems { movingPoint1, movingPoint2 ->
            val intersectionPoint = movingPoint1.toHailLine().intersects(movingPoint2.toHailLine())
            val inFuture = movingPoint1.isInFuture(intersectionPoint) && movingPoint2.isInFuture(intersectionPoint)
            val inArea = intersectionPoint.x in area && intersectionPoint.y in area
            inFuture && inArea
        }

//        val i1=0
//        val i2=3
//        val intersectionPoint = movingPointList[i1].toHailLine().intersects(movingPointList[i2].toHailLine())
//        println(movingPointList[i1].isInFuture(intersectionPoint))
//        println(movingPointList[i2].isInFuture(intersectionPoint))
        return xx.size
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }
}
//20, 19, 15 @  1, -5, -3
data class MovingPoint(val location: Point3D, val speed: Speed) {
    companion object{
        fun of(raw: String): MovingPoint {
            val p1 = Point3D.of(raw.split(" @ ")[0])
            val p2 = Point3D.of(raw.split(" @ ")[1])
            return MovingPoint(
                location = p1,
                speed = Speed(p2.x, p2.y, p2.z)
            )
        }
    }

    fun toHailLine(): HailLine {
        return HailLine.of(
            p1=location,
            p2=location.plusXYZ(speed.x, speed.y, speed.z)
        )
    }

    fun isInFuture(aPoint: Point3DDouble): Boolean {
        if (aPoint.x.isInfinite() || aPoint.y.isInfinite())
            return false
        return if (speed.x < 0) {
            aPoint.x < location.x.toDouble()
        } else {
            aPoint.x > location.x.toDouble()
        }
    }

}

data class Speed(val x: Long, val y: Long, val z: Long) {
}

data class HailLine(val a: Double, val b: Double) {
    companion object {
        fun of (p1: Point3D, p2:Point3D): HailLine {
            //y = aX + b
            val a = (p2.y-p1.y).toDouble() / (p2.x - p1.x).toDouble()
            val b = p1.y - a*p1.x
            return HailLine(a, b)
        }
    }

    fun intersects(other: HailLine): Point3DDouble {
        val x = (other.b - this.b)/(this.a - other.a)
        val y = (this.a * x) + this.b
        return Point3DDouble( x, y, 0.0)
    }
}

data class Point3DDouble(val x: Double, val y: Double, val z: Double) {

}

