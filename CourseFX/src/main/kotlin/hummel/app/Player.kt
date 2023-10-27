package hummel.app

import hummel.engine.DynamicSetting
import hummel.engine.Visualization
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
import java.util.function.Consumer

class Player(private var windowSize: WindowSize) {
	private var mediaPlayer: MediaPlayer? = null
	private var player: Player? = null
	private var borderPane: BorderPane = BorderPane()
	private var stackPane: StackPane = StackPane(borderPane)
	private var pane: Pane = Pane()
	private var visualization: Visualization = Visualization(windowSize)
	private var dynamicSettings: Array<DynamicSetting> = arrayOf(
		DynamicSetting(1, 5, 50), DynamicSetting(7, 24, 40), DynamicSetting(30, 60, 30)
	)
	var stage: Stage? = null
	var scene: Scene

	init {
		pane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		pane.viewOrder = 10.0
		pane.children.add(visualization)

		stackPane.alignment = Pos.CENTER
		stackPane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		scene = Scene(stackPane, 1280.0, 720.0)
		scene.onMousePressed = EventHandler<Event> {
			try {
				val media = Media(file.toURI().toURL().toString())
				mediaPlayer = MediaPlayer(media)
				mediaPlayer?.let { mp ->
					mp.onEndOfMedia = Runnable {
						player?.pane?.children?.clear()
						player = null
					}
					if (player == null) {
						player = Player(windowSize)
						player?.let { p ->
							mp.audioSpectrumNumBands = 1024
							mp.audioSpectrumListener =
								AudioSpectrumListener { _: Double, _: Double, floats: FloatArray, _: FloatArray ->
									p.createListener().accept(floats)
								}
							stackPane.children.add(p.pane)
							mp.play()
						}
					}
				}
			} catch (e: MalformedURLException) {
				e.printStackTrace()
			}
		}
	}

	private fun createListener(): Consumer<FloatArray> {
		return Consumer {
			for (setting in dynamicSettings) {
				setting.update(it)
			}
			visualization.update(it)
		}
	}
}