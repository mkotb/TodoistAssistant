package com.mazenk.todoist

import com.jtelegram.api.TelegramBotRegistry
import java.io.File
import java.io.FileReader
import java.nio.file.Files

data class BotConfig (
    val redisUrl: String = "redis://localhost",
    val telegramKey: String = "Insert API key here",
    val todoistClientId: String = "Insert Todoist Client ID here",
    val todoistClientSecret: String = "Insert Todoist Client Secret here"
)

fun loadBotConfig(): BotConfig? {
    val configPath = System.getenv("CONFIG_PATH")

    if (configPath == null || configPath.isEmpty()) {
        println("Config path is not configured!")
        return null
    }

    val file = File(configPath)

    if (file.isDirectory) {
        println("Config path is not configured correctly!")
        return null
    }

    if (!file.exists()) {
        val config = BotConfig()

        file.createNewFile()
        Files.write(file.toPath(), TelegramBotRegistry.GSON.toJson(config).split("\n"))

        println("Default config written. Please configure it to proceed.")
        return null
    }

    return TelegramBotRegistry.GSON.fromJson (
        FileReader(file),
        BotConfig::class.java
    )
}
