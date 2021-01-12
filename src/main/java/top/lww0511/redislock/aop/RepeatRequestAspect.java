package top.lww0511.redislock.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.lww0511.redislock.annotation.Lock;
import top.lww0511.redislock.util.HttpContextUtils;
import top.lww0511.redislock.util.IPUtils;
import top.lww0511.redislock.util.RedisKey;
import top.lww0511.redislock.util.RedisUtil;

/**
 * AOP handle repeat request
 *
 * @author lww
 * @date 2020-12-21 11:31 AM
 */
@Slf4j
@Aspect
@Component
@ComponentScan(basePackages = "top.lww0511")
public class RepeatRequestAspect {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 切面
     */
    @Pointcut("@annotation(top.lww0511.redislock.annotation.Lock)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(request);
        String servletPath = request.getServletPath();

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Lock lock = method.getAnnotation(Lock.class);
        int lockTime = lock.value();
        boolean hard = lock.hard();
        boolean distributed = lock.distributed();
        String lockKey = RedisKey.REQUEST_PREFIX + servletPath + (distributed ? "" : ("_" + ipAddr));
        lockKey = StringUtils.isEmpty(lock.name()) ? lockKey : RedisKey.REQUEST_PREFIX + lock.name() + (distributed ? "" : ("_" + ipAddr));
        log.info("RepeatRequestAspect_around_lockTime:{}, hard:{}, lockKey:{}", lockTime, hard, lockKey);
        String value = redisUtil.getValue(lockKey);
        if (!StringUtils.isEmpty(value)) {
            Assert.isTrue(false, "操作太频繁了，请休息一会再操作！");
        }
        redisUtil.setValue(lockKey, lockKey, lockTime, TimeUnit.SECONDS);
        Object proceed;
        try {
            proceed = point.proceed();
        } finally {
            if (!hard) {
                redisUtil.remove(lockKey);
            }
        }
        return proceed;
    }

}
