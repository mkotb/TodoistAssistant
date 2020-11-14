package com.mazenk.todoist.data

import com.mazenk.todoist.GlobalContext
import com.mazenk.todoist.integration.TodoistIntegration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

data class UserAuthentication (
    val telegramUserId: Long,
    val authToken: String,
)

typealias AuthenticationHook = suspend (UserAuthentication) -> Unit

object AuthenticationHandler {
    private val hooks = ArrayList<AuthenticationHook>()

    fun addHook(authHook: AuthenticationHook) {
        hooks.add(authHook)
    }

    suspend fun beginAuth(telegramUserId: Long): String {
        val state = generateRandomSecret()
        GlobalContext.redis.save("auth-keys:$state", telegramUserId) // save their user id for future reference
        return TodoistIntegration.generateAuthenticationLink(state)
    }

    suspend fun findAuth(telegramUserId: Long): UserAuthentication? {
        return GlobalContext.redis.find("user-auth:$telegramUserId")
    }

    suspend fun findTelegramUser(state: String): Long? {
        return GlobalContext.redis.find("auth-keys:$state")
    }

    suspend fun handleAuthToken(state: String, authToken: String, telegramUserId: Long) {
        val userAuth = UserAuthentication(telegramUserId, authToken)

        GlobalContext.redis.delete("auth-keys:$state")
        GlobalContext.redis.save("user-auth:$telegramUserId", userAuth)

        hooks.forEach {
            it(userAuth)
        }
    }

    private suspend fun generateRandomSecret(): String {
        val random = SecureRandom()

        return withContext(Dispatchers.IO) {
            Base64.getEncoder().encodeToString (
                random.generateSeed(128)
            )
        }
    }

}