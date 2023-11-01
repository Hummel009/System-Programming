package hummel.app

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
import java.net.MalformedURLException
import java.util.function.Consumer

class Player(windowSize: WindowSize) {
	private var mediaPlayer: MediaPlayer? = null
	private var visualization: Visualization = Visualization(windowSize)
	private var playing: Boolean = false
	var scene: Scene

	init {
		val pane = Pane()
		pane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		pane.viewOrder = 10.0

		val borderPane = BorderPane()
		val stackPane = StackPane(borderPane)
		stackPane.alignment = Pos.CENTER
		stackPane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		scene = Scene(stackPane, 1280.0, 720.0)
		scene.onMousePressed = EventHandler<Event> {
			try {
				val media = Media(file.toURI().toURL().toString())
				mediaPlayer = MediaPlayer(media)
				mediaPlayer?.let { mp ->
					mp.audioSpectrumNumBands = 1024
					mp.audioSpectrumListener =
						AudioSpectrumListener { _: Double, _: Double, floats: FloatArray, _: FloatArray ->
							Consumer<FloatArray> { visualization.update(it) }.accept(floats)
						}
					if (!playing) {
						pane.children.add(visualization)
						stackPane.children.add(pane)
						mp.play()
						playing = true
					}
					mp.onEndOfMedia = Runnable {
						pane.children.clear()
						stackPane.children.clear()
						mediaPlayer = null
						playing = false
					}
				}
			} catch (e: MalformedURLException) {
				e.printStackTrace()
			}
		}
	}
}