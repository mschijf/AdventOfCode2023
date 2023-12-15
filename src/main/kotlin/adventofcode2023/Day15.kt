package adventofcode2023

fun main() {
    Day15(test=false).showResult()
}

class Day15(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val sequenceList = inputLines.first().split(",")

    override fun resultPartOne(): Any {
        return sequenceList.sumOf{ it.hash() }
    }

    override fun resultPartTwo(): Any {
        val boxes = Array(256) {mutableListOf<Pair<String, Int>>()}
        sequenceList.forEach { sequence -> boxes.processSequence(sequence) }
        return boxes.mapIndexed { index, pairs -> (index+1L)*pairs.boxValue() }.sum()
    }

    private fun Array<MutableList<Pair<String, Int>>>.processSequence(sequence: String) {
        if (sequence.operatorIsEquals()) {
            this[sequence.label().hash()].processOperationEquals(sequence)
        } else {
            this[sequence.label().hash()].processOperationDash(sequence)
        }
    }

    private fun MutableList<Pair<String, Int>>.processOperationEquals(sequence: String) {
        val label = sequence.label()
        val index = this.indexOfFirst { it.first == label }
        val focalValue = sequence.focalValue()
        if (index >= 0) {
            this[index] = Pair(label, focalValue)
        } else {
            this.add(Pair(label, focalValue))
        }
    }

    private fun MutableList<Pair<String, Int>>.processOperationDash(sequence: String) {
        val label = sequence.label()
        val index = this.indexOfFirst { it.first == label }
        if (index >= 0) {
            this.removeAt(index)
        }
    }

    private fun List<Pair<String, Int>>.boxValue() : Long {
        return this.mapIndexed { index, pair -> (index+1L)*pair.second }.sum()
    }
    

//    Determine the ASCII code for the current character of the string.
//    Increase the current value by the ASCII code you just determined.
//    Set the current value to itself multiplied by 17.
//    Set the current value to the remainder of dividing itself by 256.

    private fun String.hash(): Int {
        var currentValue = 0
        this.forEach {ch ->
            currentValue += ch.code
            currentValue *= 17
            currentValue %= 256
        }
        return currentValue
    }

    private fun String.label(): String {
        return if (this.contains("-"))
            this.substringBefore("-")
        else
            this.substringBefore("=")
    }

    private fun String.operatorIsEquals(): Boolean {
        return this.contains("=")
    }

    private fun String.focalValue(): Int {
        return this.substringAfter("=").toInt()
    }

}


