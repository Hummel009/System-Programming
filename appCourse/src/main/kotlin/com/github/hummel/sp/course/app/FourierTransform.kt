package com.github.hummel.sp.course.app

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

private const val FORMAT_ERR: String = "Ошибка: неверный формат файла WAV!"

class FourierTransform(private var wavFile: File) {
	fun execute() {
		val samples = getSamplesFromFile()

		val sampleCount = samples.size

		val fftSize = 2.0.pow(ceil(log2(sampleCount.toDouble()))).toInt()
		val rex = samples.copyOf(fftSize)
		val imx = DoubleArray(fftSize)

		//val fftSize = 8
		//val rex = doubleArrayOf(-2.1, 1.1, -1.1, 5.1, 0.1, 3.1, 0.1, -4.1)
		//val imx = DoubleArray(fftSize)

		//FastFouriers.BEST.transform(rex, imx)

		// Выполнение разложения
		basicFourierTransform(fftSize, rex, imx)

		// Вывод результатов
		val result = buildString {
			for (i in 0 until fftSize) {
				append("REX[")
				append(i)
				append("] = ")
				append(rex[i])
				append(", IMX[")
				append(i)
				append("] = ")
				append(imx[i])
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

	/**
	 * 1000 'THE FAST FOURIER TRANSFORM
	 * 1010 'Upon entry, N% contains the number of points in the DFT, REX[ ] and
	 * 1020 'IMX[ ] contain the real and imaginary parts of the input. Upon return,
	 * 1030 'REX[ ] and IMX[ ] contain the DFT output. All signals run from 0 to N%-1.
	 * 1040 '
	 * 1050 PI = 3.14159265 'Set constants
	 * 1060 NM1% = N%-1
	 * 1070 ND2% = N%/2
	 * 1080 M% = CINT(LOG(N%)/LOG(2))
	 * 1090 J% = ND2%
	 * 1100 '
	 * 1110 FOR I% = 1 TO N%-2 'Bit reversal sorting
	 * 1120 IF I% >= J% THEN GOTO 1190
	 * 1130 TR = REX[J%]
	 * 1140 TI = IMX[J%]
	 * 1150 REX[J%] = REX[I%]
	 * 1160 IMX[J%] = IMX[I%]
	 * 1170 REX[I%] = TR
	 * 1180 IMX[I%] = TI
	 * 1190 K% = ND2%
	 * 1200 IF K% > J% THEN GOTO 1240
	 * 1210 J% = J%-K%
	 * 1220 K% = K%/2
	 * 1230 GOTO 1200
	 * 1240 J% = J%+K%
	 * 1250 NEXT I%
	 * 1260 '
	 * 1270 FOR L% = 1 TO M% 'Loop for each stage
	 * 1280 LE% = CINT(2^L%)
	 * 1290 LE2% = LE%/2
	 * 1300 UR = 1
	 * 1310 UI = 0
	 * 1320 SR = COS(PI/LE2%) 'Calculate sine & cosine values
	 * 1330 SI = -SIN(PI/LE2%)
	 * 1340 FOR J% = 1 TO LE2% 'Loop for each sub DFT
	 * 1350 JM1% = J%-1
	 * 1360 FOR I% = JM1% TO NM1% STEP LE% 'Loop for each butterfly
	 * 1370 IP% = I%+LE2%
	 * 1380 TR = REX[IP%]*UR - IMX[IP%]*UI 'Butterfly calculation
	 * 1390 TI = REX[IP%]*UI + IMX[IP%]*UR
	 * 1400 REX[IP%] = REX[I%]-TR
	 * 1410 IMX[IP%] = IMX[I%]-TI
	 * 1420 REX[I%] = REX[I%]+TR
	 * 1430 IMX[I%] = IMX[I%]+TI
	 * 1440 NEXT I%
	 * 1450 TR = UR
	 * 1460 UR = TR*SR - UI*SI
	 * 1470 UI = TR*SI + UI*SR
	 * 1480 NEXT J%
	 * 1490 NEXT L%
	 * 1500 '
	 * 1510 RETURN
	 */
}