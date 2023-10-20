import kotlinx.cinterop.*
import platform.windows.*

val log: MutableMap<String, String> = mutableMapOf()

fun main() {
	memScoped {
		val name = "Hummel009"
		val path = "Software\\RegistrySample\\"
		val szBuf = allocArray<CHARVar>(MAX_PATH)
		val dwBufLen = allocArray<DWORDVar>(1)
		dwBufLen[0] = MAX_PATH.toUInt()
		val dwFlag = allocArray<DWORDVar>(1)
		dwFlag[0] = 0u

		val hKey: HKEYVar = alloc()
		val data = "AMOGUS"

		"Create Key" to RegCreateKeyExA(
			HKEY_CURRENT_USER, path, 0u, null, REG_OPTION_VOLATILE.toUInt(), KEY_WRITE.toUInt(), null, hKey.ptr, null
		)
		"Set Value" to RegSetValueExA(hKey.value, name, 0u, REG_SZ.toUInt(), data.ptr(), data.sizeOf())
		"Close Key" to RegCloseKey(hKey.value)
		"Get Value" to RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen)

		println(szBuf.toKString())

		val hKeyRe: HKEYVar = alloc()
		val replace = "SUS"

		"Open Key Again" to RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, KEY_SET_VALUE.toUInt(), hKeyRe.ptr)
		"Set New Value" to RegSetValueExA(hKeyRe.value, name, 0u, REG_SZ.toUInt(), replace.ptr(), replace.sizeOf())
		"Close Key Again" to RegCloseKey(hKeyRe.value)
		"Get New Value" to RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), dwFlag, szBuf, dwBufLen)

		println(szBuf.toKString())
		println("Flag: ${dwFlag[0]}")

		"Delete Key" to RegDeleteKeyValueA(HKEY_CURRENT_USER, path, name)

		log.forEach { (key, value) -> println("$key: $value") }
	}
}

private infix fun String.to(signal: Int) {
	log[this] = if (signal == ERROR_SUCCESS) "OK" else "NOT OK"
}

private fun String.sizeOf(): DWORD = encodeToByteArray().toUByteArray().size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = encodeToByteArray().toUByteArray().refTo(0)
