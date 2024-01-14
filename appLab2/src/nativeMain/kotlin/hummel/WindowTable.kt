package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max
import kotlin.random.Random

private const val width: Int = 660
private const val height: Int = 660

private const val rgbWhite: COLORREF = 0x00FFFFFFu

private const val n: Int = 5
private const val m: Int = 4

private val tableData: Array<Array<String>> =
	Array(n) { Array(m) { (1..Random.nextInt(3) + 1).joinToString("\r\n") { "Text" } } }

fun table() {
	memScoped {
		val className = "RenderingTable"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.style = (CS_HREDRAW or CS_VREDRAW).toUInt()
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

		CreateWindowExW(
			0u,
			className,
			windowTitle,
			(WS_VISIBLE or WS_CAPTION or WS_SYSMENU or WS_SIZEBOX).toUInt(),
			windowX,
			windowY,
			windowWidth,
			windowHeight,
			null,
			null,
			null,
			null
		)

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}
	}
}

private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	when (msg.toInt()) {
		WM_SIZE -> {
			InvalidateRect(window, null, FALSE)
		}

		WM_PAINT -> {
			memScoped {
				val paintStructure = alloc<PAINTSTRUCT>()
				val deviceContext = BeginPaint(window, paintStructure.ptr)
				val brush = CreateSolidBrush(rgbWhite)
				val square = alloc<RECT>()
				GetClientRect(window, square.ptr)
				FillRect(deviceContext, square.ptr, brush)
				redrawTable(deviceContext, square)
				EndPaint(window, paintStructure.ptr)
			}
		}

		WM_CLOSE -> DestroyWindow(window)
		WM_DESTROY -> PostQuitMessage(0)
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

private fun redrawTable(deviceContext: HDC?, square: RECT) {
	memScoped {
		val cellWidth = square.right / m
		val cellHeight = square.bottom / n

		for (row in 0 until n) {
			for (col in 0 until m) {
				val cell = alloc<RECT>()
				cell.left = col * cellWidth
				cell.top = row * cellHeight
				cell.right = (col + 1) * cellWidth
				cell.bottom = (row + 1) * cellHeight

				if (row < tableData.size && col < tableData[row].size) {
					DrawTextW(
						deviceContext, tableData[row][col], -1, cell.ptr, (DT_CENTER or DT_VCENTER).toUInt()
					)
				}
			}
		}
	}
}