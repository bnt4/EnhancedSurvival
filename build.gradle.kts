plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

group = "com.github.bnt4"
version = "1.3.0"
description = "EnhancedSurvival"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    val props = mapOf("version" to project.version)

    inputs.properties(props)

    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}
