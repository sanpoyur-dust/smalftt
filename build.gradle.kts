plugins {
  idea
  `java-library`
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.dokka)
}

allprojects {
  group = "org.yurusanp"
  version = "0.1-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

dependencies {
  testImplementation(libs.kotest.runner.junit5)
}

kotlin {
  jvmToolchain(17)
  sourceSets.all {
    languageSettings {
      languageVersion = "2.0"
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
