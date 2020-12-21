package top.lww0511.redislock.util;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lww
 * @since 2020-04-11
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void remove(String key) {
        stringRedisTemplate.opsForValue().set(key, "", 1, TimeUnit.MILLISECONDS);
    }

    public void setValue(String key, String value, Integer expireTime) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public void setValue(String key, String value, Integer expireTime, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, unit);
    }

    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
