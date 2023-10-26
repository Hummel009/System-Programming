package hummel.app

import hummel.engine.Visualizer
import hummel.file
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.media.AudioSpectrumListener
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.net.MalformedURLException

class Player(private var windowSize: WindowSize) {
	private var borderPane: BorderPane = BorderPane()
	private var stackPane: StackPane = StackPane(borderPane)
	private var player: Player? = null
	private var visualizer: Visualizer? = null
	private var mediaPlayer: MediaPlayer? = null
	var stage: Stage? = null
	var scene: Scene

	init {
		stackPane.alignment = Pos.CENTER
		stackPane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		scene = Scene(stackPane, 1280.0, 720.0)
		scene.onMousePressed = EventHandler<Event> {
			try {
				val media = Media(file.toURI().toURL().toString())
				mediaPlayer = MediaPlayer(media)
				(mediaPlayer ?: return@EventHandler).onEndOfMedia = Runnable {
					player = null
					(visualizer ?: return@Runnable).clear()
					visualizer = null
				}
				if (player == null) {
					player = Player(windowSize)
					visualizer = Visualizer(windowSize)
					(mediaPlayer ?: return@EventHandler).audioSpectrumNumBands = 1024
					(mediaPlayer ?: return@EventHandler).audioSpectrumListener =
						AudioSpectrumListener { _: Double, _: Double, floats: FloatArray, _: FloatArray ->
							(visualizer ?: return@AudioSpectrumListener).createListener().accept(floats)
						}
					stackPane.children.add((visualizer ?: return@EventHandler).pane)
					(mediaPlayer ?: return@EventHandler).play()
				}
			} catch (e: MalformedURLException) {
				e.printStackTrace()
			}
		}
	}
}