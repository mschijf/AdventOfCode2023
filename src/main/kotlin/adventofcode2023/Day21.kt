package adventofcode2023

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day21(test=true).showResult()
}

class Day21(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val wholeGrid = inputAsGrid()
    private val sizeX = wholeGrid.keys.maxOf { it.x } + 1
    private val sizeY = wholeGrid.keys.maxOf { it.y } + 1

    private val gardenPlots = wholeGrid.filterValues { it == '.' || it == 'S' }.keys
    private val start = wholeGrid.filterValues { it == 'S' }.keys.first()

    override fun resultPartOne(): Any {
        return doSteps(if (test) 6 else 64)
    }

    override fun resultPartTwo(): Any {
        return doStepsPart2(if (test) 500  else 26_501_365)
    }

    private fun doSteps(maxSteps: Int): Int {
        var visited = setOf<Point>(start)

        repeat(maxSteps) {
            visited = visited.flatMap { pos -> pos.neighbors().filter { it in gardenPlots } }.toSet()
        }
        return visited.size
    }

    private fun doStepsPart2(maxSteps: Int): Int {
        var visited = setOf<Point>(start)

        repeat(maxSteps) {
            val result = mutableSetOf<Point>()
            visited.forEach { pos ->
                pos.neighbors().forEach { nb ->
                    val mappedNeighbor = pos(Math.floorMod(nb.x, sizeX), Math.floorMod(nb.y, sizeY))
                    if (mappedNeighbor in gardenPlots) {
                        result += nb
                    }
                }
            }
            visited = result
        }
        return visited.size
    }


//    private fun stespNecessay(): Int {
//        var a = 1L
//        repeat(26501365) {
//            a += it
//        }
//        println(a)
//        return -1
//
//        var visited = setOf<Point>(start)
//        var set1 = emptySet<Point>()
//        var i = 0
//        var result = mutableSetOf<Point>()
//        repeat(135) {
//            result = mutableSetOf<Point>()
//            visited.forEach { pos ->
//                pos.neighbors().forEach { nb ->
//                    val mappedNeighbor = pos(Math.floorMod(nb.x, sizeX), Math.floorMod(nb.y, sizeY))
//                    if (mappedNeighbor in gardenPlots) {
//                        result += nb
//                    }
//                }
//            }
////                val atHome = result.count { it in gardenPlots }
////                println("$i -> $atHome (of ${result.size})")
////                print("    ")
////                println(gardenPlots - )
//
//            val prevSet = set1
//            set1 = result.filter { it in gardenPlots }.toSet()
//
//            if (i > 130) {
//                println((gardenPlots - (set1 + prevSet)).sortedBy { it.y })
//            }
//
//            i++
//            visited = result
//
//        }
//    }
}


