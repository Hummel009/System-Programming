package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max

private const val width: Int = 960
private const val height: Int = 540

private const val buttonId1: Int = 1
private const val buttonId2: Int = 2

fun main() {
	memScoped {
		val className = "RenderingLauncher"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.lpszClassName = className.wcstr.ptr
		windowClass.hbrBackground = COLOR_WINDOW.toLong().toCPointer()

		RegisterClassW(windowClass.ptr)

		val screenWidth = GetSystemMetrics(SM_CXSCREEN)
		val screenHeight = GetSystemMetrics(SM_CYSCREEN)

		val windowWidth = width
		val windowHeight = height

		val windowX = max(0, (screenWidth - windowWidth) / 2)
		val windowY = max(0, (screenHeight - windowHeight) / 2)

		val window = CreateWindowExW(
			WS_EX_CLIENTEDGE.toUInt(),
			className,
			windowTitle,
			WS_OVERLAPPEDWINDOW.toUInt(),
			windowX,
			windowY,
			windowWidth,
			windowHeight,
			null,
			null,
			null,
			null
		)

		ShowWindow(window, SW_SHOW)
		UpdateWindow(window)

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}
	}
}

private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	when (msg.toInt()) {
		WM_CREATE -> {
			memScoped {
				val square = alloc<RECT>()
				GetClientRect(window, square.ptr)

				val buttonWidth = 100
				val buttonHeight = 40
				val buttonX = (square.right - buttonWidth) / 2
				val buttonY = (square.bottom - buttonHeight) / 2

				CreateWindowExW(
					WS_EX_CLIENTEDGE.toUInt(),
					"BUTTON",
					"Table",
					(WS_TABSTOP or WS_VISIBLE or WS_CHILD or BS_DEFPUSHBUTTON).toUInt(),
					buttonX - buttonWidth / 2,
					buttonY,
					buttonWidth,
					buttonHeight,
					window,
					buttonId1.toLong().toCPointer(),
					null,
					null
				)

				CreateWindowExW(
					WS_EX_CLIENTEDGE.toUInt(),
					"BUTTON",
					"Circle",
					(WS_TABSTOP or WS_VISIBLE or WS_CHILD or BS_DEFPUSHBUTTON).toUInt(),
					buttonX + buttonWidth + 10 - buttonWidth / 2,
					buttonY,
					buttonWidth,
					buttonHeight,
					window,
					buttonId2.toLong().toCPointer(),
					null,
					null
				)
			}
		}

		WM_COMMAND -> {
			val buttonId = wParam.loword().toInt()

			when (buttonId) {
				buttonId1 -> table()
				buttonId2 -> circle()
				else -> DefWindowProcW(window, msg, wParam, lParam)
			}
		}

		WM_CLOSE -> DestroyWindow(window)
		WM_DESTROY -> PostQuitMessage(0)
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

private fun ULong.loword(): ULong = this and 0xFFFFu