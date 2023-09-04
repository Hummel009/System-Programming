package hummel

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import java.awt.event.KeyEvent

class SpriteWindow {
	private val user32 = Native.load("user32", User32::class.java)

	private val windowClassName = "SpriteWindowClass"
	private val windowTitle = "Sprite Window"
	private val windowWidth = 800
	private val windowHeight = 600

	private val spriteWidth = 50
	private val spriteHeight = 50

	private val spriteColor = 0xFF0000

	private var spriteX = 0
	private var spriteY = 0
	private var spriteXVelocity = 5
	private var spriteYVelocity = 5

	private var isMovingHorizontally = false

	init {
		// Регистрируем класс окна
		val wndClass = WinUser.WNDCLASSEX()
		wndClass.lpfnWndProc = WinUser.WindowProc { hwnd, uMsg, wParam, lParam ->
			when (uMsg) {
				WinUser.WM_PAINT -> {
					drawSprite(hwnd)
					user32.DefWindowProc(hwnd, uMsg, wParam, lParam)
				}

				WinUser.WM_CLOSE -> {
					user32.PostQuitMessage(0)
					WinDef.LRESULT(0)
				}

				WinUser.WM_KEYDOWN -> {
					handleKeyPress(wParam.toInt())
					WinDef.LRESULT(0)
				}

				0x020A /* WM_MOUSEWHEEL */ -> {
					handleMouseWheel(wParam, lParam)
					WinDef.LRESULT(0)
				}

				else -> {
					user32.DefWindowProc(hwnd, uMsg, wParam, lParam)
				}
			}
		}
		wndClass.hInstance = null
		wndClass.lpszClassName = windowClassName
		user32.RegisterClassEx(wndClass)

		// Получаем размер экрана
		val screen = User32.INSTANCE.GetDesktopWindow()
		val screenRect = WinDef.RECT()
		User32.INSTANCE.GetWindowRect(screen, screenRect)

		// Центрируем окно на экране
		val windowX = (screenRect.right - screenRect.left - windowWidth) / 2
		val windowY = (screenRect.bottom - screenRect.top - windowHeight) / 2

		// Создаем окно
		val hwnd = user32.CreateWindowEx(
			0,
			windowClassName,
			windowTitle,
			WinUser.WS_OVERLAPPEDWINDOW or WinUser.WS_VISIBLE,
			windowX,
			windowY,
			windowWidth,
			windowHeight,
			null,
			null,
			null,
			null
		)

		// Отображаем и обновляем окно
		user32.ShowWindow(hwnd, WinUser.SW_SHOW)
		user32.UpdateWindow(hwnd)

		// Главный цикл обработки сообщений
		val msg = WinUser.MSG()
		while (user32.GetMessage(msg, null, 0, 0) != 0) {
			user32.TranslateMessage(msg)
			user32.DispatchMessage(msg)
		}
	}

	private fun drawSprite(hwnd: WinDef.HWND) {
		val hdc = user32.GetDC(hwnd)
		val rect = WinDef.RECT()
		user32.GetClientRect(hwnd, rect)

		if (isMovingHorizontally) {
			spriteX += spriteXVelocity
		} else {
			spriteY += spriteYVelocity
		}

		if (spriteX < 0 || spriteX + spriteWidth > rect.right) {
			spriteXVelocity = -spriteXVelocity
			isMovingHorizontally = true
		}

		if (spriteY < 0 || spriteY + spriteHeight > rect.bottom) {
			spriteYVelocity = -spriteYVelocity
			isMovingHorizontally = false
		}

		val spriteRect = WinDef.RECT()
		spriteRect.left = spriteX
		spriteRect.top = spriteY
		spriteRect.right = spriteX + spriteWidth
		spriteRect.bottom = spriteY + spriteHeight

		user32.ReleaseDC(hwnd, hdc)
	}

	private fun handleKeyPress(keyCode: Int) {
		when (keyCode) {
			KeyEvent.VK_ESCAPE -> user32.PostQuitMessage(0)
			KeyEvent.VK_LEFT -> spriteXVelocity = -5
			KeyEvent.VK_RIGHT -> spriteXVelocity = 5
			KeyEvent.VK_UP -> spriteYVelocity = -5
			KeyEvent.VK_DOWN -> spriteYVelocity = 5
		}
	}

	private fun handleMouseWheel(wParam: WinDef.WPARAM, lParam: WinDef.LPARAM) {
		val wheelDelta = wParam.toLong()

		val highWord = (wheelDelta shr 16).toInt() // Extract the high word
		val lowWord = (wheelDelta and 0xFFFF).toInt() // Extract the low word

		val isCtrlPressed = KeyEvent.getKeyModifiersText(KeyEvent.CTRL_DOWN_MASK) != "Ctrl"
		val isShiftPressed = KeyEvent.getKeyModifiersText(KeyEvent.SHIFT_DOWN_MASK) != "Shift"

		if (isShiftPressed) {
			// Shift key is pressed, scroll horizontally
			spriteXVelocity = if (isCtrlPressed) {
				// Ctrl key is also pressed, scroll faster horizontally
				if (lowWord > 0) 10 else -10
			} else {
				if (lowWord > 0) 5 else -5
			}
			isMovingHorizontally = true
		} else {
			// Scroll vertically
			spriteYVelocity = if (isCtrlPressed) {
				// Ctrl key is pressed, scroll faster vertically
				if (lowWord > 0) 10 else -10
			} else {
				if (lowWord > 0) 5 else -5
			}
			isMovingHorizontally = false
		}
	}
}

fun main() {
	SpriteWindow()
}