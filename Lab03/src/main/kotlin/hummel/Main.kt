package hummel

import com.sun.jna.Library
import com.sun.jna.Native

interface ExampleDLL : Library {
	companion object {
		val INSTANCE: ExampleDLL = Native.load("ExampleDLL", ExampleDLL::class.java)
	}

	fun add(a: Int, b: Int): Int
	fun sub(a: Int, b: Int): Int
	fun print(message: String?)
}

interface ReplacementDLL : Library {
	companion object {
		val INSTANCE: ReplacementDLL = Native.load("ReplacementDLL", ReplacementDLL::class.java)
	}

	fun replace(data: String?, replacement: String?)
}

fun main() {
	System.setProperty("jna.library.path", "lib")
	println("Result: ${ExampleDLL.INSTANCE.add(3, 2)}")
	println("Result: ${ExampleDLL.INSTANCE.sub(3, 2)}")
	ExampleDLL.INSTANCE.print("Hello, Hummel009!")

	ReplacementDLL.INSTANCE.replace("Hummel009's Process", "Hummel Turbamentum's Process")
}