package top.lww0511.redislock.util;

/**
 * redis key 信息
 * 前缀 PREFIX
 * 后缀 SUFFIX
 *
 * @author lww
 */
public interface RedisKey {

    /**
     * 前缀
     */
    String REQUEST_PREFIX = "REQUEST_PREFIX_";

    String CACHE_IN_REDIS = "CACHE_IN_REDIS_";

}
