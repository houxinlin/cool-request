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
    implementation("org.apache.mina:mina-core:2.2.3")
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
    version.set("2022.2")
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