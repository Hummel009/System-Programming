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

	val ps = HNUser32.PAINTSTRUCT()
	val squareRect = WinDef.RECT()
	squareRect.left = 10
	squareRect.top = 10
	squareRect.right = 30
	squareRect.bottom = 30

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
				val hdc = HNUser32.INSTANCE.BeginPaint(hwnd, ps)
				val redBrush =
					HNGdi32.INSTANCE.CreateSolidBrush(Color.RED.toDword())

				HNUser32.INSTANCE.FillRect(hdc, squareRect, redBrush)
				HNUser32.INSTANCE.EndPaint(hwnd, ps)
				WinDef.LRESULT(0)
			}

			WinUser.WM_KEYDOWN -> {
				// Clear the window by filling it with a background color (e.g., white)
				val hdc = User32.INSTANCE.GetDC(hwnd)
				val whiteColor = HNGdi32.INSTANCE.GetStockObject(HNGdi32.WHITE_BRUSH)
				HNUser32.INSTANCE.FillRect(hdc, squareRect, whiteColor)
				User32.INSTANCE.ReleaseDC(hwnd, hdc)

				User32.INSTANCE.InvalidateRect(hwnd, null, true)
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

		// Add this part to invalidate the entire window for repaint
		if (msg.message == WinUser.WM_KEYDOWN) {
			val keyCode = msg.wParam.toInt()
			when (keyCode) {
				KeyEvent.VK_LEFT -> {
					squareRect.left -= 10
					squareRect.right -= 10
				}

				KeyEvent.VK_RIGHT -> {
					squareRect.left += 10
					squareRect.right += 10
				}

				KeyEvent.VK_UP -> {
					squareRect.top -= 10
					squareRect.bottom -= 10
				}

				KeyEvent.VK_DOWN -> {
					squareRect.top += 10
					squareRect.bottom += 10
				}
			}
		}
	}
}

private fun Color.toDword(): DWORD {
	return DWORD((blue shl 16 or (green shl 8) or red).toLong())
}