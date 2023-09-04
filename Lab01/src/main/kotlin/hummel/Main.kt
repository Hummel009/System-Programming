package hummel

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import java.awt.Toolkit

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