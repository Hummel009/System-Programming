package hummel.engine

import java.util.*

class DynamicSetting(private val start: Int, private val end: Int, private val period: Int) {
	private val queue: ArrayDeque<Float> = ArrayDeque()
	private var value = 0f

	fun update(magnitudes: FloatArray) {
		val m = avgMag(magnitudes)
		queue.add(m)
		value += m
		if (queue.size >= period) {
			value -= queue.poll()
		}
	}

	fun getValue(): Float = value / period

	private fun avgMag(magnitudes: FloatArray): Float {
		var avg = 0f
		for (i in start until end) {
			avg += magnitudes[i]
		}
		return avg
	}
}