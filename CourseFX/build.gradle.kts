import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.20"
	id("org.openjfx.javafxplugin") version "0.1.0"
	id("application")
	id("idea")
	id("eclipse")
}

group = "org.example"
version = "v" + LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.formdev:flatlaf:3.2.1")
	implementation("com.formdev:flatlaf-intellij-themes:3.2.1")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

idea {
	module {
		jdkName = "17"
	}
}

eclipse {
	jdt {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

application {
	mainClass = "hummel.MainKt"
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
					"Main-Class" to "hummel.MainKt"
				)
			)
		}
		from(configurations.runtimeClasspath.get().map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}
