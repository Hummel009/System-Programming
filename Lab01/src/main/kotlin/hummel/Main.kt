package hummel

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinUser
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.KeyEvent


fun main() {
	val className = "ClassHummel009"
	val windowTitle = "Kotlin JNA WINAPI Test"

	val windowClass = WinUser.WNDCLASSEX()
	windowClass.hInstance = null
	windowClass.lpfnWndProc = WinUser.WindowProc { hwnd, uMsg, wParam, lParam ->
		when (uMsg) {
			WinUser.WM_DESTROY -> {
				User32.INSTANCE.PostQuitMessage(0)
				WinDef.LRESULT(0)
			}

			WinUser.WM_SESSION_CHANGE -> {
				WinDef.LRESULT(0)
			}

			WinUser.WM_PAINT -> {
				val ps = HNUser32.PAINTSTRUCT()
				val hdc = HNUser32.INSTANCE.BeginPaint(hwnd, ps)
				val redBrush =
					HNGdi32.INSTANCE.CreateSolidBrush(fromRGB(Color.RED.red, Color.RED.green, Color.RED.blue))

				val squareRect = WinDef.RECT()
				squareRect.left = 10
				squareRect.top = 10
				squareRect.right = 30
				squareRect.bottom = 30

				HNUser32.INSTANCE.FillRect(hdc, squareRect, redBrush)
				HNUser32.INSTANCE.EndPaint(hwnd, ps)
				WinDef.LRESULT(0)
			}

			WinUser.WM_KEYDOWN -> {
				val keyCode = wParam.toInt()
				when (keyCode) {
					KeyEvent.VK_LEFT -> moveRectangle(hwnd, -10, 0)
					KeyEvent.VK_RIGHT -> moveRectangle(hwnd, 10, 0)
					KeyEvent.VK_UP -> moveRectangle(hwnd, 0, -10)
					KeyEvent.VK_DOWN -> moveRectangle(hwnd, 0, 10)
				}
				WinDef.LRESULT(0)
			}

			else -> User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam)
		}
	}
	windowClass.lpszClassName = className
	windowClass.cbSize = windowClass.size()

	User32.INSTANCE.RegisterClassEx(windowClass)

	val screenSize = Toolkit.getDefaultToolkit().screenSize
	val screenWidth = screenSize.getWidth().toInt()
	val screenHeight = screenSize.getHeight().toInt()
	val windowWidth = 400
	val windowHeight = 300
	val x = (screenWidth - windowWidth) / 2
	val y = (screenHeight - windowHeight) / 2

	val hwnd = User32.INSTANCE.CreateWindowEx(
		0, className, windowTitle, WinUser.WS_OVERLAPPEDWINDOW, x, y, windowWidth, windowHeight, null, null, null, null
	)

	User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_SHOW)
	User32.INSTANCE.UpdateWindow(hwnd)

	val msg = WinUser.MSG()
	while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		User32.INSTANCE.TranslateMessage(msg)
		User32.INSTANCE.DispatchMessage(msg)
	}
}

fun moveRectangle(hwnd: WinDef.HWND, dx: Int, dy: Int) {
	val rect = WinDef.RECT()
	User32.INSTANCE.GetClientRect(hwnd, rect)
	User32.INSTANCE.InvalidateRect(hwnd, rect, true)
	User32.INSTANCE.UpdateWindow(hwnd)

	User32.INSTANCE.GetWindowRect(hwnd, rect)
	User32.INSTANCE.MoveWindow(
		hwnd, rect.left + dx, rect.top + dy,
		rect.right - rect.left, rect.bottom - rect.top, true
	)
}

fun fromRGB(red: Int, green: Int, blue: Int): DWORD {
	return DWORD((blue shl 16 or (green shl 8) or red).toLong())
}