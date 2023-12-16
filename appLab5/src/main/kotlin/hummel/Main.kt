package hummel

fun main() {
	val functions = mapOf(
		"launchTask1" to ::launchTask1,
		"launchTask2" to ::launchTask2,
		"launchTask3" to ::launchTask3,
	)
	while (true) {
		print("Enter the command: ")
		val command = readln()

		if ("exit" == command) {
			break
		}

		functions[command]?.invoke() ?: println("Unknown command!")
	}
}