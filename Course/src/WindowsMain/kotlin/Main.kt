import kotlinx.cinterop.*
import platform.windows.*

var NUM_BUFFERS: Int = 3

fun main() {
	memScoped {
		val hWaveIn = alloc<HWAVEINVar>()
		val waveHeader = allocArray<WAVEHDR>(NUM_BUFFERS)

		val wfx = alloc<WAVEFORMATEX>()

		if (waveInOpen(
				hWaveIn.ptr, WAVE_MAPPER, wfx.ptr, 0u, 0u, CALLBACK_FUNCTION.toUInt()
			).toInt() != MMSYSERR_NOERROR
		) {
			return
		}

		for (i in 0 until NUM_BUFFERS) {
			if (waveInPrepareHeader(
					hWaveIn.value, waveHeader[i].ptr, sizeOf<WAVEHDR>().toUInt()
				).toInt() != MMSYSERR_NOERROR
			) {
				return
			}

			if (waveInAddBuffer(
					hWaveIn.value, waveHeader[i].ptr, sizeOf<WAVEHDR>().toUInt()
				).toInt() != MMSYSERR_NOERROR
			) {
				return
			}
		}

		if (waveInStart(hWaveIn.value).toInt() != MMSYSERR_NOERROR) {
			return
		}

		if (waveInStop(hWaveIn.value).toInt() != MMSYSERR_NOERROR) {
			return
		}

		if (waveInClose(hWaveIn.value).toInt() != MMSYSERR_NOERROR) {
			return
		}
	}
}