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




//    /** The condition record of a spring. If unknown, can be assumed to be either of the other two. */
//    private enum class Condition { Operational, Damaged, Unknown }
//
//    /**
//     * An entry in the spring ledger.
//     * @property springs  The condition of the springs in this entry.
//     * @property checksum The number and size of the runs of damaged springs.
//     */
//    private data class ConditionRecord(val springs: List<Condition>, val checksum: List<Int>) {
//
//        /**
//         * The number of valid solutions tanking into account [Unknown] spring conditions, if any.
//         * If there are no unknown [springs], this value will be 1 if the [checksum] is valid, or 0 otherwise.
//         */
//        fun validSolutions(cache: MutableMap<ConditionRecord, Long>): Long {
//            // If the valid solution has already been computed, reuse it.
//            cache[this]?.let { return it }
//
//            // If no springs left, either no checksums left to satisfy, or the record is invalid.
//            if (springs.isEmpty()) return if (checksum.isEmpty()) 1 else 0
//
//            // If some springs left, but no checksums, then all remaining springs must be operational.
//            // Unknowns can be assumed as operational, but if any known damaged left, the record is invalid.
//            if (checksum.isEmpty()) return if (springs.contains(Condition.Damaged)) 0 else 1
//
//            val spring = springs.first()
//            val expectedDamaged = checksum.first()
//
//            // If the next spring could be operational, then nothing to check, just ignore it.
//            val solutionsIfOperational =
//                if (spring != Condition.Damaged) ConditionRecord(springs.drop(1), checksum).validSolutions(cache)
//                else 0
//
//            // If the next spring could be damaged, check that all conditions for a valid damaged streak are met:
//            // - There must be at least as many springs left as the next expected checksum value.
//            // - The next checksum value's worth of springs must all be (or can be disambiguated into being) damaged.
//            // - After the streak, either there are no springs left to check, or the streak is properly ended by an
//            //   operational spring (or one that can be disambiguated as such).
//            // If these conditions are met, then we should skip the current damaged streak (and maybe its terminator)
//            // entirely, satisfy the checksum, and process the remaining springs.
//            val solutionsIfSpringDamaged = when {
//                spring == Condition.Operational -> 0
//                expectedDamaged > springs.size -> 0
//                springs.take(expectedDamaged).any { it == Condition.Operational } -> 0
//                springs.size != expectedDamaged && springs[expectedDamaged] == Condition.Damaged -> 0
//                else -> ConditionRecord(springs.drop(expectedDamaged + 1), checksum.drop(1)).validSolutions(cache)
//            }
//
//            return solutionsIfOperational
//                .plus(solutionsIfSpringDamaged)
//                .also { result -> cache[this] = result }
//        }
//    }
//
//    /**
//     * Parse the [input] and return the list of condition records.
//     * @param input  The puzzle input.
//     * @param unfold Whether to unfold the records before reading them.
//     */
//    private fun parseInput(input: String, unfold: Boolean): List<ConditionRecord> = parse {
//        val lineRegex = Regex("""^([#.?]+) (\d[\d,]*\d)$""")
//
//        fun String.unfold(separator: Char): String = buildString(length.inc() + 5) {
//            val original = this@unfold
//            append(original)
//            repeat(4) { append(separator, original) }
//        }
//
//        input
//            .lineSequence()
//            .map { lineRegex.matchEntire(it)!!.destructured }
//            .map { (record, checksum) ->
//                if(unfold) record.unfold('?') to checksum.unfold(',')
//                else record to checksum
//            }
//            .map { (record, checksum) ->
//                ConditionRecord(
//                    springs = record.map {
//                        when(it) {
//                            '.' -> Condition.Operational
//                            '#' -> Condition.Damaged
//                            '?' -> Condition.Unknown
//                            else -> error("Impossible state")
//                        }
//                    },
//                    checksum = checksum.split(',').map(String::toInt),
//                )
//            }
//            .toList()
//    }
//
//    private fun List<ConditionRecord>.solve(): Long {
//        val cache: MutableMap<ConditionRecord, Long> = mutableMapOf()
//        return sumOf { it.validSolutions(cache) }
//    }
