
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.hxl.plugin"
version = "1.0.3"

repositories {
    maven { url =uri ("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
//    implementation(files("/home/LinuxWork/project/java/openapi-generator/build/libs/openapi-generator-1.0-SNAPSHOT.jar"))
    implementation(files("D:\\project\\java\\openapi-generator\\build\\libs\\openapi-generator-1.0-SNAPSHOT.jar"))
}
tasks.jar{
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) }
    from(contents)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveFileName.set("spring-boot-invoke-plugin.jar")

}
// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("203")
        untilBuild.set("232.*")
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