package info.yangguo.qinqin.cache;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;
import info.yangguo.qinqin.utils.JsonUtil;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by yangguo on 14-1-22.
 */
@Component("distributedCacheInterceptor")
@Aspect
public class DistributedCacheInterceptor {
    private static Logger logger = LoggerFactory.getLogger(DistributedCacheInterceptor.class);
    private static Map<String, RedisTemplate> redisTemplateMap = Maps.newConcurrentMap();

    @Resource(name = "springContextUtil")
    private SpringContextUtil springContextUtil;

    //切入点要拦截的类
    @Pointcut(value = "@annotation(distributedCache)")
    private void cachePointcut(DistributedCache distributedCache) {
    }

    @Around("cachePointcut(distributedCache)")
    public Object cache(ProceedingJoinPoint pjp, DistributedCache distributedCache) throws CacheException {
        RedisTemplate redisTemplate = null;
        String templateName = distributedCache.templateName();
        if (!redisTemplateMap.containsKey(templateName)) {
            redisTemplateMap.put(templateName, (RedisTemplate) springContextUtil.getBean(templateName));
        }
        redisTemplate = redisTemplateMap.get(templateName);

        if (redisTemplate == null) {
            throw new CacheException("RedisTemplate不存在", new NullPointerException());
        }


        StringBuilder key = new StringBuilder();
        Object target = pjp.getTarget();
        //获取类名
        String className = target.getClass().getName();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String returnTypeName = signature.getReturnType().getName();
        String methodName = signature.getMethod().getName();
        //返回值为void的方法是不能使用缓存的
        if (returnTypeName.equals("void")) {
            throw new CacheException("没有返回值的方法不能缓存");
        }

        if (distributedCache.prefix().equals("")) {
            key.append(className).append("_").append(returnTypeName).append("_").append(methodName);
        } else {
            key.append(distributedCache.prefix());
        }

        StringBuilder argsStringBuilder = new StringBuilder();
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            argsStringBuilder.append("_").append(JsonUtil.toJson(arg, false));
        }
        String argsString = argsStringBuilder.toString();
        if (argsString.startsWith("_{")) {
            //因为key的前缀我们基本根据业务来定，前缀基本唯一，后缀限定在方法级别，所以hash冲突极小，可以忽略
            key.append("_").append(Hashing.murmur3_128().hashBytes(argsString.getBytes()));
        } else {
            key.append(argsString);
        }


        Object cacheObject = null;
        try {
            BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key.toString());
            cacheObject = boundValueOperations.get();
            if (cacheObject == null) {
                try {
                    cacheObject = pjp.proceed();
                } catch (Throwable throwable) {
                    throw new CacheException("切入点方法执行出现异常，请检查切入点方法!", throwable);
                }
                //设定缓存的生命周期
                if (cacheObject != null) {
                    boundValueOperations.set(cacheObject, distributedCache.life().getAmount(), distributedCache.life().getTimeUnit());
                    logger.info("{}---key={}缓存成功", distributedCache.describe(), key.toString());
                }
            } else {
                logger.debug("{}---key={}缓存命中", distributedCache, key.toString());
            }
        } catch (JedisConnectionException exception) {
            logger.warn("redis连接出现异常", exception);
        } catch (RedisConnectionFailureException exception) {
            logger.warn("redis连接出现异常", exception);
        }
        return cacheObject;
    }
}
