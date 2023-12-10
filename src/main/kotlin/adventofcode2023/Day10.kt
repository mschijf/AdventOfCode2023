package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.Comparator as Comparator1

fun main() {
    Day10(test=false).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = false) {

    private val pipeGrid = inputAsGrid()
    private val start = pipeGrid.filterValues { it == 'S' }.keys.first()

    // functions for pipeGrid specific
    private fun Point.pipeNeighbors(): Set<Point> {
        return if (pipeGrid[this] == 'S') {
            this.neighbors().filter { it in pipeGrid && it.pipeNeighbors().contains(this) }.toSet()
        } else {
            this.pipeNeighbors(pipeGrid[this]!!)
        }
    }

    private fun Point.determineS(): Char {
        val startPipeNeighborSet = this.pipeNeighbors()

        pipeSymbolList.forEach {
            val tmp = this.pipeNeighbors(it)
            if (startPipeNeighborSet == tmp)
                return it
        }

        throw Exception("unknown start pipe")
    }

    //------------------------------------------------------------------------------------------------------------------

    override fun resultPartOne(): Any {
        val shortestPathMap = shortestPathToALlPoints()
        return shortestPathMap.values.max()
    }


    override fun resultPartTwo(): Any {
        val cleanGrid = pipeGrid.cleanJunkPipes()
        val extendedGrid = cleanGrid.resizeGrid()
        val floodGrid = extendedGrid.initializeFloodGrid()
        floodGrid.fillFloodGrid()

        return floodGrid.values.count { it == '.' || it == '*'}
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * determine for each point the shortest path from start
     */
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

    //replace all non-used pipes by a new symbol: '*'
    private fun Map<Point, Char>.cleanJunkPipes(): Map<Point, Char> {
        val shortestPathMap = shortestPathToALlPoints()
        return this.map {
            if (it.key in shortestPathMap)
                it.key to it.value
            else if (it.value in pipeSymbolList)
                it.key to '*'
            else
                it.key to '.'
        }.toMap()
    }

    //
    // resize the grid to make the 'squeezed' entries visible. Each element on a grid will be replaced by a 2x3 grid of
    // symbols, as follow:
    //
    //
    // +---+          +---+          +---+           +---+          +---+          +---+          +---+
    // | J |          | L |          | | |           | 7 |          | F |          | - |          | . |
    // +---+          +---+          +---+           +---+          +---+          +---+          +---+
    //
    // +---+---+      +---+---+      +---+---+       +---+---+      +---+---+      +---+---+      +---+---+
    // | | | . |      | | | . |      | | | . |       | . | . |      | . | . |      | . | . |      | . | . |
    // +---+---+      +---+---+      +---+---+       +---+---+      +---+---+      +---+---+      +---+---+
    // | J | . |      | L | - |      | | | . |       | 7 | . |      | F | - |      | - | - |      | . | . |
    // +---+---+      +---+---+      +---+---+       +---+---+      +---+---+      +---+---+      +---+---+
    // | . | . |      | . | . |      | | | . |       | | | . |      | | | . |      | . | . |      | . | . |
    // +---+---+      +---+---+      +---+---+       +---+---+      +---+---+      +---+---+      +---+---+
    //
    // As a result, this will make the grid much bigger, but with clear paths. For instance
    // +---+---+
    // | J | L |
    // +---+---+
    // | - | - |
    // +---+---+
    //
    // +---+---+---+---+
    // | | | . | | | . |
    // +---+---+---+---+
    // | J | . | L | - |
    // +---+---+---+---+
    // | . | . | . | . |
    // +---+---+---+---+
    // | . | . | . | . |
    // +---+---+---+---+
    // | - | - | - | - |
    // +---+---+---+---+
    // | . | . | . | . |
    // +---+---+---+---+
    //
    // Furthermore, since the form of the pipes are no longer interesting, I choose to replace them all by a '#' symbol
    // to make it more visible/readable when we print the grid.
    // Moreover, the new added empty fields have been symbolized by  a ',' instead of '.', to distinquish them from the original empty fields.
    //

    private fun Map<Point, Char>.resizeGrid(): Map<Point, Char> {
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
                '*' -> mutMap.setSquare(x, y, ',', '.', ',', ',', ',', ',')
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

    //
    // put an extra edge with empty cells around all borders of a grid, to be sure we have a 'outside grid' defined as empty cells.
    // I put the symbol ',' in it, to distinquish them form the orgnal empty fields ('.' fields)
    //
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

    /**
     * From a (empty) start point, outside the grid, start filling the grid with a 'flood-fill-algorithm'
     * put a 'O' an empty cell while traversing alle adjacent cells
     */

    private fun MutableMap<Point, Char>.fillFloodGrid(startFrom: Point = pos(-1,-1) ) {
        val queue = ArrayDeque<Point>().apply { this.add(startFrom) }

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()

            currentPos.neighbors().forEach { p ->
                if (this.isEmptyCell(p)) {
                    this[p] = 'O'
                    queue.add(p)
                }
            }
        }
    }

    private fun Map<Point, Char>.isEmptyCell(pos: Point) : Boolean {
        val symbol = this[pos]?:'?'
        return (symbol == '.' || symbol == ',')
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

private val pipeSymbolList = listOf('|', '-', 'L', 'J', '7', 'F')

private fun Point.pipeNeighbors(pipeSymbol: Char): Set<Point> {
    val tmp = when (pipeSymbol) {
        '|' -> setOf(this.north(), this.south())
        '-' -> setOf(this.east(), this.west())
        'L' -> setOf(this.north(), this.east())
        'J' -> setOf(this.north(), this.west())
        '7' -> setOf(this.south(), this.west())
        'F' -> setOf(this.south(), this.east())
        else -> emptySet()
    }
    return tmp
}

