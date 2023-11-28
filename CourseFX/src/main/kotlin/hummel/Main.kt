package hummel

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import hummel.app.App
import hummel.app.FourierTransform
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
import kotlin.concurrent.thread
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
		JFXPanel()

		title = "Hummel009's Audio Master"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(0, 0, 550, 225)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPane = contentPanel

		val twoColumnPanel = JPanel()
		twoColumnPanel.layout = GridLayout(0, 2, 5, 5)

		twoColumnPanel.add(JLabel("Длина записи (секунд):"))
		val timeField = JTextField(50)
		timeField.text = "5"
		twoColumnPanel.add(timeField)

		twoColumnPanel.add(JLabel("Выбор файла:"))
		val fileField = JTextField(50)
		fileField.text = "${System.getProperty("user.home")}\\Downloads\\test.wav"
		twoColumnPanel.add(fileField)

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
		twoColumnPanel.add(radioSym)
		twoColumnPanel.add(radioBar)

		val oneColumnPanel = JPanel()
		oneColumnPanel.layout = GridLayout(0, 1, 5, 5)

		val recButton = JButton("Запись звука")
		recButton.addActionListener {
			thread {
				val exeFilePath = "CourseNative.exe"
				val exeFile = File(exeFilePath)
				if (exeFile.exists()) {
					val parameters = listOf(timeField.text, fileField.text)
					val processBuilder = ProcessBuilder(exeFilePath, *parameters.toTypedArray())
					val process = processBuilder.start()
					val result = process.waitFor()
					if (result == 0) {
						JOptionPane.showMessageDialog(
							this, "Запись завершена!", "Успех", JOptionPane.INFORMATION_MESSAGE
						)
					} else {
						JOptionPane.showMessageDialog(
							this, "Микрофон недоступен!", "Ошибка", JOptionPane.ERROR_MESSAGE
						)
					}
				} else {
					JOptionPane.showMessageDialog(
						this, "Файл недоступен!", "Ошибка", JOptionPane.ERROR_MESSAGE
					)
				}
			}
		}
		oneColumnPanel.add(recButton)

		val visButton = JButton("Запуск визуализации")
		visButton.addActionListener {
			thread {
				val wavFilePath = fileField.text
				val wavFile = File(wavFilePath)
				if (wavFile.exists()) {
					val app = App(if (sym) VisSym() else VisBar(), wavFile)
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
				} else {
					JOptionPane.showMessageDialog(
						this, "Файл недоступен!", "Ошибка", JOptionPane.ERROR_MESSAGE
					)
				}
			}
		}
		oneColumnPanel.add(visButton)

		val fftButton = JButton("Запуск анализа")
		fftButton.addActionListener {
			thread {
				val wavFilePath = fileField.text
				val wavFile = File(wavFilePath)
				if (wavFile.exists()) {
					try {
						val fft = FourierTransform(wavFile)
						fft.execute()
					} catch (e: Exception) {
						JOptionPane.showMessageDialog(
							this, "Разложение недоступно!", "Ошибка", JOptionPane.ERROR_MESSAGE
						)
					}
				} else {
					JOptionPane.showMessageDialog(
						this, "Файл недоступен!", "Ошибка", JOptionPane.ERROR_MESSAGE
					)
				}
			}
		}
		oneColumnPanel.add(fftButton)

		contentPanel.add(twoColumnPanel, BorderLayout.NORTH)
		contentPanel.add(oneColumnPanel, BorderLayout.SOUTH)

		setLocationRelativeTo(null)
	}
}