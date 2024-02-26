package net.botwithus

import net.botwithus.rs3.script.ScriptConsole

class DisassembleTask(var amountToDisassemble: Int, var itemToDisassemble: String) {

    private var amountDisassembled: Int = 0

    init {
        ScriptConsole.println("DisassembleTask created")
    }

    fun isComplete(): Boolean {
        return amountDisassembled >= amountToDisassemble
    }

    fun incrementAmountDisassembled() {
        amountDisassembled++
    }

    fun getAmountDisassembled(): Int {
        return amountDisassembled
    }

}