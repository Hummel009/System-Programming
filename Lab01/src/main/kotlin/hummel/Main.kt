package hummel

import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.KeyEvent


fun main() {
	val className = "RenderingRectangle"
	val windowTitle = "Kotlin JNA WINAPI Test"

	val ps = HNUser32.PAINTSTRUCT()
	val squareRect = RECT()
	squareRect.left = 10
	squareRect.top = 10
	squareRect.right = 30
	squareRect.bottom = 30

	val windowClass = WNDCLASSEX()
	windowClass.hInstance = null
	windowClass.lpfnWndProc = WindowProc { hwnd, uMsg, wParam, lParam ->
		when (uMsg) {
			WM_DESTROY -> {
				HNUser32.INSTANCE.PostQuitMessage(0)
				LRESULT(0)
			}

			WM_SESSION_CHANGE -> {
				LRESULT(0)
			}

			WM_PAINT -> {
				val hdc = HNUser32.INSTANCE.BeginPaint(hwnd, ps)
				val redBrush = HNGdi32.INSTANCE.CreateSolidBrush(Color.RED.toDword())

				HNUser32.INSTANCE.FillRect(hdc, squareRect, redBrush)
				HNUser32.INSTANCE.EndPaint(hwnd, ps)
				LRESULT(0)
			}

			WM_KEYDOWN -> {
				//clear tail
				val hdc = HNUser32.INSTANCE.GetDC(hwnd)
				val whiteBrush = HNGdi32.INSTANCE.CreateSolidBrush(Color.WHITE.toDword())
				HNUser32.INSTANCE.FillRect(hdc, squareRect, whiteBrush)
				HNUser32.INSTANCE.ReleaseDC(hwnd, hdc)

				//move rectangle
				HNUser32.INSTANCE.InvalidateRect(hwnd, null, true)
				LRESULT(0)
			}

			else -> HNUser32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam)
		}
	}
	windowClass.lpszClassName = className
	windowClass.cbSize = windowClass.size()

	HNUser32.INSTANCE.RegisterClassEx(windowClass)

	val screenSize = Toolkit.getDefaultToolkit().screenSize
	val screenWidth = screenSize.getWidth().toInt()
	val screenHeight = screenSize.getHeight().toInt()
	val windowWidth = 400
	val windowHeight = 300
	val x = (screenWidth - windowWidth) / 2
	val y = (screenHeight - windowHeight) / 2

	val hwnd = HNUser32.INSTANCE.CreateWindowEx(
		0, className, windowTitle, WS_OVERLAPPEDWINDOW, x, y, windowWidth, windowHeight, null, null, null, null
	)

	HNUser32.INSTANCE.ShowWindow(hwnd, SW_SHOW)
	HNUser32.INSTANCE.UpdateWindow(hwnd)

	val msg = MSG()
	while (HNUser32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		HNUser32.INSTANCE.TranslateMessage(msg)
		HNUser32.INSTANCE.DispatchMessage(msg)

		if (msg.message == WM_KEYDOWN) {
			val keyCode = msg.wParam.toInt()
			when (keyCode) {
				KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
					squareRect.left -= 10
					squareRect.right -= 10
				}

				KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
					squareRect.left += 10
					squareRect.right += 10
				}

				KeyEvent.VK_UP, KeyEvent.VK_W -> {
					squareRect.top -= 10
					squareRect.bottom -= 10
				}

				KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
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