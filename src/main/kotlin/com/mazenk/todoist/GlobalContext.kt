package com.mazenk.todoist

import com.jtelegram.api.TelegramBotRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.net.URI
import kotlin.system.exitProcess

object GlobalContext {
    val config: BotConfig
    var botUsername: String = "Unknown"
    val redis = Redis()
    private val redisPool: JedisPool

    init {
        val loaded: BotConfig?

        try {
            loaded = loadBotConfig()
        } catch (ex: Exception) {
            println("An unexpected error occurred during config load")
            ex.printStackTrace()
            exitProcess(-127)
        }

        config = loaded ?: exitProcess(0)
        redisPool = resolveRedis()
    }

    private fun resolveRedis(): JedisPool {
        var localRedis: JedisPool? = null

        while (localRedis == null) {
            try {
                localRedis = JedisPool(JedisPoolConfig(), URI(config.redisUrl))
            } catch (ex: Exception) {
                println("Unable to connect to redis")
                ex.printStackTrace()

                println("Trying again in 5 seconds...")
                Thread.sleep(5000L)
            }
        }

        return localRedis
    }

    class Redis {
        suspend fun <T> useRaw(usage: (Jedis) -> T): T = withContext(Dispatchers.IO) {
            redisPool.resource.use {  redis ->
                usage(redis)
            }
        }

        suspend fun <T> save(key: String, obj: T) = useRaw { redis ->
            redis[key] = TelegramBotRegistry.GSON.toJson(obj)
        }

        suspend inline fun <reified T> find(key: String): T? = useRaw { redis ->
            TelegramBotRegistry.GSON.fromJson(redis[key], T::class.java)
        }

        suspend inline fun delete(key: String) = useRaw { redis ->
            redis.del(key)
        }
    }
}
