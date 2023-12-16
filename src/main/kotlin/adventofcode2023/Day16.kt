package adventofcode2023

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid

fun main() {
    Day16(test=false).showResult()
}

class Day16(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val grid = inputAsGrid()

    override fun resultPartOne(): Any {
        return beamIt(PointDirection(pos(-1,0), Direction.RIGHT))
    }

    override fun resultPartTwo(): Any {
        val maxX = grid.keys.maxOf{it.x}
        val maxY = grid.keys.maxOf{it.y}
        val maxEnergyList = listOf(
            (0..maxX).maxOf { beamIt(PointDirection(pos(it, -1), Direction.DOWN)) },
            (0..maxX).maxOf { beamIt(PointDirection(pos(it, maxY+1), Direction.UP)) },
            (0..maxY).maxOf { beamIt(PointDirection(pos(-1, it), Direction.RIGHT)) },
            (0..maxY).maxOf { beamIt(PointDirection(pos(maxX+1, it), Direction.LEFT)) }
        )
        return maxEnergyList.max()
    }

    private fun beamIt(startConfiguration: PointDirection): Int {
        var beamList: List<PointDirection> = listOf(startConfiguration)

        val beamListHistory = mutableSetOf<PointDirection>()
        val gridEnergy = mutableSetOf<Point>()

        var oldSize = -1
        while (oldSize != beamListHistory.size) {
            oldSize = beamListHistory.size
            beamList = beamList
                .iterate()
                .filterNot { it in beamListHistory }
                .also {
                    beamListHistory.addAll(it)
                    gridEnergy.energize(it)
                }
        }

//        grid.keys.printAsGrid { if (gridEnergy.contains(it)) "#" else "."}

        return gridEnergy.size
    }


    private fun List<PointDirection>.iterate(): List<PointDirection> {
        return this
            .flatMap{it.move()}
            .filter{it.position in grid}
    }

    private fun MutableSet<Point>.energize(points: List<PointDirection>) {
        this.addAll(points.map { it.position })
    }


    private fun PointDirection.move(): List<PointDirection> {
        val newPos = this.position.moveOneStep(this.direction)
        if (newPos !in grid) {
            return emptyList()
        }

        return when (grid[newPos]) {
            '\\' -> when (this.direction) {
                Direction.RIGHT -> listOf(PointDirection(newPos, Direction.DOWN))
                Direction.LEFT -> listOf(PointDirection(newPos, Direction.UP))
                Direction.UP -> listOf(PointDirection(newPos, Direction.LEFT))
                Direction.DOWN -> listOf(PointDirection(newPos, Direction.RIGHT))
            }

            '/' -> when (this.direction) {
                Direction.RIGHT -> listOf(PointDirection(newPos, Direction.UP))
                Direction.LEFT -> listOf(PointDirection(newPos, Direction.DOWN))
                Direction.UP -> listOf(PointDirection(newPos, Direction.RIGHT))
                Direction.DOWN -> listOf(PointDirection(newPos, Direction.LEFT))
            }

            '-' -> when (this.direction) {
                Direction.RIGHT -> listOf(PointDirection(newPos, Direction.RIGHT))
                Direction.LEFT -> listOf(PointDirection(newPos, Direction.LEFT))
                Direction.UP -> listOf(PointDirection(newPos, Direction.LEFT), PointDirection(newPos, Direction.RIGHT))
                Direction.DOWN -> listOf(PointDirection(newPos, Direction.LEFT), PointDirection(newPos, Direction.RIGHT))
            }

            '|' -> when (this.direction) {
                Direction.RIGHT -> listOf(PointDirection(newPos, Direction.UP), PointDirection(newPos, Direction.DOWN))
                Direction.LEFT -> listOf(PointDirection(newPos, Direction.UP), PointDirection(newPos, Direction.DOWN))
                Direction.UP -> listOf(PointDirection(newPos, Direction.UP))
                Direction.DOWN -> listOf(PointDirection(newPos, Direction.DOWN))
            }

            '.' -> listOf(PointDirection(newPos, this.direction))

            else -> throw Exception("ASLAKSLKALSKL")
        }
    }

}

data class PointDirection(val position: Point, val direction: Direction)