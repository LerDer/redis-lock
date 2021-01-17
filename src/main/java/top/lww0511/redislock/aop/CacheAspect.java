package top.lww0511.redislock.aop;

import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.lww0511.redislock.annotation.Cache;
import top.lww0511.redislock.util.RedisKey;
import top.lww0511.redislock.util.RedisUtil;

/**
 * AOP 缓存
 *
 * @author lww
 * @date 2021-01-17 11:30 AM
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 切面
     */
    @Pointcut("@annotation(top.lww0511.redislock.annotation.Cache)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Cache cache = method.getAnnotation(Cache.class);
        int catchTime = cache.value();
        String cacheKey = RedisKey.CACHE_IN_REDIS + method.getDeclaringClass().getName() + "." + method.getName();
        log.info("CacheAspect_around_catchTime:{}, cacheKey:{}", catchTime, cacheKey);
        String value = redisUtil.getValue(cacheKey);
        if (StringUtils.isEmpty(value)) {
            Object proceed = point.proceed();
            redisUtil.setValue(cacheKey, JSONObject.toJSONString(proceed), catchTime, TimeUnit.MINUTES);
            return proceed;
        } else {
            Class<?> returnType = method.getReturnType();
            return JSONObject.parseObject(value, returnType);
        }
    }

}