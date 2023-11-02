package hummel

import kotlinx.cinterop.*
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.windows.*

val log: MutableMap<String, String> = mutableMapOf()

fun main(args: Array<String>) {
	require(args.size == 2) { "Invalid arguments quantity" }

	val seconds = args[0].toUInt() * 1000u
	val path = args[1]

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

		"waveInOpen" to waveInOpen(hwi.ptr, WAVE_MAPPER, wfx.ptr, 0u, 0u, CALLBACK_NULL.toUInt())

		val wh = alloc<WAVEHDR>()
		val bufferSize = 10000000
		val buffer = allocArray<ShortVar>(bufferSize)

		wh.lpData = buffer.reinterpret()
		wh.dwBufferLength = bufferSize.toUInt()

		"waveInPrepareHeader" to waveInPrepareHeader(hwi.value, wh.ptr, sizeOf<WAVEHDR>().toUInt())
		"waveInAddBuffer" to waveInAddBuffer(hwi.value, wh.ptr, sizeOf<WAVEHDR>().toUInt())
		"waveInStart" to waveInStart(hwi.value)

		println("Recording started!")

		Sleep(seconds)

		"waveInStop" to waveInStop(hwi.value)
		"waveInClose" to waveInClose(hwi.value)

		val outputFile = fopen(path, "wb")

		outputFile?.let {
			writeWavHeader(it, wfx.nChannels, wfx.nSamplesPerSec, wfx.wBitsPerSample, wh.dwBytesRecorded)
			fwrite(buffer, 1u, wh.dwBytesRecorded.toULong(), it)
			fclose(it)
		} ?: run {
			println("Error opening file $path")
		}

		log.forEach { (key, value) -> println("$key: $value") }

		println("Recorded: ${wh.dwBytesRecorded / 1000u} kbytes")
	}
}

private infix fun String.to(signal: UInt) {
	log[this] = if (signal.toInt() == MMSYSERR_NOERROR) "OK" else "ERROR CODE: $signal"
}

private fun writeWavHeader(
	outputStream: CPointer<FILE>, channels: UShort, sampleRate: UInt, bitsPerSample: UShort, dataSize: UInt
) {
	fwrite("RIFF".ref(), 1u, 4u, outputStream)
	fwrite((36u + dataSize).ref(), 1u, 4u, outputStream)
	fwrite("WAVE".ref(), 1u, 4u, outputStream)
	fwrite("fmt ".ref(), 1u, 4u, outputStream)
	fwrite(16.ref(), 1u, 4u, outputStream)
	fwrite(1.ref(), 1u, 2u, outputStream)
	fwrite(channels.ref(), 1u, 2u, outputStream)
	fwrite(sampleRate.ref(), 1u, 4u, outputStream)
	fwrite((sampleRate * channels * bitsPerSample / 8u).ref(), 1u, 4u, outputStream)
	fwrite((channels * bitsPerSample / 8u).ref(), 1u, 2u, outputStream)
	fwrite(bitsPerSample.ref(), 1u, 2u, outputStream)
	fwrite("data".ref(), 1u, 4u, outputStream)
	fwrite(dataSize.ref(), 1u, 4u, outputStream)
}

private fun UShort.ref(): CValuesRef<*> = UShortArray(1) { this }.refTo(0)

private fun Int.ref(): CValuesRef<*> = IntArray(1) { this }.refTo(0)

private fun String.ref(): CValuesRef<*> = cstr.getBytes().refTo(0)

private fun UInt.ref(): CValuesRef<*> = UIntArray(1) { this }.refTo(0)
