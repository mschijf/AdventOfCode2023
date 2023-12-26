package adventofcode2023

import tool.coordinate.twodimensional.Point

fun main() {
    Day21(test=false).showResult()
}

class Day21(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val wholeGrid = inputAsGrid()
    private val maxX = wholeGrid.keys.maxOf { it.x }
    private val maxY = wholeGrid.keys.maxOf { it.y }
    private val sizeX = wholeGrid.keys.maxOf { it.x } + 1
    private val sizeY = wholeGrid.keys.maxOf { it.y } + 1

    private val gardenPlots = wholeGrid.filterValues { it == '.' || it == 'S' }.keys
    private val start = wholeGrid.filterValues { it == 'S' }.keys.first()

    override fun resultPartOne(): Any {
        val stepsToDo = if (test) 6 else 64
        return minimalPathToAllFields().filterValues { it <= stepsToDo && (it % 2) == (stepsToDo % 2)}.count()
//        return doSteps(stepsToDo)
    }

    override fun resultPartTwo(): Any {
        if (test) {
            return "we're mnot gonne run this one for test"
        }

        val stepsToDo = 26_501_365 //26501365
        val minPathToAllFields = minimalPathToAllFields()

        // clever thing: 26_501_365 is not just a number, it is 202300 * 131 + 65 (thanks to a beautiful read on:
        // https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21

        if (sizeX != sizeY) {
            return "The total starting grid  needs to be square. this doesn't work for rectangles"
        }

        val gridSize = sizeX
        val numberOfGridsReachable = (stepsToDo - gridSize/2) / gridSize

        val halfGrid = gridSize / 2
        val countEvenInGrid = minPathToAllFields.values.count { it % 2 == 0 }
        val countOddInGrid = minPathToAllFields.values.count { it % 2 == 1 }
        val countEvenCornersInGrid = minPathToAllFields.values.count{ it % 2 == 0 && it > halfGrid }
        val countOddCornersInGrid = minPathToAllFields.values.count{ it % 2 == 1 && it > halfGrid }
        val n = numberOfGridsReachable.toLong()

        //597_101_484_387_726 too low
        //wrong: 597_102_953_902_191
        //       597_102_953_902_191 rolf
        //597_102_954_104_491 too high

        //597_102_954_104_491

        //597_102_953_902_191

        //297_327_121_745_579

        println("oddTileCount: ${(n+1L)*(n+1L)}")
        println("evenTileCount: ${n.toLong()*n}")
        println("oddPoints: $countOddInGrid")
        println("evenPoints: $countEvenInGrid")

        println("oddCornerCount: ${n+1L}")
        println("evenCornerCount: $n")
        println("oddCornerPoints: $countOddCornersInGrid")
        println("evenCornerPoints: $countEvenCornersInGrid")


        val p2 = ((n+1)*(n+1)) * countOddInGrid +
                (n*n) * countEvenInGrid -
                (n+1) * countOddCornersInGrid +
                n * countEvenCornersInGrid -
                n


        return p2
    }

    private fun doSteps(maxSteps: Int): Int {
        var visited = setOf<Point>(start)

        repeat(maxSteps) {
            visited = visited.flatMap { pos -> pos.neighbors().filter { it in gardenPlots } }.toSet()
        }
        return visited.size
    }

    private fun minimalPathToAllFields(): Map<Point, Int> {
        val result = mutableMapOf<Point, Int>()
        val queue = ArrayDeque<Pair<Point, Int>>().apply{add(Pair(start, 0))}
        val visited = mutableSetOf<Point>().apply { add(start) }
        while (queue.isNotEmpty()) {
            val (current, stepsDone) = queue.removeFirst()
            result[current] = stepsDone
            current.neighbors().filter { it in gardenPlots }.filter {it !in visited}.forEach {nb->
                visited += nb
                queue.add(Pair(nb, stepsDone+1))
            }
        }
        return result
    }

}


//let p2 = ((n+1)*(n*1)) * odd_full + (n*n) * even_full - (n+1) * odd_corners + n * even_corners;

//        val oddTileCount = (n + 1L) * (n + 1L)
//        val evenTileCount = n * n.toLong()
//        val oddCornerCount = n + 1
//        val evenCornerCount = n

//        val evenPoints = visited.filter { it.value % 2 == 0 }
//        val oddPoints = visited.filter { it.value % 2 == 1 }
//        val evenCornerPoints = visited.filter { it.value % 2 == 0 && it.value > size / 2 }
//        val oddCornerPoints = visited.filter { it.value % 2 == 1 && it.value > size / 2 }

//
//        val total = (oddTileCount * oddPoints.size) +
//                (evenTileCount * evenPoints.size) -
//                (oddCornerCount * oddCornerPoints.size) +
//                (evenCornerCount * evenCornerPoints.size) -
//                n


//        val n = (maxSteps - grid.width() / 2) / grid.width() // 202_300
//        // For n=202300, which is even, we find out that there are (n + 1)^2 odd input-squares and n^2 even input-squares.
//        val oddTileCount = (n + 1L) * (n + 1L)
//        val evenTileCount = n * n.toLong()
//
//        // Some corners (odd) have to be cut out of our square, the other corners (even) have to be added.
//        // For each of the 4 corners, there are (n + 1) odd ones, and n even ones.
//        val oddCornerCount = n + 1
//        val evenCornerCount = n
//        val total = (oddTileCount * oddPoints.size) +
//                (evenTileCount * evenPoints.size) -
//                (oddCornerCount * oddCornerPoints.size) +
//                (evenCornerCount * evenCornerPoints.size) -
//                n
