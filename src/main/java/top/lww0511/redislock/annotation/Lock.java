package top.lww0511.redislock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lww
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Lock {

    /**
     * @return 锁的名称
     */
    String name() default "";

    /**
     * @return 锁定时间，默认3秒
     */
    int value() default 3;

    /**
     * @return 是否强制模式
     * true 一次请求后，必须经过锁定时间后才可访问
     * false 方法执行完后会自动删除锁，即方法执行完就可以再次访问
     */
    boolean hard() default false;

    /**
     * @return 是否是分布式锁
     * true 分布式锁 所有用户同一把锁，即key相同
     * false 每个用户锁不同，防止单个用户重复提交(提交过快 ， 如点击按钮太快)
     */
    boolean distributed() default true;
}
