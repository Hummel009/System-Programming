package hummel.engine

import hummel.app.WindowSize
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.Bloom
import javafx.scene.effect.Reflection
import javafx.scene.paint.Color
import java.lang.Double.sum
import java.util.stream.IntStream
import kotlin.math.pow

class Visualization(windowSize: WindowSize) : Group() {
	private var canvas: Canvas = Canvas()
	private var gc: GraphicsContext
	private var bloom: Bloom
	private var reflection: Reflection
	private var heights: FloatArray
	private var lazyHeights: FloatArray
	private var offsetter: FloatArray
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
		gc = canvas.getGraphicsContext2D()
		bloom = Bloom()
		canvas.effect = bloom
		length = 256
		offsetter = offsettingMap(length)
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
		canvas.translateXProperty().bind(windowSize.width.subtract(canvas.width).divide(2))
		canvas.translateYProperty().bind(windowSize.height.subtract(canvas.height).divide(2))
		children.add(canvas)
	}

	fun update(magnitudes: FloatArray) {
		if (!isVisible) {
			return
		}
		bloom.threshold = controls[3].toDouble()
		val heightMult = (controls[2] / 2).toDouble()
		gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
		val avg = normalized(avg(magnitudes.copyOf(length)).toFloat())
		gc.stroke = Color.hsb((avg * controls[4] + controls[5]).toDouble(), 1.0, 1.0)
		gc.lineWidth = (avg * 6).toDouble()
		gc.beginPath()
		for (i in length - 1 downTo 0) {
			val oldHeight = heights[i]
			var newHeight = oldHeight - (oldHeight - height(magnitudes[i])) / controls[1]
			heights[i] = newHeight
			lazyHeights[i] = if (newHeight > lazyHeights[i]) newHeight else lazyHeights[i] * 0.99f
			newHeight *= heightMult.toFloat()
			val x = centerX - width * offsetter[i]
			val y = rootHeight - newHeight
			gc.lineTo(x.toDouble(), y.toDouble())
		}
		for (i in 0 until length) {
			val x = centerX + width * offsetter[i]
			val y = rootHeight - heights[i] * heightMult.toFloat()
			gc.lineTo(x.toDouble(), y.toDouble())
		}
		gc.stroke()
		gc.closePath()
	}

	private fun normalized(f: Float): Float {
		val n = f / 100f
		return if (n > 0.99f) 0.99f else n
	}

	private fun height(magnitude: Float): Float {
		val f = (magnitude + 90).toDouble().pow(2.3).toFloat() / 40
		return if (f > 0) f else 0f
	}

	private fun avg(array: FloatArray): Double {
		return IntStream.range(0, array.size).mapToDouble { i: Int ->
			array[i].toDouble()
		}.parallel().reduce(0.0) { a: Double, b: Double ->
			sum(a, b)
		} / array.size
	}

	private fun offsettingMap(length: Int): FloatArray {
		val map = FloatArray(length)
		for (i in 0 until length) {
			map[i] = i * ((i - 128).toDouble().pow(2.0).toFloat() / 10000.0f + 1)
		}
		return map
	}
}