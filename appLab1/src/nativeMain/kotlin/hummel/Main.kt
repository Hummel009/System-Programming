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
var iter: Int = 0
var isSnakeMode: Boolean = false
var isMousePressed: Boolean = false

lateinit var rc: RECT

fun main() {
	memScoped {
		val className = "RenderingRectangle"
		val windowTitle = "Windows API: Kotlin + JNA"

		rc = alloc<RECT>()
		rc.left = 10
		rc.top = 10
		rc.right = 30
		rc.bottom = 30

		val wc = alloc<WNDCLASS>()
		wc.hCursor = LoadCursorW(null, IDC_ARROW)
		wc.lpfnWndProc = staticCFunction(::wndProc)
		wc.cbClsExtra = 0
		wc.cbWndExtra = 0
		wc.hInstance = null
		wc.hIcon = null
		wc.lpszMenuName = null
		wc.lpszClassName = className.wcstr.ptr
		wc.style = 0u

		RegisterClassW(wc.ptr)

		val hWnd = CreateWindowExW(
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

		ShowWindow(hWnd, SW_SHOW)
		UpdateWindow(hWnd)

		for (key in HotKeys.entries) {
			RegisterHotKey(hWnd, key.ordinal, MOD_CONTROL.toUInt(), key.name[0].code.toUInt())
		}

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}

		for (key in HotKeys.entries) {
			UnregisterHotKey(hWnd, key.ordinal)
		}
	}
}

private fun clearAndUpdate(hWnd: HWND?, squareRect: RECT, snakeMode: Boolean) {
	if (!snakeMode) {
		val hdc = GetDC(hWnd)
		val whiteBrush = CreateSolidBrush(rgbWhite)
		FillRect(hdc, squareRect.ptr, whiteBrush)
		ReleaseDC(hWnd, hdc)
	}
	InvalidateRect(hWnd, null, 1)
}

private fun wndProc(hWnd: HWND?, uMsg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	memScoped {
		val ps = alloc<PAINTSTRUCT>()

		val reverse = reverseX || reverseY
		var movedViaKeyboard = false

		when (uMsg.toInt()) {
			WM_KEYDOWN -> {
				clearAndUpdate(hWnd, rc, isSnakeMode)
				when (wParam.toInt()) {
					VK_LEFT, VK_A -> {
						rc.left -= speedX
						rc.right -= speedX
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					VK_RIGHT, VK_D -> {
						rc.left += speedX
						rc.right += speedX
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					VK_UP, VK_W -> {
						rc.top -= speedY
						rc.bottom -= speedY
						movedViaKeyboard = true
						if (reverse) {
							iter++
						}
					}

					VK_DOWN, VK_S -> {
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
				when (wParam.toInt()) {
					HotKeys.X.ordinal -> PostQuitMessage(0)
					HotKeys.Z.ordinal -> isSnakeMode = true
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
				isMousePressed = true
			}

			WM_LBUTTONUP -> {
				isMousePressed = false
			}

			WM_MOUSEWHEEL -> {
				clearAndUpdate(hWnd, rc, isSnakeMode)
				val wheelDelta = (wParam.toInt() shr 16)
				val isShiftPressed = (GetKeyState(VK_SHIFT).toInt() and 0x8000) != 0
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

			WM_CLOSE -> DestroyWindow(hWnd)
			WM_DESTROY -> PostQuitMessage(0)

			WM_PAINT -> {
				val hdc = BeginPaint(hWnd, ps.ptr)
				val brush = CreateSolidBrush(rgbRed)

				FillRect(hdc, rc.ptr, brush)
				EndPaint(hWnd, ps.ptr)
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
	return DefWindowProcW(hWnd, uMsg, wParam, lParam)
}

enum class HotKeys {
	Z, X, C
}