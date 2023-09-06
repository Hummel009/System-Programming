package hummel

import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.KeyEvent
import kotlin.experimental.and

const val WM_MOUSEWHEEL: Int = 0x020A
const val WM_MOUSEMOVE: Int = 0x0200
const val WM_LBUTTONDOWN: Int = 0x0201
const val WM_LBUTTONUP: Int = 0x0202

fun main() {
	val className = "RenderingRectangle"
	val windowTitle = "Windows API: Kotlin + JNA"

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
	val windowWidth = 1280
	val windowHeight = 720
	val x = (screenWidth - windowWidth) / 2
	val y = (screenHeight - windowHeight) / 2

	val hwnd = ExUser32.INSTANCE.CreateWindowEx(
		0, className, windowTitle, WS_OVERLAPPEDWINDOW, x, y, windowWidth, windowHeight, null, null, null, null
	)

	ExUser32.INSTANCE.ShowWindow(hwnd, SW_SHOW)
	ExUser32.INSTANCE.UpdateWindow(hwnd)

	for (key in HotKeys.values()) {
		ExUser32.INSTANCE.RegisterHotKey(hwnd, key.ordinal, MOD_CONTROL, (key.name[0]).code)
	}

	val msg = MSG()
	var speedX = 10
	var speedY = 10
	var reverseX = false
	var reverseY = false
	var mouseX = 0
	var mouseY = 0
	var iter = 0
	var isSnakeMode = false
	var isMousePressed = false
	loop@ while (ExUser32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		ExUser32.INSTANCE.TranslateMessage(msg)
		ExUser32.INSTANCE.DispatchMessage(msg)

		val reverse = reverseX || reverseY
		var movedViaKeyboard = false

		if (msg.message == WM_KEYDOWN) {
			clearAndUpdate(hwnd, squareRect, isSnakeMode)
			when (msg.wParam.toInt()) {
				KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
					squareRect.left -= speedX
					squareRect.right -= speedX
					movedViaKeyboard = true
					if (reverse) {
						iter++
					}
				}

				KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
					squareRect.left += speedX
					squareRect.right += speedX
					movedViaKeyboard = true
					if (reverse) {
						iter++
					}
				}

				KeyEvent.VK_UP, KeyEvent.VK_W -> {
					squareRect.top -= speedY
					squareRect.bottom -= speedY
					movedViaKeyboard = true
					if (reverse) {
						iter++
					}
				}

				KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
					squareRect.top += speedY
					squareRect.bottom += speedY
					movedViaKeyboard = true
					if (reverse) {
						iter++
					}
				}
			}
		}

		if (msg.message == WM_HOTKEY) {
			when (msg.wParam.toInt()) {
				HotKeys.X.ordinal -> {
					break@loop
				}

				HotKeys.Z.ordinal -> {
					isSnakeMode = true
				}

				HotKeys.C.ordinal -> {
					speedX *= 2
					speedY *= 2
				}
			}
		}

		if (msg.message == WM_MOUSEMOVE) {
			mouseX = msg.lParam.toInt() and 0xFFFF
			mouseY = (msg.lParam.toInt() shr 16) and 0xFFFF
		}

		if (msg.message == WM_LBUTTONDOWN) {
			isMousePressed = true
		}

		if (msg.message == WM_LBUTTONUP) {
			isMousePressed = false
		}

		if (isMousePressed) {
			clearAndUpdate(hwnd, squareRect, isSnakeMode)
			if (mouseX > squareRect.left) {
				squareRect.left += speedX
				squareRect.right += speedX
			}
			if (mouseX < squareRect.left) {
				squareRect.left -= speedX
				squareRect.right -= speedX
			}
			if (mouseY > squareRect.bottom) {
				squareRect.bottom += speedY
				squareRect.top += speedY
			}
			if (mouseY < squareRect.bottom) {
				squareRect.bottom -= speedY
				squareRect.top -= speedY
			}
		}

		if (msg.message == WM_MOUSEWHEEL) {
			clearAndUpdate(hwnd, squareRect, isSnakeMode)
			val wheelDelta = (msg.wParam.toInt() shr 16)
			val isShiftPressed = (ExUser32.INSTANCE.GetKeyState(KeyEvent.VK_SHIFT) and 0x8000.toShort()).toInt() != 0
			if (isShiftPressed) {
				if (wheelDelta > 0) {
					squareRect.left -= speedX
					squareRect.right -= speedX
				} else {
					squareRect.left += speedX
					squareRect.right += speedX
				}
			} else {
				if (wheelDelta > 0) {
					squareRect.top -= speedY
					squareRect.bottom -= speedY
				} else {
					squareRect.top += speedY
					squareRect.bottom += speedY
				}
			}
			movedViaKeyboard = true
		}

		if (movedViaKeyboard) {
			if (squareRect.left < 0) {
				squareRect.right -= squareRect.left
				squareRect.left = 0
				speedX *= -1
				speedX *= -1
				reverseX = true
			}
			if (squareRect.right > (windowWidth - 18)) {
				squareRect.left -= squareRect.right - (windowWidth - 18)
				squareRect.right = (windowWidth - 18)
				speedX *= -1
				speedX *= -1
				reverseX = true
			}
			if (squareRect.top < 0) {
				squareRect.bottom -= squareRect.top
				squareRect.top = 0
				speedY *= -1
				speedY *= -1
				reverseY = true
			}
			if (squareRect.bottom > (windowHeight - 47)) {
				squareRect.top -= squareRect.bottom - (windowHeight - 47)
				squareRect.bottom = (windowHeight - 47)
				speedY *= -1
				speedY *= -1
				reverseY = true
			}
		}
		if (reverse && iter == 5) {
			if (reverseX) {
				speedX *= -1
				speedX *= -1
				reverseX = false
				iter = 0
			}
			if (reverseY) {
				speedY *= -1
				speedY *= -1
				reverseY = false
				iter = 0
			}
		}
	}
}

private fun clearAndUpdate(hwnd: HWND?, squareRect: RECT, snakeMode: Boolean) {
	if (!snakeMode) {
		val hdc = ExUser32.INSTANCE.GetDC(hwnd)
		val whiteBrush = ExGDI32.INSTANCE.CreateSolidBrush(Color.WHITE.toDword())
		ExUser32.INSTANCE.FillRect(hdc, squareRect, whiteBrush)
		ExUser32.INSTANCE.ReleaseDC(hwnd, hdc)
	}
	ExUser32.INSTANCE.InvalidateRect(hwnd, null, true)
}

enum class HotKeys {
	X, Z, C
}

private fun Color.toDword(): DWORD {
	return DWORD((blue shl 16 or (green shl 8) or red).toLong())
}