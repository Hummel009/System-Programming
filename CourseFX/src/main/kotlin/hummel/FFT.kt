package hummel

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*
import kotlin.time.measureTime

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

fun fft(n: Int, rex: DoubleArray, imx: DoubleArray) {
	//Set constants
	val pi = Math.PI //1050
	val nm1 = n - 1 //1060
	val nd2 = n / 2 //1070
	val m = log2(n.toDouble()).toInt() //1080
	var j = nd2 //1090
	var k: Int

	//Bit reversal sorting
	class GoTo1190 : Exception()
	nextI@ for (i in 1 until n - 1) { //1110
		try {
			if (i >= j) throw GoTo1190() //1120
			val tr = rex[j] //1130
			val ti = imx[j] //1140
			rex[j] = rex[i] //1150
			imx[j] = imx[i] //1160
			rex[i] = tr //1170
			imx[i] = ti //1180
			throw GoTo1190()
		} catch (e: GoTo1190) {
			k = nd2 //1190
			while (k <= j) {
				j -= k //1210
				k /= 2 //1220
			}
			j += k //1240
			continue@nextI //1250
		}
	}

	//Loop for each stage
	for (l in 1..m) { //1270
		val le = (2.0.pow(l)).toInt() //1280
		val le2 = le / 2 //1290
		var ur = 1.0 //1300
		var ui = 0.0 //1310

		//Calculate sine & cosine values
		val sr = cos(pi / le2) //1320
		val si = -sin(pi / le2) //1330

		for (jShad in 1..le2) { //1340
			val jm1 = jShad - 1 //1350

			//Loop for each butterfly
			for (i in jm1..nm1 step le) { //1360
				val ip = i + le2 //1370

				//Butterfly calculation
				val tr = rex[ip] * ur - imx[ip] * ui //1380
				val ti = rex[ip] * ui + imx[ip] * ur //1390
				rex[ip] = rex[i] - tr //1400
				imx[ip] = imx[i] - ti //1410
				rex[i] = rex[i] + tr //1420
				imx[i] = imx[i] + ti //1430
			}
			val tr = ur //1450
			ur = tr * sr - ui * si //1460
			ui = tr * si + ui * sr //1470
		}
	}
}

fun main() {
	val filePath = "${System.getProperty("user.home")}\\Downloads\\test.wav"
	val samples = readWavFile(filePath)

	samples?.let {
		val sampleCount = it.size
		val fftSize = 2.0.pow(ceil(log2(sampleCount.toDouble()))).toInt()

		val rex = it.copyOf(fftSize)
		val imx = DoubleArray(fftSize)

		// Выполнение разложения
		fft(fftSize, rex, imx)

		// Вывод результатов
		val time = measureTime {
			val result = buildString {
				for (i in 0 until fftSize) {
					append("REX[").append(i).append("] = ").append(rex[i]).append(", IMX[").append(i).append("] = ")
						.append(imx[i]).append("\r\n")
				}
			}
			println(result)
		}
		println(time.inWholeSeconds)
	}
}