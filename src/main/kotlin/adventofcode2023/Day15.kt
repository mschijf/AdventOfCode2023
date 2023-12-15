package adventofcode2023

fun main() {
    Day15(test=false).showResult()
}

class Day15(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val sequenceList = inputLines.first().split(",")
    private val boxes = Array(256) {mutableListOf<Pair<String, Int>>()}

    override fun resultPartOne(): Any {
        return sequenceList.sumOf{it.hash() }
    }

    override fun resultPartTwo(): Any {
        sequenceList.forEach { sequence ->
            val label = sequence.label()
            val boxNr = label.hash()
            val index = boxes[boxNr].indexOfFirst { it.first == label }
            if (sequence.operatorIsEquals()) {
                val focus = sequence.focusValue()
                if (index >= 0) {
                    boxes[boxNr][index] = Pair(label, focus)
                } else {
                    boxes[boxNr].add(Pair(label, focus))
                }
            } else {
                if (index >= 0) {
                    boxes[boxNr].removeAt(index)
                }
            }
        }

//        boxes.forEachIndexed { index, pairs ->
//            println("$index --> $pairs")
//        }
        
        return boxes.mapIndexed { index, pairs -> (index+1L)*pairs.boxValue() }.sum()
    }
    
    fun List<Pair<String, Int>>.boxValue() : Long {
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

    private fun String.focusValue(): Int {
        return this.substringAfter("=").toInt()
    }

}


