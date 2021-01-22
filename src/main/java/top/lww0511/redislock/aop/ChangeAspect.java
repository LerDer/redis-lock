package top.lww0511.redislock.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.lww0511.redislock.annotation.Change;
import top.lww0511.redislock.util.RedisKey;
import top.lww0511.redislock.util.RedisUtil;

/**
 * 监听修改
 *
 * @author lww
 */
@Slf4j
@Aspect
@Component
public class ChangeAspect {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 切面
     */
    @Pointcut("@annotation(top.lww0511.redislock.annotation.Change)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Change change = method.getAnnotation(Change.class);
        String className = change.value();
        if (StringUtils.isEmpty(className)) {
            className = RedisKey.CACHE_IN_REDIS + method.getDeclaringClass().getName();
            redisUtil.deleteHashValueByScan(className);
        } else {
            List<String> keys = Arrays.asList(className.split(","));
            keys.forEach(e -> redisUtil.deleteHashValueByScan(RedisKey.CACHE_IN_REDIS + e));
        }
        return point.proceed();
    }
}
