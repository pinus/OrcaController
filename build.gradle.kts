import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.*

val conveyorCommand = "/Applications/Conveyor.app/Contents/MacOS/conveyor"
val conveyorInputDir = "${projectDir}/output"
val buildDate:String = SimpleDateFormat("yyyyMMddHHmm").format(Date())

plugins {
    application
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.1"
}

application {
    mainClass = "open.dolphin.impl.orcon.OrcaController"
}

repositories {
    mavenCentral()
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }
    }
    val deleteConveyorOutput = register<Delete>("delete-conveyor-output") {
        delete(conveyorInputDir)
    }
    val conveyor = register<Exec>("conveyor") {
        group = "distribution"
        description = "make app"
        dependsOn.add(shadowJar)
        dependsOn.add(deleteConveyorOutput)
        workingDir(projectDir)
        val jarName = shadowJar.get().archiveFileName.get()
        val projectVersion = project.property("version")
        val javaVersion = project.property("java.version")
        commandLine = (listOf(
            conveyorCommand,
            "-Kjar.name=${jarName}",
            "-Kproject.version=${projectVersion}",
            "-Kbuild.date=${buildDate}",
            "-Kjava.version=${javaVersion}",
            "-Kapp.machines=mac.aarch64",
            "make", "mac-app"))

    }
    register<Exec>("conveyor-site") {
        group = "distribution"
        description = "make site"
        dependsOn.add(shadowJar)
        dependsOn.add(deleteConveyorOutput)
        workingDir(projectDir)
        val jarName = shadowJar.get().archiveFileName.get()
        val projectVersion = project.property("version")
        val javaVersion = project.property("java.version")
        commandLine = (listOf(
            conveyorCommand,
            "-Kjar.name=${jarName}",
            "-Kproject.version=${projectVersion}",
            "-Kbuild.date=${buildDate}",
            "-Kjava.version=${javaVersion}",
            "make", "copied-site"))
    }
    register<Exec>("tar") {
        group = "distribution"
        description = "make tar"
        dependsOn.add(conveyor)
        commandLine = (listOf(
            "tar", "czf", "${conveyorInputDir}/OrcaController-${buildDate}.tgz",
            "-C", conveyorInputDir, "OrcaController.app"))
    }
    clean {
        dependsOn(deleteConveyorOutput)
    }
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.40.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.3.3")
    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("commons-io:commons-io:2.21.0")
    implementation("com.formdev:flatlaf:3.7")
}
