package hummel.app

import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import kotlin.math.pow

interface Updatable {
	fun update(magnitudes: FloatArray)

	fun normalized(f: Float): Float {
		return (f / 100f).coerceIn(0.0f, 0.99f)
	}

	fun height(magnitude: Float): Float {
		return ((magnitude + 90).toDouble().pow(2.3).toFloat() / 40).coerceAtLeast(0f)
	}

	fun createLabel(value: String, layoutX: Double): Label {
		val bottomText = Label(value)
		bottomText.textFill = Color.WHITE
		bottomText.font = Font("Arial", 20.0)
		bottomText.layoutX = layoutX
		return bottomText
	}
}