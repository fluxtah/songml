plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.fluxtah.songml"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.redundent:kotlin-xml-builder:1.9.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}