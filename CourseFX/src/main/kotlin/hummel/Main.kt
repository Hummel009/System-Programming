package hummel

import hummel.app.App
import javafx.application.Application
import java.io.File

var file: File = File("tape.wav")

fun main() {
	Application.launch(App::class.java)
}