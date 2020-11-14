package com.mazenk.todoist.telegram.commands

import com.jtelegram.api.kotlin.commands.suspendCommand
import com.jtelegram.api.kotlin.events.message.replyWith
import com.jtelegram.api.kotlin.util.textBuilder
import com.mazenk.todoist.data.AuthenticationHandler

val AuthCommand = suspendCommand { event, command ->
    val userId = event.message.from.id

    if (AuthenticationHandler.findAuth(userId) != null) {
        event.replyWith("You are already logged in!")
        return@suspendCommand
    }

    val authLink = AuthenticationHandler.beginAuth(userId)

    event.replyWith (
        textBuilder {
            link("Click here to authenticate with Todoist", authLink)
        }
    )
}
