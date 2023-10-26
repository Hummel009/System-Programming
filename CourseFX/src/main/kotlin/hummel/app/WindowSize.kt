package hummel.app

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue

class WindowSize {
	var width: DoubleProperty = SimpleDoubleProperty()
	var height: DoubleProperty = SimpleDoubleProperty()

	fun bind(width: ObservableValue<out Number>, height: ObservableValue<out Number>) {
		this.width.bind(width)
		this.height.bind(height)
	}
}
