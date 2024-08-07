package com.github.hummel.sp.course.app

import com.github.hummel.sp.course.app.VisBar.Bar.Vector2D
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.Bloom
import javafx.scene.effect.Reflection
import javafx.scene.paint.Color

class VisBar : Group(), Updatable {
	private var canvas: Canvas = Canvas()
	private var gc: GraphicsContext
	private var bloom: Bloom
	private var reflection: Reflection
	private var bars: Array<Bar>
	private var length = 0
	private var rootHeight = 0f
	private var controls: FloatArray = floatArrayOf(
		-60.0f,  // threshold
		2.0f,    // acceleration
		1.5f,    // height
		2.0f,    // bloom
		144.0f,  // frequency color offset
		86.0f,   // magnitude color offset
		0.0f,    // color offset
		0.5f     // opacity
	)

	init {
		canvas.width = 2560.0
		canvas.height = 1440.0
		canvas.isCache = true
		gc = canvas.graphicsContext2D
		bloom = Bloom()
		canvas.effect = bloom
		reflection = Reflection()
		reflection.topOffset = -canvas.height
		reflection.topOpacity = 1.0
		reflection.bottomOpacity = 1.0
		reflection.fraction = 1.0
		effect = reflection
		canvas.translateY = -360.0
		children.add(canvas)
		length = 128
		rootHeight = 0.5f * canvas.height.toFloat()

		bars = Array(64) {
			Bar(
				Vector2D(canvas.width.toFloat() / length * it, rootHeight),
				Vector2D(canvas.width.toFloat() / length * 0.75f, 0f)
			)
		}

		children.addAll(
			createLabel("0.0", 0.0), createLabel("0.5", 462.5), createLabel("1.0", 925.0)
		)
	}

	override fun update(magnitudes: FloatArray) {
		if (!this.isVisible) {
			return
		}
		gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
		gc.fill = Color.WHITE
		for (i in 0 until 64) {
			val oldHeight: Float = bars[i].size.y
			var newHeight = oldHeight - (oldHeight - height(magnitudes[i])) / controls[1]
			bars[i].size.y = newHeight
			val normalHeight = normalized(newHeight)
			newHeight *= controls[2] / 2
			bloom.threshold = controls[3].toDouble()
			gc.fill = Color.hsb(
				i * 1.0 / length * controls[4] + controls[5] * normalHeight + controls[6],
				1.0,
				normalHeight.toDouble(),
				controls[7].toDouble()
			)
			val x = bars[i].pos.x + 2
			val y = rootHeight - newHeight
			gc.fillRect(x.toDouble(), y.toDouble(), (bars[i].size.x).toDouble(), newHeight.toDouble())
		}
	}

	class Bar(val pos: Vector2D, val size: Vector2D) {
		class Vector2D(var x: Float, var y: Float)
	}
}