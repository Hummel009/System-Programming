package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max

const val width: Int = 960
const val height: Int = 540

fun main() {
	memScoped {
		val className = "RenderingLauncher"
		val windowTitle = "Windows API: Kotlin Native"

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
			(WS_OVERLAPPED or WS_CAPTION or WS_SYSMENU or WS_MINIMIZEBOX).toUInt(),
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
		val data = allocArray<LONG_PTRVar>(1)
		data[0] = GetWindowLongPtrW(window, GWLP_HINSTANCE)

		when (msg.toInt()) {
			WM_CREATE -> {
				val clientRect = alloc<RECT>()
				GetClientRect(window, clientRect.ptr)

				val buttonWidth = 100
				val buttonHeight = 100
				val buttonX = (clientRect.right - buttonWidth) / 2
				val buttonY = (clientRect.bottom - buttonHeight) / 2

				CreateWindowExW(
					WS_EX_CLIENTEDGE.toUInt(),
					"BUTTON",
					"BUTTON 1",
					(WS_TABSTOP or WS_VISIBLE or WS_CHILD or BS_DEFPUSHBUTTON).toUInt(),
					buttonX - buttonWidth / 2,
					buttonY,
					buttonWidth,
					buttonHeight,
					window,
					null,
					data.reinterpret(),
					null
				)

				CreateWindowExW(
					WS_EX_CLIENTEDGE.toUInt(),
					"BUTTON",
					"BUTTON 2",
					(WS_TABSTOP or WS_VISIBLE or WS_CHILD or BS_DEFPUSHBUTTON).toUInt(),
					buttonX + buttonWidth + 10 - buttonWidth / 2,
					buttonY,
					buttonWidth,
					buttonHeight,
					window,
					null,
					data.reinterpret(),
					null
				)
			}

			WM_CLOSE -> DestroyWindow(window)
			WM_DESTROY -> PostQuitMessage(0)

			else -> {}
		}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}