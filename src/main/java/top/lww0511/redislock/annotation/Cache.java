package top.lww0511.redislock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 添加缓存
 *
 * @author lww
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Cache {

    /**
     * @return 锁定时间，默认1分钟
     */
    int value() default 1;

    /**
     * @return 时间单位
     */
    TimeUnit unit() default TimeUnit.MINUTES;

}
