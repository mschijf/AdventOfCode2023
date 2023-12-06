package adventofcode2023

fun main() {
    Day06(test=false).showResult()
}

class Day06(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    //result: 227850
    override fun resultPartOne(): Any {
        val timeList = inputLines.first().substringAfter("Time:").trim().split("\\s+".toRegex()).map{it.toLong()}
        val distanceList = inputLines.last().substringAfter("Distance:").trim().split("\\s+".toRegex()).map{it.toLong()}

        return timeList
            .mapIndexed{ index, time -> countBeatingRecords(time, distanceList[index]) }
            .reduce { acc, i -> acc*i }
    }

    // you can reduce time, by only looping over half of the times,
    // since 3*(10-3) = 7*(10-7), you only have to look for 0,1,2,3,4,5

    private fun countBeatingRecords(time: Long, record: Long): Int {
        return if (time % 2 == 0L)
            (0L..time/2 - 1).count { t -> t*(time-t) > record } * 2 + (if ((time-time/2)*(time/2) > record) 1 else 0)
        else
            (0L..time/2).count { t -> t*(time-t) > record } * 2
        //return (0L..time).count { t -> t*(time-t) > record } * 2
    }

    //result: 42948149
    override fun resultPartTwo(): Any {
        val time = inputLines.first().substringAfter("Time:").trim().replace(" ", "").toLong()
        val distance = inputLines.last().substringAfter("Distance:").trim().replace(" ", "").toLong()
        return countBeatingRecords(time, distance)
    }
}


