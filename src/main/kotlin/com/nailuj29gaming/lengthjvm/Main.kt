package com.nailuj29gaming.lengthjvm

fun main(args: Array<String>) {
    val program = listOf(
        Parser.Op.Input,
        Parser.Op.Push(0x30),
        Parser.Op.Sub,
        Parser.Op.Dup,
        Parser.Op.Cond,
        Parser.Op.OutN,
    )
    compile(program, "Hello")
}