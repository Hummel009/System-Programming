package hummel

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import hummel.app.App
import hummel.app.VisBar
import hummel.app.VisSym
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.stage.Stage
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.system.exitProcess

fun main() {
	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatGitHubDarkIJTheme())
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	init {
		title = "Hummel009's Audio Master"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(0, 0, 500, 186)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPane = contentPanel

		val inputPanel = JPanel()
		inputPanel.layout = GridLayout(0, 2, 5, 5)

		inputPanel.add(JLabel("Длина записи (секунд):"))
		val timeField = JTextField(20)
		timeField.text = "5"
		inputPanel.add(timeField)

		inputPanel.add(JLabel("Выбор файла:"))
		val fileField = JTextField(20)
		fileField.text = "C:\\Users\\Hummel009\\Downloads\\test.wav"
		inputPanel.add(fileField)

		var sym = true
		val radioSym = JRadioButton("Симметричная визуализация")
		val radioBar = JRadioButton("Плиточная визуализация")
		radioSym.isSelected = true
		radioSym.addActionListener {
			sym = true
			radioBar.isSelected = false
			radioSym.isSelected = true
		}
		radioBar.addActionListener {
			sym = false
			radioSym.isSelected = false
			radioBar.isSelected = true
		}
		inputPanel.add(radioSym)
		inputPanel.add(radioBar)

		val recButton = JButton("Запись звука")
		recButton.addActionListener {
			val exePath =
				"D:\\Source\\System-Programming\\CourseNative\\build\\bin\\native\\releaseExecutable\\CourseNative.exe"
			val parameters = listOf(timeField.text, fileField.text)

			val processBuilder = ProcessBuilder(exePath, *parameters.toTypedArray())
			val process = processBuilder.start()
			process.waitFor()
			JOptionPane.showMessageDialog(
				this, "Запись завершена", "Message", JOptionPane.INFORMATION_MESSAGE
			)
		}

		val visButton = JButton("Запуск визуализации")
		visButton.addActionListener {
			JFXPanel()
			val app = App(if (sym) VisSym() else VisBar(), File(fileField.text))
			Platform.runLater {
				val stage = Stage()
				stage.title = "Hummel009's Media Player"
				stage.setScene(app.scene)
				stage.show()
				stage.setOnCloseRequest {
					it.consume()
					stage.hide()
					exitProcess(0)
				}
			}
		}

		contentPanel.add(inputPanel, BorderLayout.NORTH)
		contentPanel.add(recButton, BorderLayout.CENTER)
		contentPanel.add(visButton, BorderLayout.SOUTH)

		setLocationRelativeTo(null)
	}
}