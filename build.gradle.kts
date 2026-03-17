import org.gradle.kotlin.dsl.create

plugins {
    java
    id("com.gradleup.shadow") version "9.4.0"
    `maven-publish`
}

group = "com.github.Tenebris-Lux"
version = "0.3.0-Testing"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.hytale.com/release") }
    maven { url = uri("https://maven.hytale.com/pre-release") }
    maven { url = uri("https://www.cursemaven.com") }
    maven {
        name = "hMReleases"
        url = uri("https://maven.hytale-mods.dev/releases")
    }
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.02.19-1a311a592")
    implementation("curse.maven:hyui-1431415:7731691")
    compileOnly("com.buuz135:MultipleHUD:1.0.6")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        relocate("io.github.elliesaur.hyui", "com.github.tenebrislux.hff.libs.hyui")
        relocate("org.jsoup", "com.github.tenebrislux.hff.libs.jsoup")

        from("src/main/resources") {
            include("manifest.json")
        }
    }

    javadoc {
        options {
            (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            encoding = "UTF-8"
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["shadow"])

            groupId = "com.github.Tenebris-Lux"
            artifactId = "HFF"
            version = project.version.toString()
        }
    }
}