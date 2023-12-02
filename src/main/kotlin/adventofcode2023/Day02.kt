package adventofcode2023

import tool.mylambdas.substringBetween
import kotlin.math.max

fun main() {
    Day02(test=false).showResult()
}

class Day02(test: Boolean) : PuzzleSolverAbstract(test) {

    private val gameList = inputLines.map{Game.of(it)}

    //12 red cubes, 13 green cubes, and 14 blue cubes
    override fun resultPartOne(): Any {
        val possibilityCheck = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return gameList
            .filter { game -> game.isPossibleFor(possibilityCheck) }
            .sumOf { it.id }
    }

    override fun resultPartTwo(): Any {
        return gameList
            .sumOf { it.power() }
    }
}


data class Game(val id: Int, val grabList: List<Grab>) {
    companion object {
        //Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        fun of(raw: String) =
            Game (
                id = raw.substringBetween("Game", ":").trim().toInt(),
                grabList = raw.substringAfter(": ").split("; ").map{Grab.of(it)}
            )
    }

    fun isPossibleFor(configuration: Map<String, Int>)=
        grabList.all {it.isPossibleFor(configuration)}

    private fun minimalSet(): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        grabList.forEach { aGrab ->
            aGrab.cubeCountPerColor.forEach { (color, colorCount) ->
                val currentMinimum = result.getOrDefault(color, 0L)
                result[color] = max(currentMinimum, colorCount.toLong())
            }
        }
        return result
    }

    fun power(): Long {
        return minimalSet().values.reduce { acc, l -> acc*l }
    }


}

data class Grab(val cubeCountPerColor: Map<String, Int>) {
    companion object {
        //3 blue, 4 red
        fun of(raw: String) =
            Grab (
                cubeCountPerColor = raw
                    .split(", ")
                    .associate{it.substringAfter(" ") to it.substringBefore(" ").trim().toInt()}
            )
    }

    fun isPossibleFor(configuration: Map<String, Int>) =
        cubeCountPerColor.none { (color, colorCount) -> configuration.getOrDefault(color, -1) < colorCount }
}