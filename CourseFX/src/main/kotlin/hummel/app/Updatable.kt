package hummel.app

import kotlin.math.pow

interface Updatable {
	fun update(magnitudes: FloatArray)

	fun normalized(f: Float): Float {
		return (f / 100f).coerceIn(0.0f, 0.99f)
	}

	fun height(magnitude: Float): Float {
		return ((magnitude + 90).toDouble().pow(2.3).toFloat() / 40).coerceAtLeast(0f)
	}
}