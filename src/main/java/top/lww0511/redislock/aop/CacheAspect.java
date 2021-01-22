package top.lww0511.redislock.aop;

import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
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
        boolean hash = cache.value();
        Object[] args = point.getArgs();
        Parameter[] parameters = method.getParameters();
        //组成唯一key
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            params.append(parameters[i].getName()).append("=").append(args[i]);
            if (i != args.length - 1) {
                params.append("&");
            }
        }
        String className = RedisKey.CACHE_IN_REDIS + method.getDeclaringClass().getName();
        String cacheKey = className + "." + method.getName() + "?" + (hash ? params.toString().hashCode() : params);
        log.info("CacheAspect_around_cacheKey:{}", cacheKey);
        //从Redis中取
        Object value = redisUtil.getHashValue(className, cacheKey);
        if (value == null) {
            Object proceed = point.proceed();
            redisUtil.setHashValue(className, cacheKey, JSONObject.toJSONString(proceed));
            return proceed;
        } else {
            Class<?> returnType = method.getReturnType();
            return JSONObject.parseObject(value.toString(), returnType);
        }
    }

}
