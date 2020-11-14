package com.mazenk.todoist.integration

import com.mazenk.todoist.GlobalContext
import com.mazenk.todoist.integration.data.SyncData
import com.mazenk.todoist.integration.data.TodoistAccessData
import com.mazenk.todoist.integration.data.TodoistUser
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

object TodoistIntegration {
    private const val baseUrl = "https://todoist.com"
    private val httpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    val server = TodoistServer()

    fun generateAuthenticationLink(state: String): String {
        return """
            $baseUrl/oauth/authorize?client_id=${GlobalContext.config.todoistClientId}
                                    &scope=data:read_write
                                    &state=${URLEncoder.encode(state, "UTF-8")}
        """.trimIndent()
    }

    suspend fun receiveAccessToken(code: String): String {
        val accessData = httpClient.get<TodoistAccessData>("""
            $baseUrl/oauth/access_token?client_id=${GlobalContext.config.todoistClientId}
                                        &client_secret=${GlobalContext.config.todoistClientSecret}
                                        &code=$code
        """.trimIndent())
        return accessData.accessToken
    }

    suspend fun readSync(token: String, resourceTypes: String): SyncData {
        val resourceTypesEncoded = withContext(Dispatchers.IO) {
            URLEncoder.encode(resourceTypes, "UTF-8")
        }

        return httpClient.get("""
            $baseUrl/sync?token=$token
                         &sync_token=*
                         resource_types=${resourceTypesEncoded}
        """.trimIndent())
    }

    suspend fun readUser(token: String): TodoistUser {
        return readSync(token, "['user']").user!!
    }
}
