package hummel

import java.nio.charset.StandardCharsets
import java.util.*

fun main() {
	Scanner(System.`in`, StandardCharsets.UTF_8).use {
		loop@ while (true) {
			val command = it.nextLine()

			if (command.contains("1")) {
				launchTask1()
			} else if (command.contains("2")) {
				launchTask2()
			} else if (command.contains("3")) {
				launchTask3()
			} else if (command.contains("exit")) {
				break@loop
			}
		}
	}
}