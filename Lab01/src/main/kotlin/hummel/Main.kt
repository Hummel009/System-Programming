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

	val rc = RECT()
	rc.left = 10
	rc.top = 10
	rc.right = 30
	rc.bottom = 30

	val wc = WNDCLASSEX()
	wc.hInstance = null
	wc.lpszClassName = className
	wc.lpfnWndProc = WindowProc { hWnd, uMsg, wParam, lParam ->
		val ps = ExUser32.PAINTSTRUCT()
		when (uMsg) {
			WM_DESTROY -> {
				ExUser32.INSTANCE.PostQuitMessage(0)
				LRESULT(0)
			}

			WM_SESSION_CHANGE -> {
				LRESULT(0)
			}

			WM_PAINT -> {
				val hdc = ExUser32.INSTANCE.BeginPaint(hWnd, ps)
				val brush = ExGDI32.INSTANCE.CreateSolidBrush(Color.RED.toDword())

				ExUser32.INSTANCE.FillRect(hdc, rc, brush)
				ExUser32.INSTANCE.EndPaint(hWnd, ps)
				LRESULT(0)
			}

			else -> ExUser32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam)
		}
	}

	ExUser32.INSTANCE.RegisterClassEx(wc)

	val width = 1280
	val height = 720
	val hWnd = createWindowInCenter(className, windowTitle, width, height)

	ExUser32.INSTANCE.ShowWindow(hWnd, SW_SHOW)
	ExUser32.INSTANCE.UpdateWindow(hWnd)

	for (key in HotKeys.values()) {
		ExUser32.INSTANCE.RegisterHotKey(hWnd, key.ordinal, MOD_CONTROL, (key.name[0]).code)
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

		when (msg.message) {
			WM_KEYDOWN -> {
				clearAndUpdate(hWnd, rc, isSnakeMode)
				when (msg.wParam.toInt()) {
					KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
						rc.left -= speedX
						rc.right -= speedX
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
						rc.left += speedX
						rc.right += speedX
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					KeyEvent.VK_UP, KeyEvent.VK_W -> {
						rc.top -= speedY
						rc.bottom -= speedY
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
						rc.top += speedY
						rc.bottom += speedY
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}
				}
			}

			WM_HOTKEY -> {
				when (msg.wParam.toInt()) {
					HotKeys.X.ordinal -> break@loop
					HotKeys.Z.ordinal -> isSnakeMode = true
					HotKeys.C.ordinal -> {
						speedX *= 2
						speedY *= 2
					}
				}
			}

			WM_MOUSEMOVE -> {
				mouseX = msg.lParam.toInt() and 0xFFFF
				mouseY = (msg.lParam.toInt() shr 16) and 0xFFFF
			}

			WM_LBUTTONDOWN -> {
				isMousePressed = true
			}

			WM_LBUTTONUP -> {
				isMousePressed = false
			}

			WM_MOUSEWHEEL -> {
				clearAndUpdate(hWnd, rc, isSnakeMode)
				val wheelDelta = (msg.wParam.toInt() shr 16)
				val isShiftPressed =
					(ExUser32.INSTANCE.GetKeyState(KeyEvent.VK_SHIFT) and 0x8000.toShort()).toInt() != 0
				if (isShiftPressed) {
					if (wheelDelta > 0) {
						rc.left -= speedX
						rc.right -= speedX
					} else {
						rc.left += speedX
						rc.right += speedX
					}
				} else {
					if (wheelDelta > 0) {
						rc.top -= speedY
						rc.bottom -= speedY
					} else {
						rc.top += speedY
						rc.bottom += speedY
					}
				}
				movedViaKeyboard = true
			}
		}

		if (isMousePressed) {
			clearAndUpdate(hWnd, rc, isSnakeMode)
			if (mouseX > rc.left) {
				rc.left += speedX
				rc.right += speedX
			}
			if (mouseX < rc.left) {
				rc.left -= speedX
				rc.right -= speedX
			}
			if (mouseY > rc.bottom) {
				rc.bottom += speedY
				rc.top += speedY
			}
			if (mouseY < rc.bottom) {
				rc.bottom -= speedY
				rc.top -= speedY
			}
		}

		if (movedViaKeyboard) {
			if (rc.left < 0) {
				rc.right -= rc.left
				rc.left = 0
				speedX *= -1
				reverseX = true
			}
			if (rc.right > (width - 18)) {
				rc.left -= rc.right - (width - 18)
				rc.right = (width - 18)
				speedX *= -1
				reverseX = true
			}
			if (rc.top < 0) {
				rc.bottom -= rc.top
				rc.top = 0
				speedY *= -1
				reverseY = true
			}
			if (rc.bottom > (height - 47)) {
				rc.top -= rc.bottom - (height - 47)
				rc.bottom = (height - 47)
				speedY *= -1
				reverseY = true
			}
		}

		if (reverse && iter == 5) {
			if (reverseX) {
				speedX *= -1
				reverseX = false
				iter = 0
			}
			if (reverseY) {
				speedY *= -1
				reverseY = false
				iter = 0
			}
		}
	}
}

private fun createWindowInCenter(className: String, windowTitle: String, width: Int, height: Int): HWND {
	val screenSize = Toolkit.getDefaultToolkit().screenSize
	val screenWidth = screenSize.getWidth().toInt()
	val screenHeight = screenSize.getHeight().toInt()
	val x = (screenWidth - width) / 2
	val y = (screenHeight - height) / 2

	return ExUser32.INSTANCE.CreateWindowEx(
		0, className, windowTitle, WS_OVERLAPPEDWINDOW or WS_SIZEBOX, x, y, width, height, null, null, null, null
	)
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

private fun Color.toDword(): DWORD {
	return DWORD((blue shl 16 or (green shl 8) or red).toLong())
}

enum class HotKeys {
	Z, X, C
}