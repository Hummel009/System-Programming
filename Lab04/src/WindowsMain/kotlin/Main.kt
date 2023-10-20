import kotlinx.cinterop.*
import platform.windows.*

fun main() {
	memScoped {
		val data = "AMOGUS"
		val name = "Hummel009"
		val path = "Software\\RegistrySample\\"

		val hKey: HKEYVar = alloc()

		if (RegCreateKeyExA(
				HKEY_CURRENT_USER,
				path,
				0u,
				null,
				REG_OPTION_VOLATILE.toUInt(),
				KEY_WRITE.toUInt(),
				null,
				hKey.ptr,
				null
			) != ERROR_SUCCESS
		) {
			println("При создании ключа произошла ошибка")
			return
		}

		if (RegSetValueExA(
				hKey.value, name, 0u, REG_SZ.toUInt(), data.ptr(), data.sizeOf()
			) != ERROR_SUCCESS
		) {
			println("При записи строки произошла ошибка")
			return
		}

		if (RegCloseKey(hKey.value) != ERROR_SUCCESS) {
			println("При закрытии ключа произошла ошибка")
			return
		}

		if (RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, REG_SZ.toUInt(), hKey.ptr) != ERROR_SUCCESS) {
			println("При открытии ключа произошла ошибка")
			return
		}

		val szBuf = allocArray<CHARVar>(MAX_PATH)
		val dwBufLen = allocArray<DWORDVar>(1)
		dwBufLen[0] = MAX_PATH.toUInt()

		if (RegGetValueA(
				HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen
			) != ERROR_SUCCESS
		) {
			println("При чтении строки произошла ошибка")
			return
		}

		println(szBuf.toKString())
	}
}

private fun String.sizeOf(): DWORD = encodeToByteArray().toUByteArray().size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = encodeToByteArray().toUByteArray().refTo(0)
