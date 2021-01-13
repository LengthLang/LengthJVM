package com.nailuj29gaming.lengthjvm

class Parser(text: String) {
    private val program = mutableListOf<Int>()

    sealed class Op {
        object Input : Op()
        object Add : Op()
        object Sub : Op()
        object Dup : Op()
        object Cond : Op()
        data class GotoU(
            val line: Int
        ) : Op()
        object OutN : Op()
        object OutA : Op()
        object Mul : Op()
        object Div : Op()
        object GotoS : Op()
        data class Push(
            val value: Int
        ) : Op()
        object None : Op()
    }

    public val parsed: List<Op>
        get() {
            val result = mutableListOf<Op>()
            var i = 0
            while (i < program.count()) {
                result += when (program[i]) {
                    9 -> Op.Input
                    10 -> Op.Add
                    11 -> Op.Sub
                    12 -> Op.Dup
                    13 -> Op.Cond
                    14 -> Op.GotoU(program[++i])
                    15 -> Op.OutN
                    16 -> Op.OutA
                    20 -> Op.Mul
                    21 -> Op.Div
                    24 -> Op.GotoS
                    25 -> Op.Push(program[++i])
                    else -> Op.None
                }
                i++
            }
            return result
        }

    init {
        for (line in text.split("\n")) {
            program += line.length
        }
    }
}