import kotlinx.cinterop.*
import platform.windows.*
import platform.posix.*

var NUM_BUFFERS: Int = 3

val log: MutableMap<String, String> = mutableMapOf()

fun main() {
	memScoped {
		val hWaveIn = alloc<HWAVEINVar>()
		val waveHeader = allocArray<WAVEHDR>(NUM_BUFFERS)

		val wfx = alloc<WAVEFORMATEX>()
		wfx.wFormatTag = WAVE_FORMAT_PCM.toUShort() // or appropriate format tag
		wfx.nChannels = 2u // number of channels (1 for mono, 2 for stereo, etc.)
		wfx.nSamplesPerSec = 44100u // sample rate (Hz)
		wfx.nAvgBytesPerSec =
			(44100 * 2 * 16 / 8).toUInt() // average bytes per second (sample rate * number of channels * bits per sample / 8)
		wfx.nBlockAlign = (2 * 16 / 8).toUShort() // block align (number of channels * bits per sample / 8)
		wfx.wBitsPerSample = 16u // bits per sample
		wfx.cbSize = 0u // size of extra information (0 for PCM)

		val devices = waveInGetNumDevs()
		println("There are $devices microphones.")

		"waveInOpen" to waveInOpen(
			hWaveIn.ptr, WAVE_MAPPER, wfx.ptr, 0u, 0u, CALLBACK_NULL.toUInt()
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

		val wavFile = fopen("output.wav", "wb") ?: error("Cannot open output.wav for writing")

		writeWaveHeader(wavFile, wfx, NUM_BUFFERS)

		for (i in 0 until NUM_BUFFERS) {
			fwrite(waveHeader[i].lpData, 1u, waveHeader[i].dwBufferLength.convert(), wavFile)
		}

		fclose(wavFile)

		log.forEach { (key, value) -> println("$key: $value") }
	}
}

private infix fun String.to(signal: UInt) {
	log[this] = if (signal.toInt() == MMSYSERR_NOERROR) "OK" else "ERROR CODE: $signal"
}

fun writeWaveHeader(file: CPointer<FILE>, wfx: WAVEFORMATEX, numBuffers: Int) {
	val headerSize = 44
	val dataSize = NUM_BUFFERS * sizeOf<WAVEHDR>()

	fwrite("RIFF".cstr, 1u, 4u, file)
	fwrite((headerSize + dataSize), 1u, 4u, file)
	fwrite("WAVEfmt ".cstr, 1u, 8u, file)
	fwrite(16, 1u, 4u, file)

	// Write the WAVEFORMATEX structure
	fwrite(wfx.ptr, 1u, sizeOf<WAVEFORMATEX>().toULong(), file)

	// Write the "data" chunk header
	fwrite("data".cstr, 1u, 4u, file)
	fwrite(dataSize, 1u, 4u, file)
}

fun UInt.toBytes(): ByteArray {
	return ByteArray(4) { i -> (this shr (i * 8)).toByte() }
}