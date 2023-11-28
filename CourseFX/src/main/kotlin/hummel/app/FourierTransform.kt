package hummel.app

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*
import kotlin.time.measureTime

class FourierTransform(private var wavFile: File) {
	fun execute() {
		val samples = getSamplesFromFile()

		val sampleCount = samples.size
		val fftSize = 2.0.pow(ceil(log2(sampleCount.toDouble()))).toInt()

		val rex = samples.copyOf(fftSize)
		val imx = DoubleArray(fftSize)

		// Выполнение разложения
		basicFourierTransform(fftSize, rex, imx)

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

	private fun getSamplesFromFile(): DoubleArray {
		val fileSize = wavFile.length().toInt()

		val wavData = ByteArray(fileSize)
		val inputStream = FileInputStream(wavFile)
		inputStream.read(wavData)

		val chunkId = String(wavData, 0, 4)
		if (chunkId != "RIFF") {
			throw Exception("Ошибка: неверный формат файла WAV!")
		}

		val format = String(wavData, 8, 4)
		if (format != "WAVE") {
			throw Exception("Ошибка: неверный формат файла WAV!")
		}

		val subchunk1Id = String(wavData, 12, 4)
		if (subchunk1Id != "fmt ") {
			throw Exception("Ошибка: неверный формат файла WAV!")
		}

		val audioFormat = byteArrayToShort(wavData, 20)
		if (audioFormat != 1.toShort()) {
			throw Exception("Ошибка: неверный формат файла WAV!")
		}

		val bitsPerSample = byteArrayToShort(wavData, 34).toInt()

		val subchunk2Id = String(wavData, 36, 4)
		if (subchunk2Id != "data") {
			throw Exception("Ошибка: неверный формат файла WAV!")
		}

		// Чтение семплов звука
		val dataSize = byteArrayToInt(wavData, 40)
		val numSamples = dataSize / (bitsPerSample / 8)
		val samples = DoubleArray(numSamples)

		var byteIndex = 44

		for ((sampleIndex, _) in (0 until numSamples).withIndex()) {
			val sampleBytes = ByteArray(bitsPerSample / 8)
			System.arraycopy(wavData, byteIndex, sampleBytes, 0, bitsPerSample / 8)

			val sampleValue = when (bitsPerSample) {
				8 -> sampleBytes[0].toDouble()
				16 -> byteArrayToShort(sampleBytes, 0).toDouble()
				32 -> byteArrayToInt(sampleBytes, 0).toDouble()
				else -> throw Exception("Ошибка: неверный формат файла WAV!")
			}

			samples[sampleIndex] = sampleValue
			byteIndex += bitsPerSample / 8
		}

		return samples
	}

	private fun byteArrayToShort(byteArray: ByteArray, offset: Int): Short {
		val buffer = ByteBuffer.wrap(byteArray, offset, 2)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		return buffer.short
	}

	private fun byteArrayToInt(byteArray: ByteArray, offset: Int): Int {
		val buffer = ByteBuffer.wrap(byteArray, offset, 4)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		return buffer.int
	}

	@Suppress("NAME_SHADOWING")
	private fun basicFourierTransform(n: Int, rex: DoubleArray, imx: DoubleArray) {
		var k: Int
		var tr: Double
		var ti: Double
		var le: Int
		var le2: Int
		var ur: Double
		var ui: Double
		var sr: Double
		var si: Double
		var jm1: Int
		var ip: Int

		//Set constants
		val pi = Math.PI //1050
		val nm1 = n - 1 //1060
		val nd2 = n / 2 //1070
		val m = log2(n.toDouble()).toInt() //1080
		var j = nd2 //1090

		//Bit reversal sorting
		class GoTo1190 : Exception()
		nextI@ for (i in 1 until n - 1) { //1110
			try {
				if (i >= j) throw GoTo1190() //1120
				tr = rex[j] //1130
				ti = imx[j] //1140
				rex[j] = rex[i] //1150
				imx[j] = imx[i] //1160
				rex[i] = tr //1170
				imx[i] = ti //1180
				throw GoTo1190()
			} catch (e: GoTo1190) {
				k = nd2 //1190
				while (k <= j) { //1220
					j -= k //1210
					k /= 2 //1220
				}
				j += k //1240
				continue@nextI //1250
			}
		}

		//Loop for each stage
		for (l in 1..m) { //1270
			le = (2.0.pow(l)).toInt() //1280
			le2 = le / 2 //1290
			ur = 1.0 //1300
			ui = 0.0 //1310

			//Calculate sine & cosine values
			sr = cos(pi / le2) //1320
			si = -sin(pi / le2) //1330

			//Loop for each sub DFT
			for (j in 1..le2) { //1340
				jm1 = j - 1 //1350

				//Loop for each butterfly
				for (i in jm1..nm1 step le) { //1360
					ip = i + le2 //1370

					//Butterfly calculation
					tr = rex[ip] * ur - imx[ip] * ui //1380
					ti = rex[ip] * ui + imx[ip] * ur //1390
					rex[ip] = rex[i] - tr //1400
					imx[ip] = imx[i] - ti //1410
					rex[i] = rex[i] + tr //1420
					imx[i] = imx[i] + ti //1430
				}
				tr = ur //1450
				ur = tr * sr - ui * si //1460
				ui = tr * si + ui * sr //1470
			}
		}
	}
}