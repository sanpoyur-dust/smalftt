plugins {
  idea
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.dokka)
}

allprojects {
  group = "org.yurusanp"
  version = "0.1.0" + "-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

kotlin {
  jvmToolchain(17)
  sourceSets.all {
    languageSettings {
      languageVersion = "2.0"
    }
  }
}

dependencies {
  testImplementation(rootProject.libs.kotest.runner.junit5)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
