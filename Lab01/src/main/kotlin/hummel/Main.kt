package hummel

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.WString


object HelloWorldWinAPI {
	@JvmStatic
	fun main(args: Array<String>) {
		// Вызов MessageBox для вывода сообщения "Hello, World!"
		val result = User32.INSTANCE.MessageBoxW(
			0,
			WString("Hello, World!"),
			WString("MessageBox Title"),
			User32.MB_OK or User32.MB_ICONINFORMATION
		)

		// Проверка результата вызова
		if (result == 0) {
			System.err.println("Ошибка вызова MessageBox")
		}
	}

	// Определение библиотеки User32.dll с помощью JNA
	interface User32 : Library {
		fun MessageBoxW(hwnd: Int, text: WString?, caption: WString?, type: Int): Int

		companion object {
			val INSTANCE = Native.load(
				"user32",
				User32::class.java
			) as User32
			const val MB_OK = 0x00000000
			const val MB_ICONINFORMATION = 0x00000040
		}
	}
}