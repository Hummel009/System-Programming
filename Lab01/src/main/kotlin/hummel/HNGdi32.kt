package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HBRUSH
import com.sun.jna.win32.StdCallLibrary

internal interface HNGdi32 : StdCallLibrary {
	fun CreateSolidBrush(color: WinDef.DWORD?): HBRUSH?

	fun GetStockObject(i: Int): HBRUSH

	companion object {
		val INSTANCE: HNGdi32 = Native.load("gdi32", HNGdi32::class.java)
		var WHITE_BRUSH = 0
	}
}