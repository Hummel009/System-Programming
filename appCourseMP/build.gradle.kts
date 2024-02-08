import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.multiplatform")
	id("idea")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(11)
	}
}

kotlin {
	mingwX64 {
		binaries {
			executable {
				entryPoint("com.github.hummel.coursemp.main")
				linkerOpts("-lwinmm")
				baseName = "${project.name}-${project.version}"
			}
		}
	}
	sourceSets {
		configureEach {
			languageSettings {
				optIn("kotlinx.cinterop.ExperimentalForeignApi")
			}
		}
	}
}
