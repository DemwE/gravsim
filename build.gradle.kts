plugins {
    kotlin("jvm") version "2.4.20"
    application
}

application {
    mainClass.set("MainKt")
}

group = "com.github.demwe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.1.0") // Clikt
    implementation("com.google.code.gson:gson:2.11.0") // Gson
    implementation("uk.co.electronstudio.jaylib:jaylib:6.0.+") // Raylib
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.jar {
    archiveFileName.set("gravsim.jar")

    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    doLast {
        copy {
            from(archiveFile)
            into(layout.projectDirectory.dir("out/artifacts/gravsim"))
        }
    }
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
