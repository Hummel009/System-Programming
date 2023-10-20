import kotlinx.cinterop.*
import platform.windows.*

fun main() {
	memScoped {
		val data = "AMOGUS"
		val replace = "SUS"
		val name = "Hummel009"
		val path = "Software\\RegistrySample\\"
		val szBuf = allocArray<CHARVar>(MAX_PATH)
		val dwBufLen = allocArray<DWORDVar>(1)
		dwBufLen[0] = MAX_PATH.toUInt()

		val map = mutableMapOf<String, Int>()

		val hKey: HKEYVar = alloc()
		map["createKey1"] = RegCreateKeyExA(
			HKEY_CURRENT_USER, path, 0u, null, REG_OPTION_VOLATILE.toUInt(), KEY_WRITE.toUInt(), null, hKey.ptr, null
		)
		map["setValue1"] = RegSetValueExA(hKey.value, name, 0u, REG_SZ.toUInt(), data.ptr(), data.sizeOf())
		map["closeKey1"] = RegCloseKey(hKey.value)
		map["getValue1"] = RegGetValueA(
			HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen
		)

		println(szBuf.toKString())

		val hKeyRe: HKEYVar = alloc()
		map["openKey2"] = RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, REG_SZ.toUInt(), hKeyRe.ptr)
		map["setValue2"] = RegSetValueExA(
			hKeyRe.value, name, 0u, REG_SZ.toUInt(), replace.ptr(), replace.sizeOf()
		)
		map["closeKey2"] = RegCloseKey(hKeyRe.value)
		map["getValue2"] = RegGetValueA(
			HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen
		)

		println(szBuf.toKString())

		map.forEach { (key, value) -> println("$key, ${if (value == ERROR_SUCCESS) "OK" else "NOT OK"}") }
	}
}

private fun String.sizeOf(): DWORD = encodeToByteArray().toUByteArray().size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = encodeToByteArray().toUByteArray().refTo(0)
