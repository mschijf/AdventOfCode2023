package adventofcode2023.Day24Hulp

import kotlin.math.absoluteValue

data class Point3DLong(val x: Long, val y: Long, val z: Long) {

    fun plusXYZ(dx: Long, dy: Long, dz: Long) = Point3DLong(x+dx, y+dy, z+dz)
    fun plusX(dx: Long) = plusXYZ(dx, 0, 0)
    fun plusY(dy: Long) = plusXYZ(0, dy, 0)
    fun plusZ(dz: Long) = plusXYZ(0, 0, dz)

    fun distanceTo(otherPos: Point3DLong) = (otherPos.x - x).absoluteValue + (otherPos.y - y).absoluteValue + (otherPos.z - z).absoluteValue

    companion object {
        fun of(input: String): Point3DLong = input
            .removeSurrounding("<", ">")
            .removeSurrounding("(", ")")
            .removeSurrounding("[", "]")
            .removeSurrounding("{", "}")
            .split(",").run { Point3DLong(this[0].trim().toLong(), this[1].trim().toLong(), this[2].trim().toLong()) }

        val origin = Point3DLong(0,0,0)
    }

}
