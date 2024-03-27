/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * build.gradle.kts is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.hxl.plugin"
version = ""

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}
dependencies {
    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.85")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.22.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation(files("deps/openapi-generator-1.0-SNAPSHOT.jar"))
    implementation(files("deps/cool-request-script-api-1.0-SNAPSHOT.jar"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")

}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java", "properties", "org.jetbrains.plugins.yaml", "Kotlin"))
    updateSinceUntilBuild.set(false)

}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"

        }
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
    }
    patchPluginXml {
        sinceBuild.set("203")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}