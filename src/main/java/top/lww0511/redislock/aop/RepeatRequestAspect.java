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
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.lww0511.redislock.annotation.Lock;
import top.lww0511.redislock.util.HttpContextUtils;
import top.lww0511.redislock.util.IPUtils;
import top.lww0511.redislock.util.RedisKey;
import top.lww0511.redislock.util.RedisUtil;

/**
 * @author lww
 * @date 2020-12-21 11:31 AM
 */
@Slf4j
@Aspect
@Component
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
    public Object around(ProceedingJoinPoint point) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(request);
        String servletPath = request.getServletPath();
        //log.info("RepeatRequestAspect_around_ipAddr:{}, servletPath:{}", ipAddr, servletPath);

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Lock lock = method.getAnnotation(Lock.class);
        String lockKey = RedisKey.REQUEST_PREFIX + ipAddr + "_" + servletPath;
        int lockTime = lock.value();
        lockKey = StringUtils.isEmpty(lock.name()) ? lockKey : lock.name() + "_" + ipAddr;
        log.info("RepeatRequestAspect_around_lockTime:{}, lockKey:{}", lockTime, lockKey);
        String value = redisUtil.getValue(lockKey);
        if (!StringUtils.isEmpty(value)) {
            Assert.isTrue(false, "操作太频繁了，请休息一会再操作！");
        }
        redisUtil.setValue(lockKey, lockKey, lockTime, TimeUnit.SECONDS);
        Object proceed = null;
        try {
            proceed = point.proceed();
        } catch (Throwable throwable) {
            log.error("RepeatRequestAspect_around_throwable:{}", throwable);
        } finally {
            redisUtil.remove(lockKey);
        }
        return proceed;
    }

}
