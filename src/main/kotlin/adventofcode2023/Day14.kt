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

//    override fun resultPartOne(): Any {
////        printPlatform()
//        rollNorth()
////        printPlatform()
//        return platformTotalLoad()
//    }

//    val history : MutableSet<List<MutableList<Char>>> = mutableSetOf()
    val history : MutableList<Int> = mutableListOf()
    val loadHistory : MutableList<Int> = mutableListOf()
    override fun resultPartTwo(): Any {
//        printPlatform()
        history.add(platformValue())
        loadHistory.add(platformTotalLoad())
        repeat(1_000_000_000) {
            cycle()
            if (platformValue() in history) {
                val index = history.indexOf(platformValue())
                val cycleTime = (it+1-index)
                val restje = (1_000_000_000 - (index)) % cycleTime
                val totalRuns = index + restje
                println("REPEATER after $it at $index, cycletime: $cycleTime, totalruns = $totalRuns")
                println(loadHistory[index+restje])
//                printPlatform()
                return "KLAAR"
            }
            history.add(platformValue())
            loadHistory.add(platformTotalLoad())
            if (it % 1_000_000 == 0)
                println("done $it cycles" )
        }
//        printPlatform()

        repeat (6) {

        }
        return platformTotalLoad()
    }

    private fun platformValue() : Int {
        var value = 0
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[row][col] == 'O')
                    value += platform.size * row + col
            }
        }
        return value
    }

    private fun cycle() {
        rollNorth()
        rollWest()
        rollSouth()
        rollEast()
    }

    private fun rollNorth() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[row][col] == 'O')
                    moveCubeNorth(row, col)
            }
        }
    }

    private fun rollSouth() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[platform.size - row - 1][col] == 'O')
                    moveCubeSouth(platform.size - row - 1, col)
            }
        }
    }

    private fun rollEast() {
        for (col in platform[0].indices) {
            for (row in platform.indices) {
                if (platform[row][platform[0].size - col - 1] == 'O')
                    moveCubeEast(row, platform[0].size - col - 1)
            }
        }
    }

    private fun rollWest() {
        for (col in platform[0].indices) {
            for (row in platform.indices) {
                if (platform[row][col] == 'O')
                    moveCubeWest(row, col)
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

    private fun moveCubeSouth(row: Int, col: Int) {
        var walk = row+1
        while (walk < platform.size && platform[walk][col] == '.') {
            platform[walk-1][col] = '.';
            platform[walk][col] = 'O';
            walk++
        }
    }

    private fun moveCubeWest(row: Int, col: Int) {
        var walk = col-1
        while (walk >= 0 && platform[row][walk] == '.') {
            platform[row][walk+1] = '.';
            platform[row][walk] = 'O';
            walk--
        }
    }

    private fun moveCubeEast(row: Int, col: Int) {
        var walk = col+1
        while (walk < platform[row].size && platform[row][walk] == '.') {
            platform[row][walk-1] = '.';
            platform[row][walk] = 'O';
            walk++
        }
    }

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


