package com.github.hummel.sp.course.app

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

private const val FORMAT_ERR: String = "Ошибка: неверный формат файла WAV!"

class FourierTransform(private var wavFile: File) {
	fun execute() {
		val signal = getSamplesFromFile().map { it.toFloat() }.toFloatArray()

		val originalSize = signal.size
		val nearestPowerOfTwo = 1 shl (32 - Integer.numberOfLeadingZeros(originalSize - 1))
		val paddedSignal = FloatArray(nearestPowerOfTwo)
		signal.copyInto(paddedSignal)

		val n = paddedSignal.size
		val real = FloatArray(n) { paddedSignal[it] }
		val imag = FloatArray(n) { 0.0f }

		fft(real, imag)

		// Вывод результатов
		val result = buildString {
			for (i in 0 until n) {
				append("REX[")
				append(i)
				append("] = ")
				append(real[i])
				append(", IMX[")
				append(i)
				append("] = ")
				append(imag[i])
				append("\r\n")
			}
		}
		File("${System.getProperty("user.home")}\\Downloads\\fft.txt").writeText(result)
	}

	private fun getSamplesFromFile(): DoubleArray {
		val fileSize = wavFile.length().toInt()

		val wavData = ByteArray(fileSize)
		val inputStream = FileInputStream(wavFile)
		inputStream.read(wavData)

		val chunkId = String(wavData, 0, 4)
		if (chunkId != "RIFF") {
			throw Exception(FORMAT_ERR)
		}

		val format = String(wavData, 8, 4)
		if (format != "WAVE") {
			throw Exception(FORMAT_ERR)
		}

		val subchunk1Id = String(wavData, 12, 4)
		if (subchunk1Id != "fmt ") {
			throw Exception(FORMAT_ERR)
		}

		val audioFormat = byteArrayToShort(wavData, 20)
		if (audioFormat != 1.toShort()) {
			throw Exception(FORMAT_ERR)
		}

		val bitsPerSample = byteArrayToShort(wavData, 34).toInt()

		val subchunk2Id = String(wavData, 36, 4)
		if (subchunk2Id != "data") {
			throw Exception(FORMAT_ERR)
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
				else -> throw Exception(FORMAT_ERR)
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

	private fun fft(x: FloatArray, y: FloatArray) {
		var j: Int
		var k: Int
		var n1: Int
		var n2: Int
		var a: Int
		var c: Float
		var s: Float
		var t1: Float
		var t2: Float
		var n = x.size
		var m = (ln(n.toFloat()) / ln(2.0)).toInt()

		j = 0
		n2 = n / 2
		var i = 1
		while (i < n - 1) {
			n1 = n2
			while (j >= n1) {
				j = j - n1
				n1 = n1 / 2
			}
			j = j + n1

			if (i < j) {
				t1 = x[i]
				x[i] = x[j]
				x[j] = t1
				t1 = y[i]
				y[i] = y[j]
				y[j] = t1
			}
			i++
		}

		n1 = 0
		n2 = 1

		i = 0
		while (i < m) {
			n1 = n2
			n2 = n2 + n2
			a = 0

			j = 0
			while (j < n1) {
				c = cos(a.toFloat())
				s = sin(a.toFloat())
				a += 1 shl m - i - 1

				k = j
				while (k < n) {
					t1 = c * x[k + n1] - s * y[k + n1]
					t2 = s * x[k + n1] + c * y[k + n1]
					x[k + n1] = x[k] - t1
					y[k + n1] = y[k] - t2
					x[k] = x[k] + t1
					y[k] = y[k] + t2
					k = k + n2
				}
				++j
			}
			++i
		}
	}
}