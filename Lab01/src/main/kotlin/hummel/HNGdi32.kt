package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.WinDef.*

internal interface HNGdi32 : GDI32 {
	fun CreateSolidBrush(color: DWORD?): HBRUSH?

	companion object {
		val INSTANCE: HNGdi32 = Native.load("gdi32", HNGdi32::class.java)
	}
}