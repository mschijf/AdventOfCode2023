package adventofcode2023

fun main() {
    Day06(test=false).showResult()
}

class Day06(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    override fun resultPartOne(): Any {
        val timeList = inputLines.first().substringAfter("Time:").trim().split("\\s+".toRegex()).map{it.toLong()}
        val distanceList = inputLines.last().substringAfter("Distance:").trim().split("\\s+".toRegex()).map{it.toLong()}

        return timeList
            .mapIndexed{ index, time -> countBeatingRecords(time, distanceList[index]) }
            .reduce { acc, i -> acc*i }
    }

    private fun countBeatingRecords(time: Long, record: Long): Int {
        return (0L..time).count { t -> t*(time-t) > record }
    }

    //        227850
    //result: 42948149
    override fun resultPartTwo(): Any {
        val time = inputLines.first().substringAfter("Time:").trim().replace(" ", "").toLong()
        val distance = inputLines.last().substringAfter("Distance:").trim().replace(" ", "").toLong()
        return countBeatingRecords(time, distance)
    }
}


