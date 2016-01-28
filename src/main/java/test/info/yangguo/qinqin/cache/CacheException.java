package test.info.yangguo.qinqin.cache;

/**
 * Created by yangguo on 14-2-17.
 */
public class CacheException extends Exception {
    /**
     * @param content   异常描述信息
     * @param throwable
     */
    public CacheException(String content, Throwable throwable) {
        super(content, throwable);
    }

    /**
     * @param content 异常描述信息
     */
    public CacheException(String content) {
        super(content);
    }
}
