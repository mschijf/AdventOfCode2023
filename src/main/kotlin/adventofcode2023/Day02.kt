package adventofcode2023

import tool.mylambdas.substringBetween
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day02(test=false).showResult()
}

//49 is wrong

class Day02(test: Boolean) : PuzzleSolverAbstract(test) {

    private val gameList = inputLines.map{Game.of(it)}

    //12 red cubes, 13 green cubes, and 14 blue cubes
    override fun resultPartOne(): Any {
        return gameList
            .filter { it.isPossibleFor(mapOf("red" to 12, "green" to 13, "blue" to 14)) }
            .sumOf { it.id }
    }

    override fun resultPartTwo(): Any {
        return gameList.sumOf { it.power() }
    }
}


data class Game(val id: Int, val revealedList: List<Reveal>) {
    companion object {
        //Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        fun of(raw: String): Game {
            val id = raw.substringBetween("Game", ":").trim().toInt()
            val ll = raw.substringAfter(": ").split("; ").map{Reveal.of(it)}
            return Game(id, ll)
        }
    }

    fun isPossibleFor(configuration: Map<String, Int>)=
        revealedList.all {it.isPossibleFor(configuration)}

    private fun minimalSet(): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        revealedList.forEach { revealed ->
            revealed.revealed.forEach {(color, colorCount) ->
                val currentMinimum = result.getOrDefault(color, 0)
                result.put(color, max(currentMinimum.toLong(), colorCount.toLong()))
            }
        }
        return result
    }

    fun power(): Long {
        return minimalSet().values.reduce { acc, l -> acc*l }
    }
}

data class Reveal(val revealed: List<Pair<String, Int>>) {
    companion object {
        //3 blue, 4 red
        fun of(raw: String): Reveal {
            val ll = raw.split(", ").map{Pair(it.substringAfter(" "), it.substringBefore(" ").trim().toInt())}
            return Reveal(ll)
        }
    }

    fun isPossibleFor(configuration: Map<String, Int>): Boolean {
        revealed.forEach { (color, colorCount) ->
            if (configuration.getOrDefault(color, -1) < colorCount)
                return false
        }
        return true
    }
}