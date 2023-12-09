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
        val differenceSequence = mutableListOf<List<Int>>()
        var next = this
        while (next.any{it != 0}) {
            differenceSequence.add(next)
            next = next.zipWithNext { a, b -> b-a }
        }
        return differenceSequence
    }

    private fun List<Int>.predictNextValue(): Int {
        return this
            .differenceSequence()
            .foldRight(0){intList, acc -> acc + intList.last()}
//
//        val extrapolateList = this.differenceSequence()
//        var newValue = 0
//        extrapolateList.reversed().forEach {
//            newValue = it.last() + newValue
//        }
//        return newValue
    }

    private fun List<Int>.predictFirstValue(): Int {
        return this
            .differenceSequence()
            .foldRight(0){intList, acc -> intList.first() - acc}
//
//        val extrapolateList = this.differenceSequence()
//        var newValue = 0
//        extrapolateList.reversed().forEach {
//            newValue = it.first() - newValue
//        }
//        return newValue
    }

}


