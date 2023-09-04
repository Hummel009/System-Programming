package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.WinDef.*

internal interface ExGDI32 : GDI32 {
	fun CreateSolidBrush(color: DWORD?): HBRUSH?

	companion object {
		val INSTANCE: ExGDI32 = Native.load("gdi32", ExGDI32::class.java)
	}
}