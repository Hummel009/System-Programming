package hummel

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference


interface ExampleLibrary : Library {
	fun add(a: Int, b: Int): Int
	fun sub(a: Int, b: Int): Int
	fun print(message: String?)
	fun replaceString(target: String?, search: String?, replace: String?)

	companion object {
		val INSTANCE: ExampleLibrary = Native.load("mylibrary", ExampleLibrary::class.java)
	}
}

fun main() {
	System.setProperty("jna.library.path", "lib")
	println("Result: ${ExampleLibrary.INSTANCE.add(3, 2)}")
	println("Result: ${ExampleLibrary.INSTANCE.sub(3, 2)}")
	ExampleLibrary.INSTANCE.print("Hello, Hummel009!")

	val originalString = "This is a test string. Test."
	ExampleLibrary.INSTANCE.replaceString(originalString, "Test", "Example")

	println("Updated string: $originalString")
}