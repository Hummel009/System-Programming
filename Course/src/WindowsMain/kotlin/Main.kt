import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.*

val log: MutableMap<String, String> = mutableMapOf()

fun main() {
	memScoped {
		val devices = waveInGetNumDevs()
		println("There are $devices microphones.")

		val hwi = alloc<HWAVEINVar>()
		val wfx = alloc<WAVEFORMATEX>()
		wfx.wFormatTag = WAVE_FORMAT_PCM.toUShort()
		wfx.nChannels = 2u
		wfx.nSamplesPerSec = 44100u
		wfx.nAvgBytesPerSec = (44100 * 2 * 16 / 8).toUInt()
		wfx.nBlockAlign = (2 * 16 / 8).toUShort()
		wfx.wBitsPerSample = 16u
		wfx.cbSize = 0u

		"waveInOpen" to waveInOpen(
			hwi.ptr, WAVE_MAPPER, wfx.ptr, 0u, 0u, CALLBACK_NULL.toUInt()
		)

		val wh = alloc<WAVEHDR>()
		val bufferSize = 10000000
		val buffer = allocArray<ShortVar>(bufferSize)

		wh.lpData = buffer.reinterpret()
		wh.dwBufferLength = bufferSize.toUInt()

		"waveInPrepareHeader" to waveInPrepareHeader(
			hwi.value, wh.ptr, sizeOf<WAVEHDR>().toUInt()
		)

		"waveInAddBuffer" to waveInAddBuffer(
			hwi.value, wh.ptr, sizeOf<WAVEHDR>().toUInt()
		)

		"waveInStart" to waveInStart(hwi.value)

		println("START SPEAKING!")

		Sleep(5000u)

		"waveInStop" to waveInStop(hwi.value)

		"waveInClose" to waveInClose(hwi.value)

		log.forEach { (key, value) -> println("$key: $value") }

		println("Recorded: ${wh.dwBytesRecorded / 1000u} kbytes")
	}
}

private infix fun String.to(signal: UInt) {
	log[this] = if (signal.toInt() == MMSYSERR_NOERROR) "OK" else "ERROR CODE: $signal"
}