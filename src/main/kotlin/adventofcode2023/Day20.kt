package adventofcode2023

import tool.mylambdas.substringBetween

fun main() {
    Day20(test=true).showResult()
}

class Day20(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val ruler = Ruler()
        val moduleList = inputLines.map{Module.of(it, ruler)}.associateBy { it.name }
        moduleList.values.forEach { module ->
            module.initSendToModuleList(module.sendTo.map { moduleList[it]!! })
        }
        moduleList.forEach { name, module ->
            module.initReceiveFromModuleList(moduleList.values.filter { it.sendTo.contains(name) })
        }
        val button = Button(ruler)
        button.initSendToModuleList(listOf(moduleList["broadcaster"]!!))
        button.push()

        return moduleList.values
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }
}

//======================================================================================================================

enum class PulseType { HIGH, LOW}

class Ruler{
    val queue = ArrayDeque<Module>()

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
        return "$name -> ${receiveFromModuleList.joinToString(", "){it.name}}"
    }

    abstract fun receive(from: Module, pulse: PulseType)
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

    override fun receive(from: Module, pulse: PulseType) {
        sendToModuleList.forEach { it.receive(this, pulse) }
    }


}

class FlipFlop(name: String, sendTo: List<String>, ruler: Ruler): Module(name, sendTo, ruler) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String, ruler: Ruler): Broadcaster {
            return Broadcaster(
                name = raw.substringBetween("%", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
                ruler = ruler
            )
        }
    }

    private var isOn : Boolean = false
    override fun receive(from: Module, pulse: PulseType) {
        if (pulse == PulseType.LOW) {
            isOn = !isOn
            if (isOn) {
                sendToModuleList.forEach { it.receive(this, PulseType.HIGH) }
            } else {
                sendToModuleList.forEach { it.receive(this, PulseType.LOW) }
            }
        }
    }


}

class Conjunction(name: String, sendTo: List<String>, ruler: Ruler): Module(name, sendTo, ruler) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String, ruler: Ruler): Broadcaster {
            return Broadcaster(
                name = raw.substringBetween("&", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
                ruler = ruler
            )
        }
    }

    private val remember = mutableMapOf<Module, PulseType>()
    override fun receive(from: Module, pulse: PulseType) {
        remember[from] = pulse
        if (receiveFromModuleList.all{module -> remember.getOrDefault(module, PulseType.LOW) == PulseType.HIGH}) {
            sendToModuleList.forEach { it.receive(this, PulseType.LOW) }
        } else {
            sendToModuleList.forEach { it.receive(this, PulseType.HIGH) }
        }
    }
}

class Button(ruler: Ruler): Module("Button", listOf("broadcaster"), ruler) {
    override fun receive(from: Module, pulse: PulseType) {
        //do nothing
        throw Exception ("Button cannot receive anything")
    }

    fun push() {
        sendToModuleList.first().receive(this, PulseType.LOW)
    }

}
