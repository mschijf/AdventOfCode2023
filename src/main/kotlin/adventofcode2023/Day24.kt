package adventofcode2023

import tool.coordinate.threedimensional.Point3DLong
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
        return xx.size
    }

    override fun resultPartTwo(): Any {
//      equations to solve:
//        (x-xi)*(yv-yvi) = (y-yi)*(xv-xvi)
//        (x-xi)*(zv-zvi) = (z-zi)*(xv-xvi)
//      where (x,y,z,xv,yv,zv are the location and speed coordinates to find.
//      and (xi,yi,zi,xvi,yvi,zvi are the location and speed coordinates from the test set, for i in all testlines
//      (and probably you only ned the first three test lines, since that gives you a total of six equations (for six unknowns)

        movingPointList.take(3).forEach {mp->
            val (x1, y1, z1) = mp.location
            val (xv1, yv1, zv1) = mp.speed
            println("(x-$x1)*(yv-$yv1) = (y-$y1)*(xv-$xv1),")
            println("(x-$x1)*(zv-$zv1) = (z-$z1)*(xv-$xv1),")
        }

        return "NOT DONE, USE AN EQUATION SOLVER"
    }
}
//20, 19, 15 @  1, -5, -3
data class MovingPoint(val location: Point3DLong, val speed: Speed) {
    companion object{
        fun of(raw: String): MovingPoint {
            val p1 = Point3DLong.of(raw.split(" @ ")[0])
            val p2 = Point3DLong.of(raw.split(" @ ")[1])
            return MovingPoint(
                location = p1,
                speed = Speed(p2.x, p2.y, p2.z)
            )
        }
    }

    fun toHailLine(): HailLine {
        return HailLine.of(this)
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

data class HailLine(val a: Double, val b: Double, val c: Double) {
    companion object {
        fun of (p1: Point3DLong, p2:Point3DLong): HailLine {
            //y = aX + b
            val a = (p2.y-p1.y).toDouble() / (p2.x - p1.x).toDouble()
            val b = p1.y - a*p1.x
            return HailLine(a, b, 0.0)
        }
        fun of (mp: MovingPoint): HailLine {
            //y = aX + b
            val a = mp.speed.y.toDouble()/mp.speed.x.toDouble()
            val b = mp.location.y - a * mp.location.x
            return HailLine(a, b, 0.0)
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

