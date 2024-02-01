plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("org.jetbrains.intellij") version "1.10.1"
    id("io.sentry.jvm.gradle") version "4.2.0"
}

group = "com.hxl.plugin"
version = "2024.2.15"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}
dependencies {
    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.85")

    implementation("org.apache.mina:mina-core:2.2.3")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.22.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation(files("deps/openapi-generator-1.0-SNAPSHOT.jar"))
    implementation(files("deps/cool-request-script-api-1.0-SNAPSHOT.jar"))
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java", "properties", "org.jetbrains.plugins.yaml", "Kotlin"))
    updateSinceUntilBuild.set(false)

}
sentry  {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext.set(true)

    org.set("tingfengshiye")
    projectName.set("java")
    authToken.set("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")
}

tasks {
    // Set the JVM compatibility versions

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
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