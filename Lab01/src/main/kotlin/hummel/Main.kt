package hummel

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.WString


object HelloWorldWinAPI {
	@JvmStatic
	fun main(args: Array<String>) {
		// Вызов MessageBox для вывода сообщения "Hello, World!"
		User32.INSTANCE.MessageBoxW(
			null, WString("Hello, World!"), WString("MessageBox Title"), User32.MB_OK or User32.MB_ICONINFORMATION
		)
	}

	// Определение библиотеки User32.dll с помощью JNA
	interface User32 : Library {
		fun MessageBoxW(hwnd: Any?, text: WString?, caption: WString?, type: Int): Int

		companion object {
			val INSTANCE: User32 = Native.load(
				"user32", User32::class.java
			) as User32
			const val MB_OK: Int = 0x00000000
			const val MB_ICONINFORMATION: Int = 0x00000040
		}
	}
}