package com.nailuj29gaming.lengthjvm

fun main(args: Array<String>) {
    compile(listOf(
        Parser.Op.Input,
        Parser.Op.Push(0x30),
        Parser.Op.Sub,
        Parser.Op.Cond,
        Parser.Op.OutN,
        Parser.Op.Push(0)
    ), "Hello")
}