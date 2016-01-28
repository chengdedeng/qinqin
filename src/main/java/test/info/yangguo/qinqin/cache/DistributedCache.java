package test.info.yangguo.qinqin.cache;

import java.lang.annotation.*;

/**
 * Created by yangguo on 14-1-28.
 * <p/>
 * 需要缓存的方法的注解，用于方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface DistributedCache {
    String prefix() default "";

    /**
     * 缓存的生命周期
     */
    Lifecycle life() default Lifecycle.ONEHOUR;

    /**
     * 缓存的描述信息
     */
    String describe();

    /**
     * redisTemplate名称
     */
    String templateName();
}
