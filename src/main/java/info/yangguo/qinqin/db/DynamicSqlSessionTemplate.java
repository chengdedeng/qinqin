package info.yangguo.qinqin.db;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DynamicSqlSessionTemplate implements SqlSession {
    private static Logger logger = LoggerFactory.getLogger(DynamicSqlSessionTemplate.class);
    private static final String SELECT = "select";
    private static final String INSERT = "insert";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";

    private SqlSessionTemplate sqlSessionTemplate;
    private final SqlSession sqlSessionProxy;


    public DynamicSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class}, new SqlSessionInterceptor());
    }

    /**
     * 拦截SqlSessionTemplate的方法,从而进行读写分离
     * <p/>
     * 注意:如果有事务,事务的入口已经选择了数据源,所以不需要做任何处理,非事务方法数据源的选择在此处完成.
     */
    private class SqlSessionInterceptor implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
            if (synchronizationActive) {
                //此处不try的原因是DynamicDataSourceTransactionManager的doCleanupAfterCompletion会清空threadlocal
                return method.invoke(sqlSessionTemplate, args);
            } else {
                String methodName = method.getName();
                if (methodName.startsWith(SELECT)) {
                    //获取读集群的数据源
                    DataSourceHolder.setSlave();
                    logger.info("Slaver database is selected");
                } else if (methodName.startsWith(INSERT) ||
                        methodName.startsWith(UPDATE) ||
                        methodName.startsWith(DELETE)) {
                    //获取主库数据源
                    DataSourceHolder.setMaster();
                    logger.info("Master database is selected");
                } else {
                    logger.warn("******************Use a more dangerous method({}), please double-check!******************",methodName);
                }
                Object result;
                try {
                    result = method.invoke(sqlSessionTemplate, args);
                } catch (Exception e) {
                    throw e;
                } finally {
                    //清理工作
                    DataSourceHolder.clearDataSource();
                }
                return result;
            }
        }
    }

    public <T> T selectOne(String statement) {
        return sqlSessionProxy.selectOne(statement);
    }

    public <T> T selectOne(String statement, Object parameter) {
        return sqlSessionProxy.selectOne(statement, parameter);
    }

    public <E> List<E> selectList(String statement) {
        return sqlSessionProxy.selectList(statement);
    }

    public <E> List<E> selectList(String statement, Object parameter) {
        return sqlSessionProxy.selectList(statement, parameter);
    }

    public <E> List<E> selectList(String statement, Object parameter,
                                  RowBounds rowBounds) {
        return sqlSessionProxy.selectList(statement, parameter, rowBounds);
    }

    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return sqlSessionProxy.selectMap(statement, mapKey);
    }

    public <K, V> Map<K, V> selectMap(String statement, Object parameter,
                                      String mapKey) {
        return sqlSessionProxy.selectMap(statement, parameter, mapKey);
    }

    public <K, V> Map<K, V> selectMap(String statement, Object parameter,
                                      String mapKey, RowBounds rowBounds) {
        return sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
    }

    public void select(String statement, Object parameter, ResultHandler handler) {
        sqlSessionProxy.select(statement, parameter, handler);
    }

    public void select(String statement, ResultHandler handler) {
        sqlSessionProxy.select(statement, handler);
    }

    public void select(String statement, Object parameter, RowBounds rowBounds,
                       ResultHandler handler) {
        sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    public int insert(String statement) {
        return sqlSessionProxy.insert(statement);
    }

    public int insert(String statement, Object parameter) {
        return sqlSessionProxy.insert(statement, parameter);
    }

    public int update(String statement) {
        return sqlSessionProxy.update(statement);
    }

    public int update(String statement, Object parameter) {
        return sqlSessionProxy.update(statement, parameter);
    }

    public int delete(String statement) {
        return sqlSessionProxy.delete(statement);
    }

    public int delete(String statement, Object parameter) {
        return sqlSessionProxy.delete(statement, parameter);
    }

    public void commit() {
        sqlSessionProxy.commit();
    }

    public void commit(boolean force) {
        sqlSessionProxy.commit(force);
    }

    public void rollback() {
        sqlSessionProxy.rollback();
    }

    public void rollback(boolean force) {
        sqlSessionProxy.rollback(force);
    }

    public List<BatchResult> flushStatements() {
        return sqlSessionProxy.flushStatements();
    }

    public void close() {
        sqlSessionProxy.close();
    }

    public void clearCache() {
        sqlSessionProxy.clearCache();
    }

    public Configuration getConfiguration() {
        return sqlSessionProxy.getConfiguration();
    }

    public <T> T getMapper(Class<T> type) {
        return sqlSessionProxy.getMapper(type);
    }

    public Connection getConnection() {
        return sqlSessionProxy.getConnection();
    }
}
