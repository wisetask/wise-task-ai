package ru.leti.wisetask.ai.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import tools.jackson.databind.ObjectMapper
import ru.leti.wisetask.ai.dto.CachedProfile
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${app.cache.ttl}")
    private val cacheTtl: Duration
) {

    @Bean
    fun localCacheManager(
        @Value("\${cache.max-size:1000}") maxSize: Long,
        @Value("\${cache.ttl:1h}") ttl: Duration
    ): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
        )
        return cacheManager
    }

    @Bean
    fun redisCacheConfiguration(): RedisCacheConfiguration {
        val valueSerializer = JacksonJsonRedisSerializer(objectMapper, CachedProfile::class.java)
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(cacheTtl)
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(valueSerializer)
            )
    }

    @Bean
    @Primary
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration())
            .transactionAware()
            .build()
    }
}
