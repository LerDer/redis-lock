package top.lww0511.redislock.util;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
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

    public void setHashValue(String key, String valueKey, String value) {
        stringRedisTemplate.opsForHash().put(key, valueKey, value);
    }

    public Object getHashValue(String key, String valueKey) {
        return stringRedisTemplate.opsForHash().get(key, valueKey);
    }

    public void deleteHashValue(String key) {
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(key);
        keys.forEach(e -> stringRedisTemplate.opsForHash().delete(key, e));
    }

    public void deleteHashValueByScan(String key) {
        Cursor<Entry<Object, Object>> scan = stringRedisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
        while (scan.hasNext()) {
            Entry<Object, Object> next = scan.next();
            Object key1 = next.getKey();
            stringRedisTemplate.opsForHash().delete(key, key1);
        }
        try {
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanHash(String key) {
        Cursor<Entry<Object, Object>> scan = stringRedisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
        while (scan.hasNext()) {
            Entry<Object, Object> next = scan.next();
            Object key1 = next.getKey();
            System.err.println(key1);
            Object value = next.getValue();
            System.err.println(value);
        }
        try {
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanSet(String key) {
        Cursor<String> scan = stringRedisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
        while (scan.hasNext()) {
            String next = scan.next();
            System.err.println(next);
        }
        try {
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
