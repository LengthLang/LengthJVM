package com.nailuj29gaming.lengthjvm

import com.sun.org.apache.bcel.internal.generic.ALOAD
import com.sun.org.apache.bcel.internal.generic.ICONST
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode
import org.objectweb.asm.*

import java.io.FileOutputStream
import java.io.IOException
import java.util.*

fun compile(program: List<Parser.Op>, name: String) {
   if (!verify(program)) {
      print("There would be a stackunderflow, so your program wasn't compiled")
      return
   }
   val cw = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
   cw.visit(
      Opcodes.V1_8,
      Opcodes.ACC_PUBLIC,
      name,
      null,
      "java/lang/Object",
      null
   )
   cw.visitSource("$name.len", null)

   val mv = cw.visitMethod(
      Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
      "main",
      "([Ljava/lang/String;)V",
      null, null
   )

   mv.visitCode()

   mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner")
   mv.visitInsn(Opcodes.DUP)
   mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      "java/lang/System",
      "in",
      "Ljava/io/InputStream;"
   )
   mv.visitMethodInsn(
      Opcodes.INVOKESPECIAL,
      "java/util/Scanner",
      "<init>",
      "(Ljava/io/InputStream;)V",
      false
   )
   mv.visitVarInsn(Opcodes.ASTORE, 1)
   val labels = mutableListOf<Label>()
   for (i in program) {
      val label = Label()
      labels.add(label)
   }
   labels.add(Label())
   program.forEachIndexed { i, instruction ->
      val label = labels[i]
      mv.visitLabel(label)
      mv.visitFrame(0, 0, null, 0, null);
      mv.visitLineNumber(i + 1, label)
      when (instruction) {
         Parser.Op.Input -> {
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitMethodInsn(
               Opcodes.INVOKEVIRTUAL,
               "java/util/Scanner",
               "next",
               "()Ljava/lang/String;",
               false
            )
            mv.visitInsn(Opcodes.ICONST_0)
            mv.visitMethodInsn(
               Opcodes.INVOKEVIRTUAL,
               "java/lang/String",
               "charAt",
               "(I)C",
               false
            )
         }
         Parser.Op.Add -> {
            mv.visitInsn(Opcodes.IADD)
         }
         Parser.Op.Sub -> {
            mv.visitInsn(Opcodes.ISUB)
         }
         Parser.Op.Cond -> {
            mv.visitJumpInsn(Opcodes.IFEQ, labels[i+2])
         }
         Parser.Op.Dup -> {
            mv.visitInsn(Opcodes.DUP)
         }
         Parser.Op.OutN -> {
            mv.visitFieldInsn(
               Opcodes.GETSTATIC,
               "java/lang/System",
               "out",
               "Ljava/io/PrintStream;"
            )
            mv.visitInsn(Opcodes.SWAP)
            mv.visitMethodInsn(
               Opcodes.INVOKEVIRTUAL,
               "java/io/PrintStream",
               "print",
               "(I)V",
               false
            )
         }
         else -> {
            if (instruction is Parser.Op.Push) {
               mv.visitLdcInsn(instruction.value)
            }
         }
      }
   }
   mv.visitLabel(labels.last())
   mv.visitInsn(Opcodes.RETURN)

   mv.visitMaxs(0, 0)
   mv.visitEnd()

   cw.visitEnd()

   val bytes = cw.toByteArray()
   try {
      val fos = FileOutputStream("$name.class")
      fos.write(bytes)
   } catch (ex: IOException) {
      ex.printStackTrace()
   }
}

private fun verify(program: List<Parser.Op>): Boolean {
   var stackHeight = 0
   var pc = 0
   while (pc < program.count()) {
      val op = program[pc]
      stackHeight += when (op) {
         Parser.Op.Add, Parser.Op.Sub, Parser.Op.Mul, Parser.Op.Div -> -2
         Parser.Op.Dup, Parser.Op.Cond, Parser.Op.Input -> 1
         Parser.Op.GotoS, Parser.Op.OutN, Parser.Op.OutA, Parser.Op.Cond -> -1
         else -> if (op is Parser.Op.Push) 1 else if (op is Parser.Op.GotoU) {
            pc = op.line
            -1
         } else {
            0
         }
      }
      pc++
      print(stackHeight)
      if (stackHeight < 0) return false
   }
   return true
}

