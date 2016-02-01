package info.yangguo.qinqin.cache;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yangguo on 14-4-1.
 */
@Component("springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {
    //Spring应用上下文环境
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据bean的class类型来获得bean
     *
     * @param classType bean所对应的class
     * @return
     */
    public static Map getBean(Class classType) {
        Map map = applicationContext.getBeansOfType(classType);
        return map;
    }

    /**
     * 根据bean的name来获得bean
     *
     * @param name bean的name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}
