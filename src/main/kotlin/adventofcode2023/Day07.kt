package adventofcode2023

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {

    private val handList= inputLines.map{ Hand.of(it) }

    override fun resultPartOne(): Any {
        return handList
            .sortedWith(compareBy { it.handValue() })
            .mapIndexed { index, hand -> (index+1) * hand.bid }
            .sum()
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }


}

data class Hand(val cardsOrdered: List<Char>, val cards: List<Char>, val cardListValue: Int, val bid: Int) {
    companion object {
        fun of (raw: String): Hand {
            val cardList = raw.substringBefore(" ").toList()
            return Hand(
                cardsOrdered=cardList.sortedWith(compareBy{cardValue(it)}),
                cards=cardList,
                cardListValue = cardList.cardListValue(),
                bid = raw.substringAfter(" ").toInt()
            )
        }

        //A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
        private fun cardValue(card: Char): Int {
            return when(card) {
                'A' -> 14
                'K' -> 13
                'Q' -> 12
                'J' -> 11
                'T' -> 10
                else -> card - '0'
            }
        }

        private fun List<Char>.cardListValue(): Int {
            var result = 0
            this.forEach {
                result = result * 15 + cardValue(it)
            }
            return result
        }

    }


    fun handValue(): Int {
        return when  {
            cardsOrdered.fiveOfAKind() -> 6_000_000 + cardListValue
            cardsOrdered.fourOfAKind() -> 5_000_000 + cardListValue
            cardsOrdered.fullHouse() -> 4_000_000 + cardListValue
            cardsOrdered.threeOfAKind() -> 3_000_000 + cardListValue
            cardsOrdered.twoPair() -> 2_000_000 + cardListValue
            cardsOrdered.onePair() -> 1_000_000 + cardListValue
            cardsOrdered.highCard() -> 0 + cardListValue
            else -> throw Exception ("not possible")
        }
    }

    fun List<Char>.fiveOfAKind(): Boolean {
        return (this.distinct().count() == 1) && (
                this[0] == this[1] && this[0] == this[2] && this[0] == this[3] && this[0] == this[4]
                )
    }

    fun List<Char>.fourOfAKind(): Boolean {
        return (this.distinct().count() == 2) && (
                (this[0] == this[1] && this[0] == this[2] && this[0] == this[3]) ||
                (this[1] == this[2] && this[1] == this[3] && this[1] == this[4])
                )
    }

    fun List<Char>.fullHouse(): Boolean {
        return (this.distinct().count() == 2) && (
                (this[0] == this[1] && this[2] == this[3] && this[2] == this[4]) ||
                        (this[0] == this[1] && this[0] == this[2] && this[3] == this[4])
                )
    }

    fun List<Char>.threeOfAKind(): Boolean {
        return (this.distinct().count() == 3) && (
                (this[0] == this[1] && this[0] == this[2]) ||
                (this[1] == this[2] && this[1] == this[3]) ||
                (this[2] == this[3] && this[2] == this[4])
        )
    }

    fun List<Char>.twoPair(): Boolean {
        return (this.distinct().count() == 3) && (
                (this[0] == this[1] && this[2] == this[3]) ||
                        (this[0] == this[1] && this[3] == this[4]) ||
                        (this[1] == this[2] && this[3] == this[4])
                )
    }

    fun List<Char>.onePair(): Boolean {
        return (this.distinct().count() == 4) && (
                (this[0] == this[1]) ||
                        (this[1] == this[2]) ||
                        (this[2] == this[3])  ||
                        (this[3] == this[4])
                )
    }

    fun List<Char>.highCard(): Boolean {
        return (this.distinct().count() == 5)
    }

}


