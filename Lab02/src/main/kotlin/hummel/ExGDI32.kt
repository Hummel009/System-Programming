package hummel

import com.sun.jna.Native
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.WinDef.*

internal interface ExGDI32 : GDI32 {
	companion object {
		val INSTANCE: ExGDI32 = Native.load("gdi32", ExGDI32::class.java)
	}

	@FieldOrder("eM11", "eM12", "eM21", "eM22", "eDx", "eDy")
	open class XFORM : Structure() {
		@JvmField
		var eM11 = 0f

		@JvmField
		var eM12 = 0f

		@JvmField
		var eM21 = 0f

		@JvmField
		var eM22 = 0f

		@JvmField
		var eDx = 0f

		@JvmField
		var eDy = 0f
	}

	fun CreateSolidBrush(color: DWORD?): HBRUSH?
	fun TextOutA(hdc: HDC?, x: Int, y: Int, lpString: String?, c: Int): Boolean
	fun SetWorldTransform(hdc: HDC?, lpxf: XFORM?): Boolean
	fun SetGraphicsMode(hdc: HDC?, iMode: Int): Int
}