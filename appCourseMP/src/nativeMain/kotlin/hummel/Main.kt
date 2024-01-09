package hummel

import kotlinx.cinterop.*
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.windows.*
import kotlin.system.exitProcess

val log: MutableMap<String, String> = mutableMapOf()

fun main(args: Array<String>) {
	require(args.size == 2) { "Ошибка: неверные аргументы!" }

	val seconds = args[0].toUInt() * 1000u
	val path = args[1]

	memScoped {
		val devices = waveInGetNumDevs()
		println("Доступно следующее количество микрофонов: $devices.")

		val waveIn = alloc<HWAVEINVar>()
		val waveFormat = alloc<WAVEFORMATEX>()
		waveFormat.wFormatTag = WAVE_FORMAT_PCM.toUShort()
		waveFormat.nChannels = 2u
		waveFormat.nSamplesPerSec = 44100u
		waveFormat.nAvgBytesPerSec = (44100 * 2 * 16 / 8).toUInt()
		waveFormat.nBlockAlign = (2 * 16 / 8).toUShort()
		waveFormat.wBitsPerSample = 16u
		waveFormat.cbSize = 0u

		"waveInOpen" to waveInOpen(waveIn.ptr, WAVE_MAPPER, waveFormat.ptr, 0u, 0u, CALLBACK_NULL.toUInt())

		val waveBuffer = alloc<WAVEHDR>()
		val dataSize = 10000000
		val data = allocArray<ShortVar>(dataSize)

		waveBuffer.lpData = data.reinterpret()
		waveBuffer.dwBufferLength = dataSize.toUInt()

		"waveInPrepareHeader" to waveInPrepareHeader(waveIn.value, waveBuffer.ptr, sizeOf<WAVEHDR>().toUInt())
		"waveInAddBuffer" to waveInAddBuffer(waveIn.value, waveBuffer.ptr, sizeOf<WAVEHDR>().toUInt())
		"waveInStart" to waveInStart(waveIn.value)

		println("Запись начата!")

		Sleep(seconds)

		"waveInStop" to waveInStop(waveIn.value)
		"waveInClose" to waveInClose(waveIn.value)

		val outputFile = fopen(path, "wb")

		outputFile?.let {
			writeWavHeader(
				it,
				waveFormat.nChannels,
				waveFormat.nSamplesPerSec,
				waveFormat.wBitsPerSample,
				waveBuffer.dwBytesRecorded
			)
			fwrite(data, 1u, waveBuffer.dwBytesRecorded.toULong(), it)
			fclose(it)
		} ?: run {
			throw RuntimeException("Ошибка: файл недоступен!")
		}

		log.forEach { (key, value) -> println("$key: $value") }

		println("Записано: ${waveBuffer.dwBytesRecorded / 1000u} килобайт")

		if (log.entries.any { it.value != "OK" }) {
			exitProcess(1)
		}
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
