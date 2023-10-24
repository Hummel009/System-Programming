import kotlinx.cinterop.*
import platform.windows.*

var NUM_BUFFERS: Int = 3

val log: MutableMap<String, String> = mutableMapOf()

fun main() {
	memScoped {
		val hWaveIn = alloc<HWAVEINVar>()
		val waveHeader = allocArray<WAVEHDR>(NUM_BUFFERS)

		val wfx = alloc<WAVEFORMATEX>()

		"waveInOpen" to waveInOpen(
			hWaveIn.ptr, WAVE_MAPPER, wfx.ptr, 0u, 0u, CALLBACK_FUNCTION.toUInt()
		)

		for (i in 0 until NUM_BUFFERS) {
			"waveInPrepareHeader" to waveInPrepareHeader(
				hWaveIn.value, waveHeader[i].ptr, sizeOf<WAVEHDR>().toUInt()
			)

			"waveInAddBuffer" to waveInAddBuffer(
				hWaveIn.value, waveHeader[i].ptr, sizeOf<WAVEHDR>().toUInt()
			)
		}

		"waveInStart" to waveInStart(hWaveIn.value)

		"waveInStop" to waveInStop(hWaveIn.value)

		"waveInClose" to waveInClose(hWaveIn.value)

		log.forEach { (key, value) -> println("$key: $value") }
	}
}

private infix fun String.to(signal: UInt) {
	log[this] = if (signal.toInt() == MMSYSERR_NOERROR) "OK" else signal.toString()
}