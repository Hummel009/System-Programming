package hummel

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.Kernel32

internal interface ExKernel32 : Kernel32 {
	companion object {
		val INSTANCE: ExKernel32 = Native.load("kernel32", ExKernel32::class.java)
	}

	fun GetSystemInfo(lpSystemInfo: SYSTEM_INFO?): Int

	open class SYSTEM_INFO : Structure() {
		@JvmField
		var wProcessorArchitecture: Short = 0

		@JvmField
		var wReserved: Short = 0

		@JvmField
		var dwPageSize = 0

		@JvmField
		var lpMinimumApplicationAddress: Pointer? = null

		@JvmField
		var lpMaximumApplicationAddress: Pointer? = null

		@JvmField
		var dwActiveProcessorMask: Long = 0

		@JvmField
		var dwNumberOfProcessors = 0

		@JvmField
		var dwProcessorType = 0

		@JvmField
		var dwAllocationGranularity = 0

		@JvmField
		var wProcessorLevel: Short = 0

		@JvmField
		var wProcessorRevision: Short = 0

		override fun getFieldOrder(): List<String> {
			return mutableListOf(
				"wProcessorArchitecture",
				"wReserved",
				"dwPageSize",
				"lpMinimumApplicationAddress",
				"lpMaximumApplicationAddress",
				"dwActiveProcessorMask",
				"dwNumberOfProcessors",
				"dwProcessorType",
				"dwAllocationGranularity",
				"wProcessorLevel",
				"wProcessorRevision"
			)
		}
	}
}