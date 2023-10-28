package hummel

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import hummel.app.App
import javafx.application.Application
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder

lateinit var file: File

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
		setBounds(0, 0, 500, 156)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPane = contentPanel

		val inputPanel = JPanel()
		inputPanel.layout = GridLayout(0, 2, 5, 5)

		inputPanel.add(JLabel("Длина записи:"))
		val timeField = JTextField(20)
		timeField.text = "5000"
		inputPanel.add(timeField)

		inputPanel.add(JLabel("Выбор файла:"))
		val fileField = JTextField(20)
		fileField.text = "C:\\Users\\Hummel009\\Downloads\\test.wav"
		inputPanel.add(fileField)

		val recButton = JButton("Запись звука")
		recButton.addActionListener {
			val exePath =
				"D:\\Source\\System-Programming\\CourseNative\\build\\bin\\native\\releaseExecutable\\CourseNative.exe"
			val parameters = listOf(timeField.text, fileField.text)

			val processBuilder = ProcessBuilder(exePath, *parameters.toTypedArray())
			val process = processBuilder.start()
			val exitCode = process.waitFor()
			JOptionPane.showMessageDialog(
				this, "Код завершения: $exitCode", "Message", JOptionPane.INFORMATION_MESSAGE
			)
		}

		val visButton = JButton("Запуск визуализации")
		visButton.addActionListener {
			file = File(fileField.text)
			Application.launch(App::class.java)
		}

		contentPanel.add(inputPanel, BorderLayout.NORTH)
		contentPanel.add(recButton, BorderLayout.CENTER)
		contentPanel.add(visButton, BorderLayout.SOUTH)

		setLocationRelativeTo(null)
	}
}