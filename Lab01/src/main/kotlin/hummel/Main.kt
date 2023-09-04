package hummel

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.win32.StdCallLibrary
import javax.swing.SwingUtilities
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser

fun main() {
	SwingUtilities.invokeLater {
		val app = SpriteMoverApp()
		app.isVisible = true
	}
}

interface User32 : StdCallLibrary {
	companion object {
		val INSTANCE: User32 = Native.load("user32", User32::class.java)
	}

	fun SetCursorPos(x: Int, y: Int): Boolean
	fun GetCursorPos(lpPoint: WinDef.POINT): Boolean
	fun RegisterHotKey(hWnd: WinDef.HWND?, id: Int, fsModifiers: Int, vk: Int): Boolean
	fun UnregisterHotKey(hWnd: WinDef.HWND?, id: Int): Boolean
	fun SendMessage(hWnd: WinDef.HWND?, msg: Int, wParam: WinDef.WPARAM, lParam: WinDef.LPARAM): WinDef.LRESULT
	fun PostMessage(hWnd: WinDef.HWND?, msg: Int, wParam: WinDef.WPARAM, lParam: WinDef.LPARAM): Boolean
	fun GetClientRect(hWnd: WinDef.HWND?, lpRect: WinDef.RECT): Boolean
	fun GetWindowRect(hWnd: WinDef.HWND?, lpRect: WinDef.RECT): Boolean
	fun ScreenToClient(hWnd: WinDef.HWND?, lpPoint: WinDef.POINT): Boolean
	fun GetAsyncKeyState(vKey: Int): Short
	fun GetKeyState(vKey: Int): Short
}

interface Kernel32 : Library {
	companion object {
		val INSTANCE: Kernel32 = Native.load("kernel32", Kernel32::class.java)
	}

	fun GetCurrentThreadId(): WinDef.DWORD
}

class WinDefEx {
	class MSG : Structure() {
		@JvmField
		var hwnd: WinDef.HWND = WinDef.HWND(Pointer.NULL)

		@JvmField
		var message: Int = 0

		@JvmField
		var wParam: WinDef.WPARAM = WinDef.WPARAM(0)

		@JvmField
		var lParam: WinDef.LPARAM = WinDef.LPARAM(0)

		@JvmField
		var time: Int = 0

		@JvmField
		var pt: WinDef.POINT = WinDef.POINT(0, 0)
	}
}