package adventofcode2023

fun main() {
    Day12(test=false).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {


    override fun resultPartOne(): Any {
        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first()}
        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().split(",").map{it.toInt()}}
        return execute(conditionRecordLines, conditionRecordGroups)
    }

    override fun resultPartTwo(): Any {
        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first().copyFive("?")}
        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().copyFive(",").split(",").map{it.toInt()}}
        return execute(conditionRecordLines, conditionRecordGroups)
    }


    private fun String.copyFive(delimiter: String): String {
        return "$this$delimiter$this$delimiter$this$delimiter$this$delimiter$this"
    }

    private fun execute(conditionRecordLines: List<String>, conditionRecordGroups: List<List<Int>>): Long {
        var sum = 0L
        conditionRecordLines.forEachIndexed { index, s ->
            val count = calculate(s, conditionRecordGroups[index])
            println("$index -> $count")
            sum += count
        }
        return sum
    }


    private fun calculate(inputString: String, conditionRecordGroup: List<Int>, lastChar: Char=' ', hekjesGehad: Int = 0,
                          cache:MutableMap<Triple<String, Int, List<Int>>, Long> = mutableMapOf() ): Long {

        if (conditionRecordGroup.isEmpty()) {
            return if (inputString.contains("#")) 0 else 1L
        }

        if (inputString.isEmpty()) {
            return if (conditionRecordGroup.size == 1 && conditionRecordGroup.first() == hekjesGehad) 1 else 0
        }

        if (hekjesGehad > conditionRecordGroup.first()) {
            return 0
        }

        val cacheKey = Triple(inputString, hekjesGehad, conditionRecordGroup)
        if (cache.contains(cacheKey)) {
            return cache[cacheKey]!!
        }

        val currentChar = inputString.first()
        val remainder = inputString.drop(1)
        val tmp = if (currentChar == '?') {
            val metHekje = calculate(remainder, conditionRecordGroup, '#', hekjesGehad+1)

            val metPuntje = if (lastChar == '#') {
                if (hekjesGehad != conditionRecordGroup.first()) {
                    0
                } else {
                    calculate(remainder, conditionRecordGroup.drop(1), '.', 0)
                }
            } else {
                calculate(remainder, conditionRecordGroup, '.', 0)
            }

            metHekje + metPuntje
        } else {
            if (lastChar == '#' && currentChar == '.') {
                if (hekjesGehad != conditionRecordGroup.first()) {
                    0
                } else {
                    calculate(remainder, conditionRecordGroup.drop(1), currentChar, 0)
                }
            } else {
                calculate(remainder, conditionRecordGroup, currentChar, if (currentChar == '#') hekjesGehad + 1 else 0)
            }
        }
        cache[cacheKey] = tmp
        return tmp
    }
}