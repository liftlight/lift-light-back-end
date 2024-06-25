package com.liftlight.infrastructure.persistence

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * Redis 설정 클래스
 */
@EnableRedisRepositories
@EnableCaching
@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: Int
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun sortedSetTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<Any, Any> {
        val template = RedisTemplate<Any, Any>()
        template.connectionFactory = redisConnectionFactory
        template.setDefaultSerializer(StringRedisSerializer()) // StringRedisSerializer 사용
        return template
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory?): RedisCacheManager {
        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(3)) // 캐시 만료 기간 4시간
            .serializeKeysWith(
                RedisSerializationContext
                    .SerializationPair
                    .fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext
                    .SerializationPair
                    .fromSerializer(GenericJackson2JsonRedisSerializer())
            )

        return RedisCacheManager
            .builder(
                RedisCacheWriter.lockingRedisCacheWriter(
                    redisConnectionFactory!!
                )
            )
            .cacheDefaults(cacheConfiguration)
            .build()
    }
}