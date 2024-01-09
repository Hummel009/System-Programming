package hummel

import kotlinx.cinterop.*
import platform.windows.*

const val VK_W: Int = 0x57
const val VK_A: Int = 0x41
const val VK_S: Int = 0x53
const val VK_D: Int = 0x44

const val width: Int = 1280
const val height: Int = 720

const val rgbRed: COLORREF = 0x000000FFu
const val rgbWhite: COLORREF = 0x00FFFFFFu

var speedX: Int = 10
var speedY: Int = 10
var reverseX: Boolean = false
var reverseY: Boolean = false
var mouseX: Int = 0
var mouseY: Int = 0
var count: Int = 0
var snakeMode: Boolean = false
var mousePressed: Boolean = false

lateinit var square: RECT

fun main() {
	memScoped {
		val className = "RenderingRectangle"
		val windowTitle = "Windows API: Kotlin Native"

		square = alloc<RECT>()
		square.left = 10
		square.top = 10
		square.right = 30
		square.bottom = 30

		val windowClass = alloc<WNDCLASS>()
		windowClass.hCursor = LoadCursorW(null, IDC_ARROW)
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.cbClsExtra = 0
		windowClass.cbWndExtra = 0
		windowClass.hInstance = null
		windowClass.hIcon = null
		windowClass.lpszMenuName = null
		windowClass.lpszClassName = className.wcstr.ptr
		windowClass.style = 0u

		RegisterClassW(windowClass.ptr)

		val window = CreateWindowExW(
			WS_EX_CLIENTEDGE.toUInt(),
			className,
			windowTitle,
			(WS_OVERLAPPED or WS_CAPTION or WS_SYSMENU or WS_MINIMIZEBOX).toUInt(),
			CW_USEDEFAULT,
			CW_USEDEFAULT,
			width,
			height,
			null,
			null,
			null,
			null
		)

		ShowWindow(window, SW_SHOW)
		UpdateWindow(window)

		for (key in HotKeys.entries) {
			RegisterHotKey(window, key.ordinal, MOD_CONTROL.toUInt(), key.name[0].code.toUInt())
		}

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}

		for (key in HotKeys.entries) {
			UnregisterHotKey(window, key.ordinal)
		}
	}
}

private fun clearAndUpdate(window: HWND?, square: RECT, snakeMode: Boolean) {
	if (!snakeMode) {
		val deviceContext = GetDC(window)
		val brush = CreateSolidBrush(rgbWhite)
		FillRect(deviceContext, square.ptr, brush)
		ReleaseDC(window, deviceContext)
	}
	InvalidateRect(window, null, 1)
}

private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	memScoped {
		val paintStructure = alloc<PAINTSTRUCT>()

		val reverse = reverseX || reverseY
		var movedViaKeyboard = false

		when (msg.toInt()) {
			WM_KEYDOWN -> {
				clearAndUpdate(window, square, snakeMode)
				when (wParam.toInt()) {
					VK_LEFT, VK_A -> {
						square.left -= speedX
						square.right -= speedX
						movedViaKeyboard = true
						if (reverse) {
							count++
						}
					}

					VK_RIGHT, VK_D -> {
						square.left += speedX
						square.right += speedX
						movedViaKeyboard = true
						if (reverse) {
							count++
						}
					}

					VK_UP, VK_W -> {
						square.top -= speedY
						square.bottom -= speedY
						movedViaKeyboard = true
						if (reverse) {
							count++
						}
					}

					VK_DOWN, VK_S -> {
						square.top += speedY
						square.bottom += speedY
						movedViaKeyboard = true
						if (reverse) {
							count++
						}
					}
				}
			}

			WM_HOTKEY -> {
				when (wParam.toInt()) {
					HotKeys.X.ordinal -> PostQuitMessage(0)
					HotKeys.Z.ordinal -> snakeMode = true
					HotKeys.C.ordinal -> {
						speedX *= 2
						speedY *= 2
					}
				}
			}

			WM_MOUSEMOVE -> {
				mouseX = lParam.toInt() and 0xFFFF
				mouseY = (lParam.toInt() shr 16) and 0xFFFF
			}

			WM_LBUTTONDOWN, WM_LBUTTONUP -> {
				mousePressed = true
			}

			WM_MOUSEWHEEL -> {
				clearAndUpdate(window, square, snakeMode)
				val wheelDelta = (wParam.toInt() shr 16)
				val isShiftPressed = (GetKeyState(VK_SHIFT).toInt() and 0x8000) != 0
				if (isShiftPressed) {
					if (wheelDelta > 0) {
						square.left -= speedX
						square.right -= speedX
					} else {
						square.left += speedX
						square.right += speedX
					}
				} else {
					if (wheelDelta > 0) {
						square.top -= speedY
						square.bottom -= speedY
					} else {
						square.top += speedY
						square.bottom += speedY
					}
				}
				movedViaKeyboard = true
			}

			WM_CLOSE -> DestroyWindow(window)
			WM_DESTROY -> PostQuitMessage(0)

			WM_PAINT -> {
				val deviceContext = BeginPaint(window, paintStructure.ptr)
				val brush = CreateSolidBrush(rgbRed)

				FillRect(deviceContext, square.ptr, brush)
				EndPaint(window, paintStructure.ptr)
			}
		}

		if (mousePressed) {
			clearAndUpdate(window, square, snakeMode)
			if (mouseX > square.left) {
				square.left += speedX
				square.right += speedX
			}
			if (mouseX < square.left) {
				square.left -= speedX
				square.right -= speedX
			}
			if (mouseY > square.bottom) {
				square.bottom += speedY
				square.top += speedY
			}
			if (mouseY < square.bottom) {
				square.bottom -= speedY
				square.top -= speedY
			}
		}

		if (movedViaKeyboard) {
			if (square.left < 0) {
				square.right -= square.left
				square.left = 0
				speedX *= -1
				reverseX = true
			}
			if (square.right > (width - 18)) {
				square.left -= square.right - (width - 18)
				square.right = (width - 18)
				speedX *= -1
				reverseX = true
			}
			if (square.top < 0) {
				square.bottom -= square.top
				square.top = 0
				speedY *= -1
				reverseY = true
			}
			if (square.bottom > (height - 47)) {
				square.top -= square.bottom - (height - 47)
				square.bottom = (height - 47)
				speedY *= -1
				reverseY = true
			}
		}

		if (reverse && count == 5) {
			if (reverseX) {
				speedX *= -1
				reverseX = false
				count = 0
			}
			if (reverseY) {
				speedY *= -1
				reverseY = false
				count = 0
			}
		}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

enum class HotKeys {
	Z, X, C
}