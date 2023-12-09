package adventofcode2023

fun main() {
    Day09(test=false).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val historyLines = inputLines.map{it.split("\\s+".toRegex()).map{it.toInt()}}

    override fun resultPartOne(): Any {
        return historyLines.sumOf{it.extrapolate() }
    }

    override fun resultPartTwo(): Any {
        return historyLines.sumOf{it.extrapolate2() }
    }

    private fun List<Int>.extrapolate(): Int {
        val extrapolateList = mutableListOf<List<Int>>(this)
        var next = this
        while (next.any{it != 0}) {
            next = next.drop(1).mapIndexed { index, i -> i - next[index] }
            extrapolateList.add(next)
        }
        var newValue = 0
        for (i in extrapolateList.size-2 downTo 0  ) {
            newValue = extrapolateList[i].last() + newValue
        }
        return newValue
    }

    private fun List<Int>.extrapolate2(): Int {
        val extrapolateList = mutableListOf<List<Int>>(this)
        var next = this
        while (next.any{it != 0}) {
            next = next.drop(1).mapIndexed { index, i -> i - next[index] }
            extrapolateList.add(next)
        }
        var newValue = 0
        for (i in extrapolateList.size-2 downTo 0  ) {
            newValue = extrapolateList[i].first() - newValue
        }
        return newValue
    }

}


