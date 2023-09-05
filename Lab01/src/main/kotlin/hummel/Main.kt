package hummel

import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.KeyEvent
import kotlin.experimental.and

const val WM_MOUSEWHEEL: Int = 0x020A

fun main() {
	val className = "RenderingRectangle"
	val windowTitle = "Kotlin JNA WINAPI Test"

	val ps = ExUser32.PAINTSTRUCT()
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
				ExUser32.INSTANCE.PostQuitMessage(0)
				LRESULT(0)
			}

			WM_SESSION_CHANGE -> {
				LRESULT(0)
			}

			WM_PAINT -> {
				val hdc = ExUser32.INSTANCE.BeginPaint(hwnd, ps)
				val redBrush = ExGDI32.INSTANCE.CreateSolidBrush(Color.RED.toDword())

				ExUser32.INSTANCE.FillRect(hdc, squareRect, redBrush)
				ExUser32.INSTANCE.EndPaint(hwnd, ps)
				LRESULT(0)
			}

			else -> ExUser32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam)
		}
	}
	windowClass.lpszClassName = className
	windowClass.cbSize = windowClass.size()

	ExUser32.INSTANCE.RegisterClassEx(windowClass)

	val screenSize = Toolkit.getDefaultToolkit().screenSize
	val screenWidth = screenSize.getWidth().toInt()
	val screenHeight = screenSize.getHeight().toInt()
	val windowWidth = 400
	val windowHeight = 300
	val x = (screenWidth - windowWidth) / 2
	val y = (screenHeight - windowHeight) / 2

	val hwnd = ExUser32.INSTANCE.CreateWindowEx(
		0, className, windowTitle, WS_OVERLAPPEDWINDOW, x, y, windowWidth, windowHeight, null, null, null, null
	)

	ExUser32.INSTANCE.ShowWindow(hwnd, SW_SHOW)
	ExUser32.INSTANCE.UpdateWindow(hwnd)

	val msg = MSG()
	var speedL = 10
	var speedR = 10
	var speedT = 10
	var speedB = 10
	while (ExUser32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		ExUser32.INSTANCE.TranslateMessage(msg)
		ExUser32.INSTANCE.DispatchMessage(msg)

		var moved = false
		if (msg.message == WM_KEYDOWN) {
			clearAndUpdate(hwnd, squareRect)
			val keyCode = msg.wParam.toInt()
			when (keyCode) {
				KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
					squareRect.left -= speedL
					squareRect.right -= speedR
					moved = true
				}

				KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
					squareRect.left += speedL
					squareRect.right += speedR
					moved = true
				}

				KeyEvent.VK_UP, KeyEvent.VK_W -> {
					squareRect.top -= speedT
					squareRect.bottom -= speedB
					moved = true
				}

				KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
					squareRect.top += speedT
					squareRect.bottom += speedB
					moved = true
				}
			}
		}

		if (msg.message == WM_MOUSEWHEEL) {
			clearAndUpdate(hwnd, squareRect)
			val wheelDelta = (msg.wParam.toInt() shr 16)
			val isShiftPressed = (ExUser32.INSTANCE.GetKeyState(KeyEvent.VK_SHIFT) and 0x8000.toShort()).toInt() != 0
			if (isShiftPressed) {
				if (wheelDelta > 0) {
					squareRect.left -= speedL
					squareRect.right -= speedR
				} else {
					squareRect.left += speedL
					squareRect.right += speedR
				}
			} else {
				if (wheelDelta > 0) {
					squareRect.top -= speedT
					squareRect.bottom -= speedB
				} else {
					squareRect.top += speedT
					squareRect.bottom += speedB
				}
			}
			moved = true
		}

		if (moved) {
			if (squareRect.left < 0) {
				squareRect.right -= squareRect.left
				squareRect.left = 0
				speedL *= -1
				speedR *= -1
			}
			if (squareRect.right > (windowWidth - 18)) {
				squareRect.left -= squareRect.right - (windowWidth - 18)
				squareRect.right = (windowWidth - 18)
				speedL *= -1
				speedR *= -1
			}
			if (squareRect.top < 0) {
				squareRect.bottom -= squareRect.top
				squareRect.top = 0
				speedT *= -1
				speedB *= -1
			}
			if (squareRect.bottom > (windowHeight - 47)) {
				squareRect.top -= squareRect.bottom - (windowHeight - 47)
				squareRect.bottom = (windowHeight - 47)
				speedT *= -1
				speedB *= -1
			}
		}
	}
}

private fun clearAndUpdate(hwnd: HWND?, squareRect: RECT) {
	val hdc = ExUser32.INSTANCE.GetDC(hwnd)
	val whiteBrush = ExGDI32.INSTANCE.CreateSolidBrush(Color.WHITE.toDword())
	ExUser32.INSTANCE.FillRect(hdc, squareRect, whiteBrush)
	ExUser32.INSTANCE.ReleaseDC(hwnd, hdc)
	ExUser32.INSTANCE.InvalidateRect(hwnd, null, true)
}

private fun Color.toDword(): DWORD {
	return DWORD((blue shl 16 or (green shl 8) or red).toLong())
}