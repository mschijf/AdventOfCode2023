package adventofcode2023

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test, hasInputFile = true) {


    override fun resultPartOne(): Any {
        val handList= inputLines.map{ Hand.of(it, false) }
        return handList
            .sortedWith(compareBy { it.handValue() })
            .mapIndexed { index, hand -> (index+1) * hand.bid }
            .sum()
    }

    override fun resultPartTwo(): Any {
        val handList= inputLines.map{ Hand.of(it, true) }
        return handList
            .sortedWith(compareBy { it.valueWithJokers() })
            .mapIndexed { index, hand -> (index+1) * hand.bid }
            .sum()
    }


}

data class Hand(val cardsOrdered: List<Char>, val cards: List<Char>, val cardListValue: Int, val bid: Int) {
    companion object {
        fun of (raw: String, useJoker: Boolean): Hand {
            val cardList = raw.substringBefore(" ").toList()
            return Hand(
                cardsOrdered=cardList.sortedWith(compareBy{cardValue(it, useJoker)}),
                cards=cardList,
                cardListValue = cardList.cardListValue(useJoker),
                bid = raw.substringAfter(" ").toInt(),
            )
        }

        //A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
        fun cardValue(card: Char, useJoker: Boolean): Int {
            return when(card) {
                'A' -> 14
                'K' -> 13
                'Q' -> 12
                'J' -> if (useJoker) 0 else 11
                'T' -> 10
                else -> card - '0'
            }
        }

        private fun List<Char>.cardListValue(useJoker: Boolean): Int {
            var result = 0
            this.forEach {
                result = result * 15 + cardValue(it, useJoker)
            }
            return result
        }

    }

    fun valueWithJokers(currentHand: String="", index: Int=0): Int {
        if (index > 4) {
            return currentHand.toList().sortedWith(compareBy{cardValue(it, true)}).localHandValue()
        }
        val value = if (cardsOrdered[index] == 'J') {
            listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2').maxOf{
                valueWithJokers(currentHand+it, index+1)
            }
        } else {
            valueWithJokers(currentHand+cardsOrdered[index], index+1)
        }
        return value
    }

    private fun List<Char>.localHandValue(): Int {
        return when  {
            this.fiveOfAKind() -> 6_000_000 + cardListValue
            this.fourOfAKind() -> 5_000_000 + cardListValue
            this.fullHouse() -> 4_000_000 + cardListValue
            this.threeOfAKind() -> 3_000_000 + cardListValue
            this.twoPair() -> 2_000_000 + cardListValue
            this.onePair() -> 1_000_000 + cardListValue
            this.highCard() -> 0 + cardListValue
            else -> throw Exception ("not possible")
        }
    }

    fun handValue(): Int {
        return cardsOrdered.localHandValue()
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


