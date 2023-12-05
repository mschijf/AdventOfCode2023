package adventofcode2023

import tool.mylambdas.splitByCondition
import tool.mylambdas.substringBetween
import kotlin.math.min

fun main() {
    Day05(test=false).showResult()
}

// Bruteforce does work...
//   Result part 1: 388071289 (after 0.001 sec)
//   Result part 2: 84206669 (after 525.693 sec)
//                 218728775

// 0..105449248
//    218728775

class Day05(test: Boolean) : PuzzleSolverAbstract(test) {

    val seedList = inputLines().first().substringAfter("seeds: ").split("\\s+".toRegex()).map{it.toLong()}
    val seedRanges = seedList.chunked(2).map{it[0]..it[0]+it[1]-1}
    val transformerList = inputLines().drop(2).splitByCondition { it.isEmpty() }.map{Transformer.of(it)}
    val transformerMap = transformerList.associateBy { it.source }

//    override fun resultPartOne(): Any {
//        return seedList.map{seedToLocation(it)}.min()
//    }

    override fun resultPartTwo(): Any {
        var nextRanges = if (test)
            listOf((0L..55))
        else
            listOf(transformerMap["humidity"]!!.transformNumberList.first { 0 in it.destinationRange }!!.sourceRange)

        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["temperature"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["light"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["water"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["fertilizer"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["soil"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })
        nextRanges = nextRanges.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["seed"]!!.transformNumberList )}
        println(nextRanges.sortedBy { it.first })

        var smallest = Long.MAX_VALUE
        nextRanges.forEach { seedRange ->
            var i = seedRange.first
            while (i <= seedRange.last) {
                if (i.inRanges(seedRanges))
                    smallest = min(smallest, seedToLocation(i))
                i++
            }
        }
        return smallest

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
//            print("$current $currentNumber")
            currentNumber = transformerMap[current]!!.transform(currentNumber)
            current = transformerMap[current]!!.destination
//            println(" --> $currentNumber")
        }
//        println("$current $currentNumber")
        return currentNumber
    }
}

fun Long.inRanges(checkRanges: List<LongRange>) : Boolean {
    return checkRanges.any{range -> this in range}
}

fun destinationRangesToSource(inRange: LongRange, compareRanges: List<TransformNumber>): List<LongRange> {
    val result = mutableListOf<LongRange>()
    var start = inRange.first
    var end = inRange.last
    compareRanges.forEach {compareRange ->
        if (start <= end && start in compareRange.destinationRange && end in compareRange.destinationRange) {
            result.add(compareRange.sourceRange.first + (start-compareRange.destinationRange.first)..compareRange.sourceRange.first + (end-compareRange.destinationRange.first))
            return result
        }
        if (start <= end && start in compareRange.destinationRange) {
            result.add(compareRange.sourceRange.first + (start-compareRange.destinationRange.first)..compareRange.sourceRange.last)
            start = compareRange.destinationRange.last+1
        }
        if (start <= end && end in compareRange.destinationRange) {
            result.add(compareRange.sourceRange.first ..compareRange.sourceRange.first + (end-compareRange.destinationRange.first))
            end = compareRange.destinationRange.first-1
        }
    }
    if (start <= end)
        result.add(start..end)
    return result
}


data class Transformer(val source: String, val destination: String, val transformNumberList: List<TransformNumber>) {
    fun transform(from: Long) : Long {
        val transformNumber = transformNumberList.firstOrNull { from in it.sourceRange } ?: TransformNumber(from..from, from ..from)
        return transformNumber.destinationRange.first + (from - transformNumber.sourceRange.first)
    }

    companion object {
        fun of(raw: List<String>): Transformer {
            return Transformer(
                source = raw.first().substringBefore("-"),
                destination = raw.first().substringBetween("to-", " map:"),
                transformNumberList = raw.drop(1).map { TransformNumber.of(it) }
            )
        }
    }
}

data class TransformNumber (val sourceRange: LongRange, val destinationRange: LongRange) {
    companion object {
        fun of(raw: String): TransformNumber {
            val list = raw.split("\\s+".toRegex()).map {it.toLong() }
            val sourceStart = list[1]
            val destinationStart = list[0]
            val length = list[2]
            return TransformNumber(
                sourceRange = sourceStart..sourceStart+length-1,
                destinationRange = destinationStart .. destinationStart+length-1
            )
        }
    }
}