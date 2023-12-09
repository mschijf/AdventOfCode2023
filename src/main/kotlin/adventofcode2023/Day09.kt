package adventofcode2023

fun main() {
    Day09(test=false).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val historyLines = inputLines.map{line -> line.split("\\s+".toRegex()).map{ word -> word.toInt() }}

    override fun resultPartOne(): Any {
        return historyLines.sumOf{ it.predictNextValue() }
    }

    override fun resultPartTwo(): Any {
        return historyLines.sumOf{ it.predictFirstValue() }
    }

    private fun List<Int>.differenceSequence() : List<List<Int>> {
        val differenceSequence = mutableListOf<List<Int>>(this)
        var next = this
        while (next.any{it != 0}) {
            next = next.drop(1).mapIndexed { index, i -> i - next[index] }
            differenceSequence.add(next)
        }
        return differenceSequence
    }

    private fun List<Int>.predictNextValue(): Int {
        val extrapolateList = this.differenceSequence()
        var newValue = 0
        for (i in extrapolateList.size-2 downTo 0  ) {
            newValue = extrapolateList[i].last() + newValue
        }
        return newValue
    }

    private fun List<Int>.predictFirstValue(): Int {
        val extrapolateList = this.differenceSequence()
        var newValue = 0
        for (i in extrapolateList.size-2 downTo 0  ) {
            newValue = extrapolateList[i].first() - newValue
        }
        return newValue
    }

}


