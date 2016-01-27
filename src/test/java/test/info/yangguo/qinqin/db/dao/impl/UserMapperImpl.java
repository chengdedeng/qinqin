package test.info.yangguo.qinqin.db.dao.impl;

import java.util.List;

import test.info.yangguo.qinqin.db.domain.User;
import test.info.yangguo.qinqin.db.DynamicSqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import test.info.yangguo.qinqin.db.dao.UserMapper;


@Repository("UserMapperImpl")
public class UserMapperImpl implements UserMapper{
	
	@Autowired
	private DynamicSqlSessionTemplate sqlSessionTemplate;

	public List<User> selectByUserNameAndPwd(User user) {
		return sqlSessionTemplate.selectList("selectByUserNameAndPwd", user);
	}

	public void insert(User user) {	
		sqlSessionTemplate.insert("insert", user);
		//Connection connection1 = sqlSessionTemplate.getConnection();
		//Connection connection2 = sqlSessionTemplate.getConnection();
		
	}
}
