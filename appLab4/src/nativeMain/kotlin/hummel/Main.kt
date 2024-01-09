package hummel

import kotlinx.cinterop.*
import platform.windows.*

val log: MutableMap<String, String> = mutableMapOf()

const val name: String = "Hummel009"
const val path: String = "Software\\RegistrySample\\"
var changed: Boolean = false

fun main() {
	val threadOperate = CreateThread(
		null, 0u, staticCFunction(::threadOperate), null, 0u, null
	)

	val threadNotify = CreateThread(
		null, 0u, staticCFunction(::threadNotify), null, 0u, null
	)

	WaitForSingleObject(threadOperate, INFINITE)
	WaitForSingleObject(threadNotify, INFINITE)

	CloseHandle(threadOperate)
	CloseHandle(threadNotify)

	println("Call results:\n")
	log.forEach { (key, value) -> println("$key: $value") }

	println()

	if (changed) {
		println("The key was changed!")
	} else {
		println("The key wasn't changed!")
	}
}

@Suppress("UNUSED_PARAMETER")
fun threadNotify(lpParameter: LPVOID?): DWORD {
	memScoped {
		val key = alloc<HKEYVar>()

		"Open Key For Monitoring" to RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, KEY_NOTIFY.toUInt(), key.ptr)

		val event = CreateEventA(null, 1, 0, null)

		RegNotifyChangeKeyValue(key.value, 1, REG_NOTIFY_CHANGE_LAST_SET.toUInt(), event, 1)

		if (WaitForSingleObject(event, INFINITE) == WAIT_OBJECT_0) {
			changed = true
		}
		ResetEvent(event)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun threadOperate(lpParameter: LPVOID?): DWORD {
	memScoped {
		Sleep(1000u)

		val buffer = allocArray<CHARVar>(MAX_PATH)

		val bufferLength = allocArray<DWORDVar>(1)
		bufferLength[0] = MAX_PATH.toUInt()

		val key = alloc<HKEYVar>()
		val keyValue = "AMOGUS"

		"Create Key" to RegCreateKeyExA(
			HKEY_CURRENT_USER, path, 0u, null, REG_OPTION_VOLATILE.toUInt(), KEY_WRITE.toUInt(), null, key.ptr, null
		)
		"Set Value" to RegSetValueExA(key.value, name, 0u, REG_SZ.toUInt(), keyValue.ptr(), keyValue.sizeOf())
		"Close Key" to RegCloseKey(key.value)
		"Get Value" to RegGetValueA(HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, buffer, bufferLength)

		println("Initial value: ${buffer.toKString()}")

		val newKey = alloc<HKEYVar>()
		val newKeyValue = "SUS"

		"Open Key Again" to RegOpenKeyExA(HKEY_CURRENT_USER, path, 0u, KEY_SET_VALUE.toUInt(), newKey.ptr)
		"Set New Value" to RegSetValueExA(
			newKey.value, name, 0u, REG_SZ.toUInt(), newKeyValue.ptr(), newKeyValue.sizeOf()
		)
		"Close Key Again" to RegCloseKey(newKey.value)
		"Get New Value" to RegGetValueA(
			HKEY_CURRENT_USER, path, name, RRF_RT_REG_SZ.toUInt(), null, buffer, bufferLength
		)

		println("New value: ${buffer.toKString()}")

		"Delete Key" to RegDeleteKeyValueA(HKEY_CURRENT_USER, path, name)

		println()
	}
	return 0u
}

private infix fun String.to(signal: Int) {
	log[this] = if (signal == ERROR_SUCCESS) "OK" else "$signal"
}

private fun String.sizeOf(): DWORD = cstr.size.toUInt()

private fun String.ptr(): CValuesRef<UByteVarOf<UByte>> = cstr.getBytes().toUByteArray().refTo(0)
