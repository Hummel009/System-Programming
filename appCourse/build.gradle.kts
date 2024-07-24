import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("org.openjfx.javafxplugin")
	id("application")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

dependencies {
	implementation("com.formdev:flatlaf:latest.release")
	implementation("com.formdev:flatlaf-intellij-themes:latest.release")
	implementation("com.tambapps.fft4j:fft4j:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(11)
	}
}

application {
	mainClass = "com.github.hummel.sp.course.MainKt"
}

javafx {
	version = "22-ea+11"
	modules = listOf("javafx.media", "javafx.controls", "javafx.swing")
}

tasks {
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "com.github.hummel.sp.course.MainKt"
				)
			)
		}
		from(configurations.runtimeClasspath.get().map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}
