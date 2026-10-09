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
    implementation("uk.co.electronstudio.jaylib:jaylib:6.0.+") // Raylib
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.jar {
    archiveFileName.set("gravsim.jar")
    manifest {
        attributes(
            "Main-Class" to "MainKt"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    doLast {
        copy {
            from(archiveFile)
            into(file("${rootDir}/out/artifacts/gravsim"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}