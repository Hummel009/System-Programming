package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val width: Int = 660
private const val height: Int = 660

const val rgbWhite: COLORREF = 0x00FFFFFFu

fun circle() {
	memScoped {
		val className = "RenderingCircle"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.hCursor = LoadCursorW(null, IDC_ARROW)
		windowClass.lpfnWndProc = staticCFunction(::circleProc)
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

private fun circleProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	memScoped {
		val ps = alloc<PAINTSTRUCT>()

		when (msg.toInt()) {
			WM_DESTROY -> {
				PostQuitMessage(0)
			}

			WM_SIZE -> {
				val hdc = BeginPaint(window, ps.ptr)
				val brush = CreateSolidBrush(rgbWhite)
				val rc = alloc<RECT>()
				GetClientRect(window, rc.ptr)
				FillRect(hdc, rc.ptr, brush)
				redrawCircle(hdc)
				EndPaint(window, ps.ptr)
			}

			else -> DefWindowProcW(window, msg, wParam, lParam)
		}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

private fun redrawCircle(hdc: HDC?) {
	SaveDC(hdc)

	val cX = 300
	val cY = 300
	val r3 = 240
	val r2 = 190
	val r1 = 140
	var angle = 0.0

	val text = "Lorem ipsum dolor sit amet"
	val textLength = text.length

	Ellipse(hdc, 50, 50, 550, 550)
	Ellipse(hdc, 100, 100, 500, 500)
	Ellipse(hdc, 150, 150, 450, 450)
	Ellipse(hdc, 200, 200, 400, 400)
	Ellipse(hdc, 250, 250, 350, 350)
	Ellipse(hdc, 290, 290, 310, 310)

	for (i in 0 until textLength) {
		val hFont = CreateFontA(
			24,
			0,
			(-(angle + 90) * 10).toInt(),
			0,
			FW_NORMAL,
			FALSE.toUInt(),
			FALSE.toUInt(),
			FALSE.toUInt(),
			ANSI_CHARSET.toUInt(),
			OUT_DEFAULT_PRECIS.toUInt(),
			CLIP_DEFAULT_PRECIS.toUInt(),
			DEFAULT_QUALITY.toUInt(),
			DEFAULT_PITCH.toUInt() or FF_SWISS.toUInt(),
			"Arial"
		)
		SelectObject(hdc, hFont)

		val temp = angle.toRadian()

		var x = cX + r1 * cos(temp)
		var y = cY + r1 * sin(temp)

		TextOutA(hdc, x.toInt(), y.toInt(), "${text[i]}", 1)

		x = cX + r2 * cos(temp)
		y = cY + r2 * sin(temp)

		TextOutA(hdc, x.toInt(), y.toInt(), "${text[i]}", 1)

		x = cX + r3 * cos(temp)
		y = cY + r3 * sin(temp)

		TextOutA(hdc, x.toInt(), y.toInt(), "${text[i]}", 1)

		angle += (360 / textLength - if (angle >= 360) 360 else 0) + 0.5

		DeleteObject(hFont)
	}

	RestoreDC(hdc, -1)
}

private fun Double.toRadian(): Double = this * PI / 180