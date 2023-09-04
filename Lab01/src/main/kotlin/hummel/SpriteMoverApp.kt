package hummel

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import kotlin.math.min
import kotlin.system.exitProcess

class SpriteMoverApp : JFrame() {
	private val user32 = User32.INSTANCE

	private val spriteSize = 50
	private val spriteColor = Color.RED
	private val spriteSpeed = 5

	private var spriteX = 0
	private var spriteY = 0
	private var spriteDirectionX = 1
	private var spriteDirectionY = 1

	private var isShiftPressed = false
	private var isHotKeyPressed = false

	private val hotKeyID = 1

	init {
		title = "Sprite Mover App"
		defaultCloseOperation = EXIT_ON_CLOSE
		isResizable = false
		setSize(800, 600)
		setLocationRelativeTo(null)
		addKeyListener(object : KeyListener {
			override fun keyTyped(e: KeyEvent?) {}

			override fun keyPressed(e: KeyEvent?) {
				e?.let {
					when (it.keyCode) {
						KeyEvent.VK_SHIFT -> isShiftPressed = true
						else -> handleArrowKeys(it)
					}
				}
			}

			override fun keyReleased(e: KeyEvent?) {
				e?.let {
					when (it.keyCode) {
						KeyEvent.VK_SHIFT -> isShiftPressed = false
					}
				}
			}
		})
		Timer(16, { moveSprite() }).start()
	}

	private fun handleArrowKeys(e: KeyEvent) {
		when (e.keyCode) {
			KeyEvent.VK_UP -> spriteY -= spriteSpeed
			KeyEvent.VK_DOWN -> spriteY += spriteSpeed
			KeyEvent.VK_LEFT -> spriteX -= spriteSpeed
			KeyEvent.VK_RIGHT -> spriteX += spriteSpeed
		}
		spriteX = min(maxOf(spriteX, 0), width - spriteSize)
		spriteY = min(maxOf(spriteY, 0), height - spriteSize)
		repaint()
	}

	private fun moveSprite() {
		if (isShiftPressed) {
			if (spriteX <= 0 || spriteX >= width - spriteSize) {
				spriteDirectionX *= -1
			}
			if (spriteY <= 0 || spriteY >= height - spriteSize) {
				spriteDirectionY *= -1
			}
			spriteX += spriteDirectionX * spriteSpeed
			spriteY += spriteDirectionY * spriteSpeed
		}
		repaint()
	}

	override fun paint(g: Graphics) {
		super.paint(g)
		g.color = spriteColor
		g.fillRect(spriteX, spriteY, spriteSize, spriteSize)
	}
}