package hummel.app

import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.Bloom
import javafx.scene.effect.Reflection
import javafx.scene.paint.Color

class VisSym : Group(), Updatable {
	private var canvas: Canvas = Canvas()
	private var gc: GraphicsContext
	private var bloom: Bloom
	private var reflection: Reflection
	private var heights: FloatArray
	private var lazyHeights: FloatArray
	private var length: Int = 256
	private var width: Float = 4f
	private var rootHeight: Float
	private var centerX: Float
	private var controls: FloatArray = floatArrayOf(
		-60.0f,  // threshold
		2.0f,    // acceleration
		1.5f,    // height
		2.0f,    // bloom
		86.0f,   // magnitude
		0.0f     // color offset
	)

	init {
		canvas.width = 2560.0
		canvas.height = 1440.0
		canvas.isCache = true
		gc = canvas.graphicsContext2D
		bloom = Bloom()
		canvas.effect = bloom
		length = 256
		heights = FloatArray(length)
		lazyHeights = FloatArray(length)
		rootHeight = 0.5f * canvas.height.toFloat()
		centerX = 0.5f * canvas.width.toFloat()
		reflection = Reflection()
		reflection.topOffset = -canvas.height
		reflection.topOpacity = 1.0
		reflection.bottomOpacity = 1.0
		reflection.fraction = 1.0
		effect = reflection
		canvas.translateX = -640.0
		canvas.translateY = -360.0
		children.add(canvas)

		children.addAll(
			createLabel("2.0", 70.0),
			createLabel("1.0", 255.0),
			createLabel("0.5", 440.0),
			createLabel("0.0", 625.0),
			createLabel("0.5", 810.0),
			createLabel("1.0", 995.0),
			createLabel("2.0", 1180.0)
		)
	}

	override fun update(magnitudes: FloatArray) {
		if (!isVisible) {
			return
		}
		bloom.threshold = controls[3].toDouble()
		val heightMult = (controls[2] / 2).toDouble()
		gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
		val avg = normalized(magnitudes.copyOf(length).average().toFloat())
		gc.stroke = Color.hsb((avg * controls[4] + controls[5]).toDouble(), 1.0, 1.0)
		gc.lineWidth = (avg * 6).toDouble()
		gc.beginPath()
		for (i in length - 1 downTo 0) {
			val oldHeight = heights[i]
			var newHeight = oldHeight - (oldHeight - height(magnitudes[i])) / controls[1]
			heights[i] = newHeight
			lazyHeights[i] = if (newHeight > lazyHeights[i]) newHeight else lazyHeights[i] * 0.99f
			newHeight *= heightMult.toFloat()
			val x = centerX - width * i * 2
			val y = rootHeight - newHeight
			gc.lineTo(x.toDouble(), y.toDouble())
		}
		for (i in 0 until length) {
			val x = centerX + width * i * 2
			val y = rootHeight - heights[i] * heightMult.toFloat()
			gc.lineTo(x.toDouble(), y.toDouble())
		}
		gc.stroke()
		gc.closePath()
	}
}