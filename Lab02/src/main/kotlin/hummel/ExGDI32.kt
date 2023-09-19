package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.WinDef.*

internal interface ExGDI32 : GDI32 {
	companion object {
		val INSTANCE: ExGDI32 = Native.load("gdi32", ExGDI32::class.java)
	}

	fun CreateSolidBrush(color: DWORD?): HBRUSH?
	fun SetTextColor(hdc: HDC?, color: DWORD?): DWORD?
	fun TextOutA(hdc: HDC?, x: Int, y: Int, lpString: String?, c: Int): Boolean

	fun CreateFontA(
		cHeight: Int,
		cWidth: Int,
		cEscapement: Int,
		cOrientation: Int,
		cWeight: Int,
		bItalic: Int,
		bUnderline: Int,
		bStrikeOut: Int,
		iCharSet: Int,
		iOutPrecision: Int,
		iClipPrecision: Int,
		iQuality: Int,
		iPitchAndFamily: Int,
		pszFaceName: String?
	): HFONT?
}