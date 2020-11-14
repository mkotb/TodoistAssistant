package com.mazenk.todoist

import com.jtelegram.api.TelegramBotRegistry
import com.jtelegram.api.kotlin.registerBot
import com.jtelegram.api.update.PollingUpdateProvider
import com.mazenk.todoist.integration.TodoistIntegration
import com.mazenk.todoist.telegram.TodoistBot

suspend fun main() {
    val botRegistry = TelegramBotRegistry.builder()
        .updateProvider(PollingUpdateProvider.builder().build())
        .build()

    val bot = botRegistry.registerBot("")

    GlobalContext.botUsername = bot.botInfo.username

    TodoistIntegration.server.start()
    TodoistBot.initialize(bot)
}
