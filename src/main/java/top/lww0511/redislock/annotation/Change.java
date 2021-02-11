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
public @interface Change {

    /**
     * @return 缓存产生的类的全限定名，多个用逗号(,)分隔，
     * 不设置 则为当前方法所在的类的全限定名
     */
    String[] value() default "";
}
