package adventofcode2023

fun main() {
    Day12(test=true).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {


    override fun resultPartOne(): Any {
        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first()}
        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().split(",").map{it.toInt()}}
        return execute(conditionRecordLines, conditionRecordGroups)
//        return conditionRecordLines.mapIndexed { index, s -> s.permutationCount(conditionRecordGroups[index]) }.sum()
    }

//    override fun resultPartTwo(): Any {
//        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first().copyFive("?")}
//        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().copyFive(",").split(",").map{it.toInt()}}
//        return conditionRecordLines.filter{it.startsWith("?") && it.endsWith("?")}.count()
//        return execute(conditionRecordLines, conditionRecordGroups)
//        //return conditionRecordLines.mapIndexed { index, s -> s.permutationCount(conditionRecordGroups[index]) }.sum()
//    }


    private fun String.copyFive(delimiter: String): String {
        return "$this$delimiter$this$delimiter$this$delimiter$this$delimiter$this"
    }

    private fun execute(conditionRecordLines: List<String>, conditionRecordGroups: List<List<Int>>): Long {
        var sum = 0L
        conditionRecordLines.forEachIndexed { index, s ->
//            val count = s.permutationCount( conditionRecordGroups[index], "", 0)
            val count = s.permutationCountV2( conditionRecordGroups[index], "", 0)
            println("$index -> $count")
            sum += count
        }
        return sum
    }


//    private fun String.permutationCount(conditionRecordGroup:List<Int>, s: String="", index: Int = 0): Int {
//        val tmpConditionGroup = s.makeGroups()
//        if (s.length == this.length) {
//            return if (conditionRecordGroup == tmpConditionGroup) 1 else 0
//        }
//        if (!conditionRecordGroup.startsWith(tmpConditionGroup)) {
//            return 0
//        }
//
//        if (this[index] == '?') {
//            return this.permutationCount(conditionRecordGroup, s+"#", index+1) + this.permutationCount(conditionRecordGroup, s+".", index+1)
//        } else {
//            return this.permutationCount(conditionRecordGroup, s+this[index], index+1)
//        }
//    }

    private fun String.permutationCount(conditionRecordGroup:List<Int>, s: String, stringIndex: Int): Long {
        if (s.length == this.length) {
            val tmpConditionGroup = s.makeGroups()
            return if (conditionRecordGroup == tmpConditionGroup) 1 else 0
        }
        val tmpConditionGroup = s.makeGroups()
        if (!conditionRecordGroup.startsWith(tmpConditionGroup)) {
            return 0
        }

        val tmp = if (this[stringIndex] == '?') {
             this.permutationCount(conditionRecordGroup, s+"#", stringIndex+1) +
                     this.permutationCount(conditionRecordGroup, s+".", stringIndex+1)
        } else {
            this.permutationCount(conditionRecordGroup, s+this[stringIndex], stringIndex+1)
        }
        return tmp
    }


    private fun String.permutationCountV2(conditionRecordGroup:List<Int>, s: String, stringIndex: Int, cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf<Pair<Int, Int>, Long>()): Long {
        val tmpConditionGroup = s.makeGroups()
        if (s.length == this.length) {
            return if (conditionRecordGroup == tmpConditionGroup) 1 else 0
        }
        if (!conditionRecordGroup.startsWith(tmpConditionGroup)) {
            return 0
        }

        if (s.endsWith(".")) {
            val cacheKey = Pair(stringIndex, conditionRecordGroup.size - tmpConditionGroup.size)
            if (cache.contains(cacheKey)) {
                return cache[cacheKey]!!
            }
        }

        val tmp = if (this[stringIndex] == '?') {
            this.permutationCountV2(conditionRecordGroup, s+"#", stringIndex+1, cache) +
                    this.permutationCountV2(conditionRecordGroup, s+".", stringIndex+1, cache)
        } else {
            this.permutationCountV2(conditionRecordGroup, s+this[stringIndex], stringIndex+1, cache)
        }

        if (s.endsWith(".")) {
            val cacheKey = Pair(stringIndex, conditionRecordGroup.size - tmpConditionGroup.size)
            cache[cacheKey] = tmp
        }
        return tmp
    }





    private fun List<Int>.startsWith(other: List<Int>): Boolean {
        if (other.size == 0) {
            return true
        }

        if (other.size == 1) {
            return other[0] <= this[0]
        }

        if (other.size > this.size)
            return false

        for (index in 0 .. other.size-2) {
            if (this[index] != other[index])
                return false
        }
        return true
    }

    private fun String.makeGroups(): List<Int> {
        return this.split(".").map{it.length}.filterNot { it ==  0 }
    }
}


