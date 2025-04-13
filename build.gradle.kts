plugins {
    kotlin("jvm") version "2.1.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.fluxtah.songml"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.fluxtah.songml.MainKt")
}

tasks {
    shadowJar {
        archiveBaseName.set("songml")
        archiveClassifier.set("all")
        archiveVersion.set("") // clean output name: songml-all.jar
    }
}

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