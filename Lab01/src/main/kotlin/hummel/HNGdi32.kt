package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinDef.HBRUSH
import com.sun.jna.win32.StdCallLibrary

internal interface HNGdi32 : StdCallLibrary {
	fun CreateSolidBrush(color: DWORD?): HBRUSH?

	companion object {
		val INSTANCE: HNGdi32 = Native.load("gdi32", HNGdi32::class.java)
	}
}