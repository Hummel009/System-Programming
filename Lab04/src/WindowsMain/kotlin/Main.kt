import kotlinx.cinterop.*
import platform.windows.*

fun main() {
	memScoped {
		val name = "Hummel009"
		val path = "Software\\RegistrySample\\"
		val szBuf = allocArray<CHARVar>(MAX_PATH)
		val dwBufLen = allocArray<DWORDVar>(1)
		dwBufLen[0] = MAX_PATH.toUInt()
		val dwFlag = allocArray<DWORDVar>(1)
		dwFlag[0] = 0u

		val log = mutableMapOf<String, Int>()

		val hKey: HKEYVar = alloc()
		val data = "AMOGUS"
		log["Create Key"] = RegCreateKeyExA(
			HKEY_CURRENT_USER, path, 0u, null, REG_OPTION_VOLATILE.toUInt(), KEY_WRITE.toUInt(), null, hKey.ptr, null
		)
		log["Set Value"] = RegSetValueExA(hKey.value, name, 0u, REG_SZ.toUInt(), data.ptr(), data.sizeOf())
		log["Close Key"] = RegCloseKey(hKey.value)
		log["Get Value"] = RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen)

		println(szBuf.toKString())

		val hKeyRe: HKEYVar = alloc()
		val replace = "SUS"
		log["Open Key Again"] = RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, KEY_SET_VALUE.toUInt(), hKeyRe.ptr)
		log["Set New Value"] = RegSetValueExA(hKeyRe.value, name, 0u, REG_SZ.toUInt(), replace.ptr(), replace.sizeOf())
		log["Close Key Again"] = RegCloseKey(hKeyRe.value)
		log["Get New Value"] = RegGetValueA(
			HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), dwFlag, szBuf, dwBufLen
		)

		println(szBuf.toKString())
		println("Flag: ${dwFlag[0]}")

		log["Delete Key"] = RegDeleteKeyValueA(HKEY_CURRENT_USER, path, name)

		log.forEach { (key, value) -> println("$key: ${if (value == ERROR_SUCCESS) "OK" else "NOT OK"}") }
	}
}

private fun String.sizeOf(): DWORD = encodeToByteArray().toUByteArray().size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = encodeToByteArray().toUByteArray().refTo(0)
