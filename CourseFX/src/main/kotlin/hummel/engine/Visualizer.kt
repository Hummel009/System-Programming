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
	private var visualization: Visualization
	private var dynamics: Array<DynamicSetting?>

	init {
		pane.background = Background(
			BackgroundFill(
				Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY
			)
		)
		pane.viewOrder = 10.0
		visualization = Visualization(windowSize)
		pane.children.add(visualization)
		dynamics = arrayOfNulls(3)
		dynamics[0] = DynamicSetting(1, 5, 50)
		dynamics[1] = DynamicSetting(7, 24, 40)
		dynamics[2] = DynamicSetting(30, 60, 30)
	}

	fun createListener(): Consumer<FloatArray> {
		return Consumer {
			for (d in dynamics) {
				(d ?: return@Consumer).update(it)
			}
			visualization.update(it)
		}
	}

	fun clear() {
		pane.children.clear()
	}
}