package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max

const val VK_W: Int = 0x57
const val VK_A: Int = 0x41
const val VK_S: Int = 0x53
const val VK_D: Int = 0x44

const val width: Int = 960
const val height: Int = 540

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
		val className = "RedSquare"
		val windowTitle = "Windows API: Kotlin Native"

		square = alloc<RECT>()
		square.left = 10
		square.top = 10
		square.right = 30
		square.bottom = 30

		val windowClass = alloc<WNDCLASS>()
		windowClass.style = 0u
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.cbClsExtra = 0
		windowClass.cbWndExtra = 0
		windowClass.hInstance = null
		windowClass.hIcon = null
		windowClass.hCursor = null
		windowClass.hbrBackground = (COLOR_WINDOW + 1).toLong().toCPointer()
		windowClass.lpszMenuName = null
		windowClass.lpszClassName = className.wcstr.ptr

		RegisterClassW(windowClass.ptr)

		val screenWidth = GetSystemMetrics(SM_CXSCREEN)
		val screenHeight = GetSystemMetrics(SM_CYSCREEN)

		val windowWidth = width
		val windowHeight = height

		val windowX = max(0, (screenWidth - windowWidth) / 2)
		val windowY = max(0, (screenHeight - windowHeight) / 2)

		val window = CreateWindowExW(
			0u,
			className,
			windowTitle,
			(WS_VISIBLE or WS_CAPTION or WS_SYSMENU).toUInt(),
			windowX,
			windowY,
			width,
			height,
			null,
			null,
			null,
			null
		)

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

private fun clearAndUpdate(window: HWND?, snakeMode: Boolean) {
	memScoped {
		val deviceContext = GetDC(window)
		val square = alloc<RECT>()
		val brush = CreateSolidBrush(rgbWhite)
		GetClientRect(window, square.ptr)
		if (!snakeMode) {
			FillRect(deviceContext, square.ptr, brush)
		}
		InvalidateRect(window, null, TRUE)
		ReleaseDC(window, deviceContext)
	}
}

private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	memScoped {
		val paintStructure = alloc<PAINTSTRUCT>()

		val reverse = reverseX || reverseY
		var movedViaKeyboard = false

		when (msg.toInt()) {
			WM_KEYDOWN -> {
				clearAndUpdate(window, snakeMode)
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

			WM_LBUTTONDOWN -> {
				mousePressed = true
			}

			WM_LBUTTONUP -> {
				mousePressed = false
			}

			WM_MOUSEWHEEL -> {
				clearAndUpdate(window, snakeMode)
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

			WM_PAINT -> {
				val deviceContext = BeginPaint(window, paintStructure.ptr)
				val brush = CreateSolidBrush(rgbRed)

				FillRect(deviceContext, square.ptr, brush)
				EndPaint(window, paintStructure.ptr)
			}

			WM_CLOSE -> DestroyWindow(window)
			WM_DESTROY -> PostQuitMessage(0)
		}

		if (mousePressed) {
			clearAndUpdate(window, snakeMode)
			if (mouseX > square.left) {
				square.left += speedX * 2
				square.right += speedX * 2
			}
			if (mouseX < square.left) {
				square.left -= speedX * 2
				square.right -= speedX * 2
			}
			if (mouseY > square.bottom) {
				square.bottom += speedY * 2
				square.top += speedY * 2
			}
			if (mouseY < square.bottom) {
				square.bottom -= speedY * 2
				square.top -= speedY * 2
			}
			mousePressed = false
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