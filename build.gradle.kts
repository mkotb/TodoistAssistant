plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.mazenk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    mavenLocal() // TODO remove

    maven {
        name = "GitHub Packages"
        url = uri("https://maven.pkg.github.com/jTelegram/jTelegramBotAPI")
        credentials {
            username = System.getProperty("gpr.user") ?: ""
            password = System.getProperty("gpr.key")
        }
    }
}

val ktorVersion = "1.3.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    // kTelegram
    implementation("com.jtelegram:ktelegrambotapi:4.0.12")

    // Redis
    implementation("redis.clients:jedis:3.2.0")

    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}