package hummel

import com.sun.jna.Native
import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

internal interface HNUser32 : StdCallLibrary {

	@Structure.FieldOrder("hdc", "fErase", "rcPaint", "fRestore", "fIncUpdate", "rgbReserved")
	open class PAINTSTRUCT : Structure() {
		@JvmField
		var hdc: HDC? = null

		@JvmField
		var fErase = false

		@JvmField
		var rcPaint: RECT? = null

		@JvmField
		var fRestore = false

		@JvmField
		var fIncUpdate = false

		@JvmField
		var rgbReserved = ByteArray(32)
	}

	fun BeginPaint(hWnd: HWND?, lpPaint: PAINTSTRUCT?): HDC?
	fun EndPaint(hWnd: HWND?, lpPaint: PAINTSTRUCT?): Boolean
	fun FillRect(hDC: HDC?, lprc: RECT?, hbr: HBRUSH?): Int

	companion object {
		val INSTANCE: HNUser32 = Native.load("user32", HNUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
	}
}