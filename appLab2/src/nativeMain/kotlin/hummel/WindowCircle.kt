package hummel

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val width: Int = 660
private const val height: Int = 660

fun circle() {
	memScoped {
		val className = "RenderingCircle"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.lpszClassName = className.wcstr.ptr
		windowClass.hbrBackground = (COLOR_WINDOW + 1).toLong().toCPointer()

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
		WM_PAINT -> {
			memScoped {
				val paintStructure = alloc<PAINTSTRUCT>()
				val deviceContext = BeginPaint(window, paintStructure.ptr)
				redrawCircle(deviceContext)
				EndPaint(window, paintStructure.ptr)
			}
		}

		WM_CLOSE -> DestroyWindow(window)
		WM_DESTROY -> PostQuitMessage(0)
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

private fun redrawCircle(deviceContext: HDC?) {
	SaveDC(deviceContext)

	val cX = 300
	val cY = 300
	val r3 = 240
	val r2 = 190
	val r1 = 140
	var angle = 0.0

	val text = "Lorem ipsum dolor sit amet"
	val textLength = text.length

	Ellipse(deviceContext, 50, 50, 550, 550)
	Ellipse(deviceContext, 100, 100, 500, 500)
	Ellipse(deviceContext, 150, 150, 450, 450)
	Ellipse(deviceContext, 200, 200, 400, 400)
	Ellipse(deviceContext, 250, 250, 350, 350)
	Ellipse(deviceContext, 290, 290, 310, 310)

	for (i in 0 until textLength) {
		val font = CreateFontW(
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
		SelectObject(deviceContext, font)

		val temp = angle.toRadian()

		var x = cX + r1 * cos(temp)
		var y = cY + r1 * sin(temp)

		TextOutW(deviceContext, x.toInt(), y.toInt(), "${text[i]}", 1)

		x = cX + r2 * cos(temp)
		y = cY + r2 * sin(temp)

		TextOutW(deviceContext, x.toInt(), y.toInt(), "${text[i]}", 1)

		x = cX + r3 * cos(temp)
		y = cY + r3 * sin(temp)

		TextOutW(deviceContext, x.toInt(), y.toInt(), "${text[i]}", 1)

		angle += (360 / textLength - if (angle >= 360) 360 else 0) + 0.5

		DeleteObject(font)
	}

	RestoreDC(deviceContext, -1)
}

private fun Double.toRadian(): Double = this * PI / 180