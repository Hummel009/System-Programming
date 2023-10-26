package hummel.engine

import hummel.app.WindowSize
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.util.function.Consumer

class Visualizer(windowSize: WindowSize) {
	var pane: Pane = Pane()
	private var visualization: Visualization = Visualization(windowSize)
	private var dynamics: Array<DynamicSetting> = arrayOf(
		DynamicSetting(1, 5, 50), DynamicSetting(7, 24, 40), DynamicSetting(30, 60, 30)
	)

	init {
		pane.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
		pane.viewOrder = 10.0
		pane.children.add(visualization)
	}

	fun createListener(): Consumer<FloatArray> {
		return Consumer {
			for (d in dynamics) {
				d.update(it)
			}
			visualization.update(it)
		}
	}

	fun clear() {
		pane.children.clear()
	}
}