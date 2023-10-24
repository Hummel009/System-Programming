import kotlinx.cinterop.*
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.windows.*

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

		val wavFile = fopen("output.wav", "w") ?: error("Cannot open output.wav for writing")

		writeWaveHeader(wavFile, wfx, NUM_BUFFERS)

		for (i in 0 until NUM_BUFFERS) {
			while ((waveHeader[i].dwFlags and WHDR_DONE.toUInt()) == 0u) {
				Sleep(5000u)
			}
			fwrite(waveHeader[i].lpData, 1u, waveHeader[i].dwBufferLength.convert(), wavFile)
		}

		"waveInStop" to waveInStop(hWaveIn.value)

		fclose(wavFile)

		"waveInClose" to waveInClose(hWaveIn.value)

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
	fwrite((headerSize + dataSize).toString().cstr, 1u, 4u, file)
	fwrite("WAVEfmt ".cstr, 1u, 8u, file)
	fwrite("16".cstr, 1u, 4u, file)

	fwrite(wfx.ptr, 1u, sizeOf<WAVEFORMATEX>().toULong(), file)

	fwrite("data".cstr, 1u, 4u, file)
	fwrite(dataSize.toString().cstr, 1u, 4u, file)
}