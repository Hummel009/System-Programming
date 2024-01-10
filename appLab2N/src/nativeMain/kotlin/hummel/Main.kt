package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max

private const val width: Int = 960
private const val height: Int = 540

fun main() {
	memScoped {
		val className = "RenderingLauncher"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.lpszClassName = className.wcstr.ptr

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
	memScoped {
		when (msg.toInt()) {
			WM_CREATE -> {
				val clientRect = alloc<RECT>()
				GetClientRect(window, clientRect.ptr)

				val buttonWidth = 100
				val buttonHeight = 40
				val buttonX = (clientRect.right - buttonWidth) / 2
				val buttonY = (clientRect.bottom - buttonHeight) / 2

				val id1 = 1
				val id2 = 2

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
					id1.toLong().toCPointer(),
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
					id2.toLong().toCPointer(),
					null,
					null
				)
			}

			WM_COMMAND -> {
				val buttonId = wParam.loword().toInt()

				when (buttonId) {
					1 -> {
						MessageBoxW(window, "Button 1 clicked!", "Message", MB_OK.toUInt())
					}

					2 -> circle()

					else -> DefWindowProcW(window, msg, wParam, lParam)
				}
			}

			WM_CLOSE -> DestroyWindow(window)
			WM_DESTROY -> PostQuitMessage(0)

			else -> {}
		}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

private fun ULong.loword(): ULong = this and 0xFFFFu