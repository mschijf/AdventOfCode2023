package adventofcode2023

import tool.mylambdas.splitByCondition
import tool.mylambdas.substringBetween
import kotlin.math.min

fun main() {
    Day05(test=true).showResult()
}

// Bruteforce does work...
//   Result part 1: 388071289 (after 0.001 sec)
//   Result part 2: 84206669 (after 525.693 sec)

class Day05(test: Boolean) : PuzzleSolverAbstract(test) {

    val seedList = inputLines().first().substringAfter("seeds: ").split("\\s+".toRegex()).map{it.toLong()}
    val seedRanges = seedList.chunked(2).map{it[0]..it[0]+it[1]-1}
    val transformerList = inputLines().drop(2).splitByCondition { it.isEmpty() }.map{Transformer.of(it)}
    val transformerMap = transformerList.associateBy { it.source }

    override fun resultPartOne(): Any {
        return seedList.map{seedToLocation(it)}.min()
    }

    override fun resultPartTwo(): Any {
        return partTwoBruteforce()
    }

    private fun partTwoBruteforce(): Any {
        var smallest = Long.MAX_VALUE

        seedRanges.forEach { seedRange ->
            var i = seedRange.first
            while (i <= seedRange.last) {
                smallest = min(smallest, seedToLocation(i))
                i++
            }
        }
        return smallest
    }


    private fun seedToLocation(seedNumber: Long): Long {
        var current = "seed"
        var currentNumber = seedNumber
        while (transformerMap.contains(current)) {
            currentNumber = transformerMap[current]!!.transform(currentNumber)
            current = transformerMap[current]!!.destination
        }
        return currentNumber
    }
}

fun newCandidateRanges(inRange: LongRange, compareRanges: List<LongRange>): List<LongRange> {
    val result = mutableListOf<LongRange>()
    var start = inRange.first
    var end = inRange.last
    compareRanges.forEach {compareRange ->
        if (start in compareRange && end in compareRange) {
            result.add(start..end)
            return result
        }
        if (start in compareRange) {
            result.add(start..compareRange.last)
            start = compareRange.last+1
        }
        if (end in compareRange) {
            result.add(compareRange.first..end)
            end = compareRange.first-1
        }
    }
    return result
}


data class Transformer(val source: String, val destination: String, val transformNumberList: List<TransformNumber>) {
    fun transform(from: Long) : Long {
        val transformNumber = transformNumberList.firstOrNull { from in it.sourceRange } ?: TransformNumber(from..from, from)
        return transformNumber.destinationStart + (from - transformNumber.sourceRange.first)
    }

    companion object {
        fun of(raw: List<String>) = Transformer (
            source = raw.first().substringBefore("-"),
            destination = raw.first().substringBetween("to-", " map:"),
            transformNumberList = raw.drop(1).map{TransformNumber.of(it)}
        )
    }
}

data class TransformNumber (val sourceRange: LongRange, val destinationStart: Long) {
    companion object {
        fun of(raw: String): TransformNumber {
            val list = raw.split("\\s+".toRegex()).map {it.toLong() }
            val sourceStart = list[1]
            val destinationStart = list[0]
            val length = list[2]
            return TransformNumber(
                sourceRange = sourceStart..sourceStart+length-1,
                destinationStart
            )
        }
    }
}