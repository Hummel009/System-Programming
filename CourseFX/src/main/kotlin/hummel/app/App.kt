package hummel.app

import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

class App : Application() {
	private lateinit var windowSize: WindowSize
	private lateinit var player: Player

	override fun init() {
		windowSize = WindowSize()
		player = Player(windowSize)
	}

	override fun start(stage: Stage) {
		windowSize.bind(stage.widthProperty(), stage.heightProperty())
		stage.title = "Hummel009's Media Player"
		stage.setScene(player.scene)
		stage.show()
		stage.setOnCloseRequest {
			it.consume()
			stage.hide()
			exitProcess(0)
		}
	}
}