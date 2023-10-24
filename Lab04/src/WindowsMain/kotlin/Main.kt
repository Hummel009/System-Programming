import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.*
import kotlin.native.internal.collectReferenceFieldValues

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

		val thread = nativeHeap.alloc<pthread_tVar>()
		pthread_create(thread.ptr, null, staticCFunction(::threadFunction), null)

		val hKey = alloc<HKEYVar>()
		val data = "AMOGUS"

		"Create Key" to RegCreateKeyExA(
			HKEY_CURRENT_USER, path, 0u, null, REG_OPTION_VOLATILE.toUInt(), KEY_WRITE.toUInt(), null, hKey.ptr, null
		)
		"Set Value" to RegSetValueExA(hKey.value, name, 0u, REG_SZ.toUInt(), data.ptr(), data.sizeOf())
		val file = fopen("registryBackup.reg", "w")
		fprintf(file, "%s\n", data)
		fclose(file)
		"Close Key" to RegCloseKey(hKey.value)
		"Get Value" to RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, szBuf, dwBufLen)

		println(szBuf.toKString())

		val hKeyRe = alloc<HKEYVar>()
		val replace = "SUS"

		"Open Key Again" to RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, KEY_SET_VALUE.toUInt(), hKeyRe.ptr)
		"Set New Value" to RegSetValueExA(hKeyRe.value, name, 0u, REG_SZ.toUInt(), replace.ptr(), replace.sizeOf())
		"Close Key Again" to RegCloseKey(hKeyRe.value)
		"Get New Value" to RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), dwFlag, szBuf, dwBufLen)

		println(szBuf.toKString())
		println("Flag: ${dwFlag[0]}")

		"Delete Key" to RegDeleteKeyValueA(HKEY_CURRENT_USER, path, name)

		log.forEach { (key, value) -> println("$key: $value") }

		pthread_join(thread.value, null)
		nativeHeap.free(thread)
	}
}

fun threadFunction(arg: COpaquePointer?): CPointer<*>? {
	memScoped {
		val hKey = alloc<HKEYVar>()

		"Open Key For Monitoring" to RegOpenKeyExA(
			HKEY_CURRENT_USER, "Software\\RegistrySample", 0u, KEY_NOTIFY.toUInt(), hKey.ptr
		)

		val hEvent = CreateEventA(null, 1, 0, null)

		RegNotifyChangeKeyValue(hKey.value, 1, REG_NOTIFY_CHANGE_LAST_SET.toUInt(), hEvent, 1)

		if (WaitForSingleObject(hEvent, INFINITE) == WAIT_OBJECT_0) {
			println("CHANGED")
		}
		ResetEvent(hEvent)
	}
	return null
}

private infix fun String.to(signal: Int) {
	log[this] = if (signal == ERROR_SUCCESS) "OK" else signal.toString()
}

private fun String.sizeOf(): DWORD = cstr.size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = cstr.getBytes().toUByteArray().refTo(0)
