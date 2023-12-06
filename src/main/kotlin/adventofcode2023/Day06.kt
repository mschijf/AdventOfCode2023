package adventofcode2023

fun main() {
    Day06(test=false).showResult()
}

class Day06(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    override fun resultPartOne(): Any {
        val timeList = inputLines.first().substringAfter("Time:").trim().split("\\s+".toRegex()).map{it.toLong()}
        val distanceList = inputLines.last().substringAfter("Distance:").trim().split("\\s+".toRegex()).map{it.toLong()}

        return timeList
            .mapIndexed{ index, time -> play(time).count{it > distanceList[index]} }
            .reduce { acc, i -> acc*i }
    }

    private fun play(time: Long):List<Long> {
        return (0L..time).map{t -> t*(time-t)}
    }

    override fun resultPartTwo(): Any {
        val time = inputLines.first().substringAfter("Time:").trim().replace(" ", "").toLong()
        val distance = inputLines.last().substringAfter("Distance:").trim().replace(" ", "").toLong()

        return play(time).count{t -> t*(time-t) > distance}
    }
}


