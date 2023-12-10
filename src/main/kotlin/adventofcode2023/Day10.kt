package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.Comparator as Comparator1

fun main() {
    Day10(test=true).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = false) {

    private val pipeGrid = inputAsGrid()
    private val start = pipeGrid.filterValues { it == 'S' }.keys.first()

    override fun resultPartOne(): Any {
        val dg = shortestPathToALlPoints()
//        dg.printAsGrid(default="."){ it.toString()?:"?" }
        return dg.values.max()
    }

    private val cleanGrid = pipeGrid.cleanGrid()
    private val extendedGrid = cleanGrid.extendGrid()
    private val floodGrid = extendedGrid.initializeFloodGrid()

    override fun resultPartTwo(): Any {
        cleanGrid.printAsGrid {it.toString()}
        fillFloodGridRecursive(pos(-1,-1))
//        floodGrid.printAsGrid {it.toString()}
        return floodGrid.values.count { it == '.' || it == '*'}
    }

    private fun Map<Point, Char>.cleanGrid(): Map<Point, Char> {
        val onPipePath = shortestPathToALlPoints()
        val xx =  this.map { if (it.key in onPipePath) it.key to it.value else it.key to '*' }.toMap()
        return xx
    }

    private fun Map<Point, Char>.extendGrid(): Map<Point, Char> {
        val mutMap = mutableMapOf<Point, Char>()
        this.forEach { entry ->
            val x = entry.key.x * 2
            val y = entry.key.y * 3
            val pipeSymbol = if (entry.value == 'S') entry.key.determineS() else entry.value
            when (pipeSymbol) {
                '|' -> mutMap.setSquare(x, y, '#', '#', '#', ',', ',', ',')
                '-' -> mutMap.setSquare(x, y, ',', '#', ',', ',', '#', ',')
                'L' -> mutMap.setSquare(x, y, '#', '#', ',', ',', '#', ',')
                'J' -> mutMap.setSquare(x, y, '#', '#', ',', ',', ',', ',')

                '7' -> mutMap.setSquare(x, y, ',', '#', '#', ',', ',', ',')
                'F' -> mutMap.setSquare(x, y, ',', '#', '#', ',', '#', ',')
                '.' -> mutMap.setSquare(x, y, ',', '.', ',', ',', ',', ',')
                '*' -> mutMap.setSquare(x, y, '.', '*', '.', '.', '.', '.')
                else -> throw Exception("unexpected")
            }
        }
        return mutMap
    }

    private fun MutableMap<Point, Char>.setSquare(x: Int, y: Int, ch00: Char, ch01: Char, ch02: Char, ch10: Char, ch11: Char, ch12: Char) {
        this[pos(x,y)]=ch00
        this[pos(x,y+1)]=ch01
        this[pos(x,y+2)]=ch02
        this[pos(x+1,y)]=ch10
        this[pos(x+1,y+1)]=ch11
        this[pos(x+1,y+2)]=ch12
    }

    private fun Map<Point, Char>.initializeFloodGrid(): MutableMap<Point, Char> {
        val floodGrid = this.toMutableMap()
        val maxX = this.keys.maxOf { it.x }
        val maxY = this.keys.maxOf { it.y }
        for (i in -1 .. maxX+1) {
            floodGrid[pos(i, -1)] = ','
            floodGrid[pos(i, maxY+1)] = ','
        }
        for (i in -1 .. maxY+1) {
            floodGrid[pos(-1, i)] = ','
            floodGrid[pos(maxX+1, i)] = ','
        }
        return floodGrid
    }

    private fun shortestPathToALlPoints() : Map<Point, Int> {

        val distanceMap = mutableMapOf<Point, Int>()
        distanceMap[start] = 0
        val compareByDistance: Comparator1<Point> = compareBy{ distanceMap[it]?:0 }
        val queue = PriorityQueue(compareByDistance).apply { this.add(start) }

        while (queue.isNotEmpty()) {
            val currentPos = queue.remove()

            currentPos.pipeNeighbors().filterNot { nb -> nb in distanceMap }.forEach {newPos ->
                distanceMap[newPos] = distanceMap[currentPos]!! + 1
                queue.add(newPos)
            }
        }
        return distanceMap
    }

    private fun fillFloodGrid(start: Point) {
        val queue = ArrayDeque<Point>().apply { this.add(start) }

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            floodGrid[currentPos] = 'O'

            currentPos.neighbors().filter { it in floodGrid }.forEach {
                if (floodGrid[it]!! == '.' || floodGrid[it]!! == ',') {
                    queue.add(it)
                }
            }
        }
//
//
//        if ( floodGrid[current]!! == 'O') {
//            return
//        }
//
//        if ( floodGrid[current]!! != '.' &&  floodGrid[current]!! != ',') {
//            return
//        }
//
//        floodGrid[current] = 'O'
//        current.neighbors().filter { it in floodGrid }.forEach {
//            fillFloodGrid(it)
//        }
    }

    private fun fillFloodGridRecursive(current: Point) {
        if ( floodGrid[current]!! == 'O') {
            return
        }

        if ( floodGrid[current]!! != '.' &&  floodGrid[current]!! != ',') {
            return
        }

        floodGrid[current] = 'O'
        current.neighbors().filter { it in floodGrid }.forEach {
            fillFloodGrid(it)
        }
    }




//    | is a vertical pipe connecting north and south.
//    - is a horizontal pipe connecting east and west.
//    L is a 90-degree bend connecting north and east.
//    J is a 90-degree bend connecting north and west.
//    7 is a 90-degree bend connecting south and west.
//    F is a 90-degree bend connecting south and east.
//    . is ground; there is no pipe in this tile.
//    S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.

    private fun Point.pipeNeighbors(): List<Point> {
        val tmp = when (pipeGrid[this]) {
            '|' -> listOf(this.north(), this.south())
            '-' -> listOf(this.east(), this.west())
            'L' -> listOf(this.north(), this.east())
            'J' -> listOf(this.north(), this.west())
            '7' -> listOf(this.south(), this.west())
            'F' -> listOf(this.south(), this.east())
            'S' -> this.neighbors().filter { it.pipeNeighbors().contains(this) }
            else -> emptyList()
        }
        return tmp
    }

    private fun Point.determineS(): Char {
        val pipeList = listOf('|', '-', 'L', 'J', '7', 'F')
        val startPipeNeighborSet = this.pipeNeighbors().toSet()

        pipeList.forEach {
            val tmp = when (it) {
                '|' -> setOf(this.north(), this.south())
                '-' -> setOf(this.east(), this.west())
                'L' -> setOf(this.north(), this.east())
                'J' -> setOf(this.north(), this.west())
                '7' -> setOf(this.south(), this.west())
                'F' -> setOf(this.south(), this.east())
                else -> emptySet()
            }
            if (startPipeNeighborSet == tmp)
                return it
        }

        throw Exception("unknown start pipe")
    }

}


