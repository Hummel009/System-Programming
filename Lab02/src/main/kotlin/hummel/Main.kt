package hummel

import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Color
import java.awt.EventQueue
import java.awt.FlowLayout
import java.awt.Toolkit
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.UIManager
import kotlin.math.cos
import kotlin.math.sin

const val n: Int = 5
const val m: Int = 4

const val DT_CENTER: Int = 0x00000001
const val DT_VCENTER: Int = 0x00000004
const val CS_VREDRAW: Int = 0x0001
const val CS_HREDRAW: Int = 0x0002
const val GM_ADVANCED: Int = 2

val rand: Random = Random()
val tableData: Array<Array<String>> = Array(n) { Array(m) { (1..rand.nextInt(3) + 1).joinToString("\r\n") { "Text" } } }

fun main() {
	EventQueue.invokeLater {
		try {
			for (info in UIManager.getInstalledLookAndFeels()) {
				if ("Windows Classic" == info.name) {
					UIManager.setLookAndFeel(info.className)
					break
				}
			}
			val frame = JFrame("Window Launcher")
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
			frame.setSize(350, 150)
			val button1 = JButton("Launch: Table with the text")
			button1.addActionListener { launchTable() }
			val button2 = JButton("Launch: Circle with the text")
			button2.addActionListener { launchCircle() }
			val panel = JPanel()
			panel.setLayout(FlowLayout())
			panel.add(button1)
			panel.add(button2)
			frame.add(panel)
			frame.setLocationRelativeTo(null)
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

private fun launchCircle() {
	val className = "RenderingCircle"
	val windowTitle = "Windows API: Kotlin + JNA"

	val wc = WNDCLASSEX()
	wc.hInstance = null
	wc.lpszClassName = className
	wc.style = CS_HREDRAW or CS_VREDRAW
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

			WM_SIZE -> {
				val hdc = ExUser32.INSTANCE.BeginPaint(hWnd, ps)
				val brush = ExGDI32.INSTANCE.CreateSolidBrush(Color.WHITE.toDword())
				val rc = RECT()
				ExUser32.INSTANCE.GetClientRect(hWnd, rc)
				ExUser32.INSTANCE.FillRect(hdc, rc, brush)
				redrawCircle(hdc)
				ExUser32.INSTANCE.EndPaint(hWnd, ps)
				LRESULT(0)
			}

			else -> ExUser32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam)
		}
	}

	ExUser32.INSTANCE.RegisterClassEx(wc)

	val hWnd = createWindowInCenter(className, windowTitle, 720, 720)

	ExUser32.INSTANCE.ShowWindow(hWnd, SW_SHOW)
	ExUser32.INSTANCE.UpdateWindow(hWnd)

	val msg = MSG()
	while (ExUser32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		ExUser32.INSTANCE.TranslateMessage(msg)
		ExUser32.INSTANCE.DispatchMessage(msg)
	}
}

private fun launchTable() {
	val className = "RenderingTable"
	val windowTitle = "Windows API: Kotlin + JNA"

	val wc = WNDCLASSEX()
	wc.hInstance = null
	wc.lpszClassName = className
	wc.style = CS_HREDRAW or CS_VREDRAW
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

			WM_SIZE -> {
				val hdc = ExUser32.INSTANCE.BeginPaint(hWnd, ps)
				val brush = ExGDI32.INSTANCE.CreateSolidBrush(Color.WHITE.toDword())
				val rc = RECT()
				ExUser32.INSTANCE.GetClientRect(hWnd, rc)
				ExUser32.INSTANCE.FillRect(hdc, rc, brush)
				redrawTable(hdc, rc)
				ExUser32.INSTANCE.EndPaint(hWnd, ps)
				LRESULT(0)
			}

			else -> ExUser32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam)
		}
	}

	ExUser32.INSTANCE.RegisterClassEx(wc)

	val hWnd = createWindowInCenter(className, windowTitle, 1280, 720)

	ExUser32.INSTANCE.ShowWindow(hWnd, SW_SHOW)
	ExUser32.INSTANCE.UpdateWindow(hWnd)

	val msg = MSG()
	while (ExUser32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
		ExUser32.INSTANCE.TranslateMessage(msg)
		ExUser32.INSTANCE.DispatchMessage(msg)
	}
}

private fun redrawCircle(hdc: HDC?) {
	val text = "Lorem ipsum dolor sit amet"
	val textLength = text.length

	val radius = 200
	val xForm = ExGDI32.XFORM()
	xForm.eDx = 360.toFloat()
	xForm.eDy = 360.toFloat()
	var angle = 0

	for (i in 0 until textLength) {
		val temp = angle.toRadian()
		xForm.eM11 = cos(temp).toFloat()
		xForm.eM12 = sin(temp).toFloat()
		xForm.eM21 = -sin(temp).toFloat()
		xForm.eM22 = cos(temp).toFloat()
		ExGDI32.INSTANCE.SetGraphicsMode(hdc, GM_ADVANCED)
		ExGDI32.INSTANCE.SetWorldTransform(hdc, xForm)

		angle += 360 / textLength - if (angle >= 360) 360 else 0

		ExGDI32.INSTANCE.TextOutA(hdc, radius, 0, text[i].toString(), 1)
	}
}

private fun redrawTable(hdc: HDC?, rc: RECT) {
	val cellWidth = rc.right / m
	val cellHeight = rc.bottom / n

	for (row in 0 until n) {
		for (col in 0 until m) {
			val cellRect = RECT()
			cellRect.left = col * cellWidth
			cellRect.top = row * cellHeight
			cellRect.right = (col + 1) * cellWidth
			cellRect.bottom = (row + 1) * cellHeight

			if (row < tableData.size && col < tableData[row].size) {
				ExUser32.INSTANCE.DrawText(
					hdc, tableData[row][col], -1, cellRect, DT_CENTER or DT_VCENTER
				)
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

private fun Color.toDword(): DWORD = DWORD((blue shl 16 or (green shl 8) or red).toLong())

private fun Int.toRadian(): Double = this * 3.14159265359 / 180