package com.liftlight.infrastructure

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


/**
 * Redis 서비스
 */
@Service
class RedisService(private val stringRedisTemplate: RedisTemplate<String, String>) {

    fun saveKeyAndValue(key: String, value: String, expireTime: Int) {
        val ops = stringRedisTemplate.opsForValue()
        ops[key] = value
        stringRedisTemplate.expire(key, expireTime.toLong(), TimeUnit.MINUTES)
    }

    fun getValueByKey(refreshToken: String?): String? {
        val ops = stringRedisTemplate.opsForValue()
        return ops[refreshToken!!]
    }

    fun deleteValueByKey(refreshToken: String) {
        stringRedisTemplate.delete(refreshToken)
    }
}