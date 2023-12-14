package adventofcode2023

fun main() {
    Day14(test=false).showResult()
}

class Day14(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

//    private val cubeRocks = inputAsGrid().filterValues{it == '#'}.keys
//    private val roundedRocks = inputAsGrid().filterValues{it == 'O'}.keys

    private val platform = inputLines.mapIndexed { row, line ->
        line.mapIndexed { col, ch ->  ch}.toMutableList()
    }

    override fun resultPartOne(): Any {
//        printPlatform()
        rollNorth()
//        printPlatform()
        return platformTotalLoad()
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun rollNorth() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[row][col] == 'O')
                    moveCubeNorth(row, col)
            }
        }
    }

    private fun moveCubeNorth(row: Int, col: Int) {
        var walk = row-1
        while (walk >= 0 && platform[walk][col] == '.') {
            platform[walk+1][col] = '.';
            platform[walk][col] = 'O';
            walk--
        }
    }

//    private fun moveCubeSouth(row: Int, col: Int) {
//        var walk = row+1
//        while (walk >= platform.size && platform[walk][col] == '.') {
//            platform[walk+1][col] = '.';
//            platform[walk][col] = 'O';
//            walk++
//        }
//    }
//
//    private fun moveCubeNorth(row: Int, col: Int) {
//        var walk = row-1
//        while (walk >= 0 && platform[walk][col] == '.') {
//            platform[walk+1][col] = '.';
//            platform[walk][col] = 'O';
//            walk--
//        }
//    }
//
//    private fun moveCubeNorth(row: Int, col: Int) {
//        var walk = row-1
//        while (walk >= 0 && platform[walk][col] == '.') {
//            platform[walk+1][col] = '.';
//            platform[walk][col] = 'O';
//            walk--
//        }
//    }

    private fun platformTotalLoad() : Int {
        val tl = platform.mapIndexed { index, chars ->
            chars.count { ch -> ch == 'O' } * (platform.size-index)
        }.sum()
        return tl
    }

    fun printPlatform() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                print(platform[row][col])
            }
            println()
        }
        println()
    }

}


