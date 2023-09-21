package hummel

fun main() {
	val systemInfo = ExKernel32.SYSTEM_INFO()
	ExKernel32.INSTANCE.GetSystemInfo(systemInfo)
	println("Processor Architecture: " + systemInfo.wProcessorArchitecture)
	println("Number of Processors: " + systemInfo.dwNumberOfProcessors)
	println("Processor Type: " + systemInfo.dwProcessorType)
	println("Processor Level: " + systemInfo.wProcessorLevel)
	println("Processor Revision: " + systemInfo.wProcessorRevision)
}