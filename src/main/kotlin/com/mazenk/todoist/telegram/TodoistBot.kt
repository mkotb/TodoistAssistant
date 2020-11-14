package com.mazenk.todoist.telegram

import com.jtelegram.api.chat.id.ChatId
import com.jtelegram.api.kotlin.KTelegramBot
import com.jtelegram.api.kotlin.execute
import com.jtelegram.api.requests.message.send.SendText
import com.mazenk.todoist.data.AuthenticationHandler
import com.mazenk.todoist.data.UserAuthentication
import com.mazenk.todoist.telegram.commands.AuthCommand
import kotlin.properties.Delegates

object TodoistBot {
    var bot: KTelegramBot by Delegates.notNull()

    fun initialize(bot: KTelegramBot) {
        this.bot = bot
        bot.commandRegistry.registerCommand("auth", AuthCommand)

        AuthenticationHandler.addHook(this::onAuth)
    }

    suspend fun onAuth(authData: UserAuthentication) {
        bot.execute (
            SendText.builder()
                .chatId(ChatId.of(authData.telegramUserId))
                .text("Successfully authenticated with Todoist!")
                .build()
        )
    }
}