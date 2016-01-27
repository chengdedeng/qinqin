package test.info.yangguo.qinqin.db.datasource;

import test.info.yangguo.qinqin.db.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import test.info.yangguo.qinqin.db.dao.UserMapper;


public class UserMapperTest{

	UserMapper userMapper;
	
	@Before
    public void before(){        
		String[] xmls = new String[]{ "classpath:applicationContext.xml","classpath:dataSource.xml","classpath:applicationContext-tx.xml" };
		//String[] xmls = new String[]{ "classpath:applicationContext.xml","classpath:dataSource.xml"};
        ApplicationContext context = new ClassPathXmlApplicationContext(xmls);
        userMapper = (UserMapper) context.getBean("UserMapperImpl");
    }
	
	@Test
	public void testUserMapper() throws Throwable{
		User user = new User();
		//user.setUserId(3726434L);
		user.setUserName("ttt");
		userMapper.insert(user);
	}

	@Test
	public void testSelectByUserNameAndPwd() throws Throwable{
		User user = new User();
		//user.setUserId(3726434L);
		user.setUserName("ttt");
		user.setPassword("1");
		userMapper.selectByUserNameAndPwd(user);
		userMapper.selectByUserNameAndPwd(user);
	}
	
}
