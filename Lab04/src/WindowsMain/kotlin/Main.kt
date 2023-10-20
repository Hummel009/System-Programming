import kotlinx.cinterop.*
import platform.windows.*

fun main() {
	memScoped {
		val szTestString = "This is the test"
		val szPath = "Software\\RegistrySample\\"

		val hKey: HKEYVar = alloc()

		// Создаем ключ в ветке HKEY_CURRENT_USER
		if (RegCreateKeyExW(
				HKEY_CURRENT_USER,
				szPath,
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

		// Пишем тестовую строку в созданный ключ
		if (RegSetValueExW(
				hKey.value,
				"Test string",
				0u,
				REG_SZ.toUInt(),
				szTestString.ptr(),
				szTestString.sizeOf()
			) != ERROR_SUCCESS
		) {
			println("При записи строки произошла ошибка")
			return
		}

		if (RegCloseKey(hKey.value) != ERROR_SUCCESS) {
			println("При закрытии ключа произошла ошибка")
			return
		}

		val szBuf = allocArray<CHARVar>(MAX_PATH)
		val dwBufLen = allocArray<DWORDVar>(1)
		dwBufLen[0] = MAX_PATH.toUInt()

		if (RegGetValueW(
				HKEY_CURRENT_USER,
				szPath,
				"Test String",
				RRF_RT_REG_SZ.toUInt(),
				null,
				szBuf,
				dwBufLen
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
