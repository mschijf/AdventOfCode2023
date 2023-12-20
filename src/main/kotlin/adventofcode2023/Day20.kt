package adventofcode2023

import tool.mylambdas.substringBetween

fun main() {
    Day20(test=false).showResult()
}

class Day20(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val ruler = Ruler()
        val moduleList1 = inputLines.map { Module.of(it, ruler) }.associateBy { it.name }
        val unknown = moduleList1.values.flatMap{it.sendTo}.filter{it !in moduleList1}
        val moduleList = moduleList1 + unknown.map{it to UnknownModule(it)}
        moduleList.values.forEach { module ->
            module.initSendToModuleList(module.sendTo.map { moduleList[it]!! })
        }
        moduleList.forEach { name, module ->
            module.initReceiveFromModuleList(moduleList.values.filter { it.sendTo.contains(name) })
        }
        var ch = 0L
        var cl = 0L
        repeat(1000) {
            val (cnl, cnh) = moduleList.pushButton()
            ch += cnh
            cl += cnl
        }

        return ch*cl
    }

    fun Map<String, Module>.pushButton(): Pair<Long, Long> {
        var countLow = 1L
        var countHigh = 0L
        val queue = ArrayDeque<Message>()
        val all = this["broadcaster"]!!.receive(this["broadcaster"]!!, PulseType.LOW)
        queue.addAll(all)
        while (queue.isNotEmpty()) {
            val message = queue.removeFirst()
            if (message.pulse == PulseType.LOW)
                countLow++
            else
                countHigh++

            val all = message.to.receive(message.from, message.pulse)
            queue.addAll(all)
        }
        return Pair(countLow, countHigh)
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }
}

//======================================================================================================================

enum class PulseType { HIGH, LOW}

class Ruler{
}

abstract class Module(val name: String, val sendTo: List<String>, val ruler: Ruler) {
    companion object {
        fun of(raw: String, ruler: Ruler): Module {
            when (raw.first()) {
                '%' -> return FlipFlop.of(raw, ruler)
                '&' -> return Conjunction.of(raw, ruler)
                'b' -> return Broadcaster.of(raw, ruler)
                else -> throw Exception("Unexpected input")
            }
        }
    }

    protected var sendToModuleList: List<Module> = emptyList()
    fun initSendToModuleList(aList : List<Module>) {
        sendToModuleList = aList
    }

    protected var receiveFromModuleList: List<Module> = emptyList()
    fun initReceiveFromModuleList(aList : List<Module>) {
        receiveFromModuleList = aList
    }

    override fun toString(): String {
        return "$name(${type()}) -> ${sendToModuleList.joinToString(", "){it.name}}"
    }

    abstract fun receive(from: Module, pulse: PulseType): List<Message>
    abstract fun type(): String
}

class Broadcaster(name: String, sendTo: List<String>, ruler:Ruler): Module(name, sendTo, ruler) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String, ruler: Ruler): Broadcaster {
            return Broadcaster(
                name = raw.substringBefore(" ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
                ruler = ruler
            )
        }
    }

    override fun receive(from: Module, pulse: PulseType): List<Message> {
        return sendToModuleList.map { Message(this, it, pulse) }
    }

    override fun type(): String {
        return "BC"
    }

}

class FlipFlop(name: String, sendTo: List<String>, ruler: Ruler): Module(name, sendTo, ruler) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String, ruler: Ruler): FlipFlop {
            return FlipFlop(
                name = raw.substringBetween("%", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
                ruler = ruler
            )
        }
    }

    private var isOn : Boolean = false
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        if (pulse == PulseType.LOW) {
            isOn = !isOn
            if (isOn) {
                return sendToModuleList.map { Message(this, it, PulseType.HIGH) }
            } else {
                return sendToModuleList.map { Message(this, it, PulseType.LOW) }
            }
        }
        return emptyList()
    }

    override fun type(): String {
        return "FF"
    }
}

class Conjunction(name: String, sendTo: List<String>, ruler: Ruler): Module(name, sendTo, ruler) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String, ruler: Ruler): Conjunction {
            return Conjunction (
                name = raw.substringBetween("&", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
                ruler = ruler
            )
        }
    }

    private val remember = mutableMapOf<Module, PulseType>()
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        remember[from] = pulse
        if (receiveFromModuleList.all{module -> remember.getOrDefault(module, PulseType.LOW) == PulseType.HIGH}) {
            return sendToModuleList.map { Message(this, it, PulseType.LOW) }
        } else {
            return sendToModuleList.map { Message(this, it, PulseType.HIGH) }
        }
    }

    override fun type(): String {
        return "CJ"
    }

}

class UnknownModule(name: String): Module(name, emptyList<String>(), Ruler()) {
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        return emptyList()
    }

    override fun type(): String {
        return "UN"
    }

}


data class Message(val from: Module, val to:Module, val pulse: PulseType) {
    override fun toString(): String {
        return "${from.name} (${from.type()}} -- $pulse --> ${to.name} (${to.type()}}"
    }
}
