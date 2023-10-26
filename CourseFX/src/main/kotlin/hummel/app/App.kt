package hummel.app

import javafx.application.Application
import javafx.stage.Stage

class App : Application() {
	private var windowSize: WindowSize? = null
	private var player: Player? = null

	override fun init() {
		windowSize = WindowSize()
		player = Player(windowSize ?: return)
	}

	override fun start(stage: Stage) {
		(windowSize ?: return).bind(stage.widthProperty(), stage.heightProperty())
		(player ?: return).stage = stage
		stage.title = "Hummel009's Media Player"
		stage.setScene((player ?: return).scene)
		stage.show()
	}
}