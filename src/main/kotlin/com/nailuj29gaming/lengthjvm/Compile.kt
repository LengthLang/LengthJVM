package com.nailuj29gaming.lengthjvm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.FileOutputStream
import java.io.IOException

fun compile(program: List<Parser.Op>, name: String) {
   val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
   cw.visit(
      Opcodes.V1_8,
      Opcodes.ACC_PUBLIC,
      name,
      null,
      "java/lang/Object",
      null
   )

   val mv = cw.visitMethod(
      Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
      "main",
      "([Ljava/lang/String;)V",
      null, null
   )

   mv.visitCode()
   mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      "java/lang/System",
      "out",
      "Ljava/io/PrintStream;"
   )
   mv.visitLdcInsn("Hello, World")
   mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      "java/io/PrintStream",
      "println",
      "(Ljava/lang/String;)V",
      false
   )
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
