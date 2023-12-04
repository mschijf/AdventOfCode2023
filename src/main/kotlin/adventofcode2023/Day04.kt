package adventofcode2023

import tool.mylambdas.substringBetween
import kotlin.math.min

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test) {

    private val cardList = inputLines().map{Card.of(it)}
    override fun resultPartOne(): Any {
        return cardList.map { it.determineWinning() }.sum()
    }

    override fun resultPartTwo(): Any {
        val countCards = Array<Int>(cardList.size+1){1}
        countCards[0] = 0
        cardList.forEach { card ->
            for (i in 1..min(card.winningNumbers(), card.id+cardList.size)) {
                countCards[card.id+i] += countCards[card.id]
            }
        }

        return countCards.toList().sum()
    }
}

data class Card(val id: Int, val winning: List<Int>, val having: List<Int>) {

    fun winningNumbers(): Int {
        return winning.toSet().intersect(having.toSet()).count()
    }

    fun determineWinning(): Long {
        val winningNumbers = winningNumbers()
        return if (winningNumbers > 0) 1L shl (winningNumbers-1) else 0
    }


    companion object {
        //Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        fun of(raw: String) =
            Card(
                id = raw.substringBetween("Card ", ": ").trim().toInt(),
                winning = raw.substringBetween(": ", " | ").split(" ").filter { it.isNotEmpty() }.map{it.trim().toInt()},
                having = raw.substringAfter( " | ").split(" ").filter { it.isNotEmpty() }.map{it.trim().toInt()}
            )
    }
}
