package hummel

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

fun readWavFile(filePath: String): DoubleArray? {
	val file = File(filePath)
	val fileSize = file.length().toInt()

	val wavData = ByteArray(fileSize)
	val inputStream = FileInputStream(file)
	inputStream.read(wavData)

	val chunkId = String(wavData, 0, 4)
	if (chunkId != "RIFF") {
		println("Неверный формат файла WAV")
		return null
	}

	val format = String(wavData, 8, 4)
	if (format != "WAVE") {
		println("Неверный формат файла WAV")
		return null
	}

	val subchunk1Id = String(wavData, 12, 4)
	if (subchunk1Id != "fmt ") {
		println("Неверный формат файла WAV")
		return null
	}

	val audioFormat = byteArrayToShort(wavData, 20)
	if (audioFormat != 1.toShort()) {
		println("Неподдерживаемый формат аудио")
		return null
	}

	val numChannels = byteArrayToShort(wavData, 22).toInt()
	val sampleRate = byteArrayToInt(wavData, 24)
	val bitsPerSample = byteArrayToShort(wavData, 34).toInt()

	val subchunk2Id = String(wavData, 36, 4)
	if (subchunk2Id != "data") {
		println("Неверный формат файла WAV")
		return null
	}

	// Чтение семплов звука
	val dataSize = byteArrayToInt(wavData, 40)
	val numSamples = dataSize / (bitsPerSample / 8)
	val samples = DoubleArray(numSamples)

	var byteIndex = 44 // Начальный индекс для данных семплов

	for ((sampleIndex, _) in (0 until numSamples).withIndex()) {
		val sampleBytes = ByteArray(bitsPerSample / 8)
		System.arraycopy(wavData, byteIndex, sampleBytes, 0, bitsPerSample / 8)

		val sampleValue = when (bitsPerSample) {
			8 -> sampleBytes[0].toDouble()
			16 -> byteArrayToShort(sampleBytes, 0).toDouble()
			32 -> byteArrayToInt(sampleBytes, 0).toDouble()
			else -> {
				println("Неподдерживаемая глубина бита: $bitsPerSample")
				return null
			}
		}

		samples[sampleIndex] = sampleValue
		byteIndex += bitsPerSample / 8
	}

	return samples
}

fun byteArrayToShort(byteArray: ByteArray, offset: Int): Short {
	val buffer = ByteBuffer.wrap(byteArray, offset, 2)
	buffer.order(ByteOrder.LITTLE_ENDIAN)
	return buffer.short
}

fun byteArrayToInt(byteArray: ByteArray, offset: Int): Int {
	val buffer = ByteBuffer.wrap(byteArray, offset, 4)
	buffer.order(ByteOrder.LITTLE_ENDIAN)
	return buffer.int
}

fun fft(N: Int, REX: DoubleArray, IMX: DoubleArray) {
	//Set constants
	val PI = 3.14159265
	val NM1 = N - 1
	val ND2 = N / 2
	val M = (log2(N.toDouble())).toInt()
	var J = ND2
	var K: Int

	//Bit reversal sorting
	class GoTo1190 : Exception()
	nextI@ for (I in 1 until N - 1) { //1110
		try {
			if (I >= J) throw GoTo1190() //1120
			val TR = REX[J] //1130
			val TI = IMX[J] //1140
			REX[J] = REX[I] //1150
			IMX[J] = IMX[I] //1160
			REX[I] = TR //1170
			IMX[I] = TI //1180
			throw GoTo1190() //1190
		} catch (e: GoTo1190) {
			K = ND2 //1190
			while (K <= J) {
				J -= K //1210
				K /= 2 //1220
			}
			J += K //1240
			continue@nextI //1250
		}
	}

	for (L in 1..M) {
		val LE = (2.0.pow(L.toDouble())).toInt()
		val LE2 = LE / 2
		var UR = 1.0
		var UI = 0.0
		val SR = cos(PI / LE2)
		val SI = -sin(PI / LE2)

		for (J in 1..LE2) {
			val JM1 = J - 1
			for (I in JM1 until NM1 step LE) {
				val IP = I + LE2
				val TR = REX[IP] * UR - IMX[IP] * UI
				val TI = REX[IP] * UI + IMX[IP] * UR
				REX[IP] = REX[I] - TR
				IMX[IP] = IMX[I] - TI
				REX[I] = REX[I] + TR
				IMX[I] = IMX[I] + TI
			}
			val TR = UR
			UR = TR * SR - UI * SI
			UI = TR * SI + UI * SR
		}
	}
}

fun main() {
	val filePath = "${System.getProperty("user.home")}\\Downloads\\test.wav"
	val samples = readWavFile(filePath)

	samples?.let {
		val sampleCount = it.size
		val fftSize = 2.0.pow(ceil(log2(sampleCount.toDouble()))).toInt()

		// Создание массивов для разложения FFT
		val REX = it.copyOf(fftSize)
		val IMX = DoubleArray(fftSize)

		// Выполнение разложения
		fft(fftSize, REX, IMX)

		// Вывод результатов
		for (i in 0 until fftSize) {
			println("REX[$i] = ${REX[i]}, IMX[$i] = ${IMX[i]}")
		}
	}
}