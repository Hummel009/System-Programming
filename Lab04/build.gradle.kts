import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.multiplatform") version "1.9.20"
	id("idea")
}

group = "hummel"
version = "v" + LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

repositories {
	mavenCentral()
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

kotlin {
	mingwX64 {
		binaries {
			executable {
				entryPoint("hummel.main")
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
