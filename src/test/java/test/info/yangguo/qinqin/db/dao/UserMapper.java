package test.info.yangguo.qinqin.db.dao;

import java.util.List;

import test.info.yangguo.qinqin.db.domain.User;


public interface UserMapper {
	
	

	List<User> selectByUserNameAndPwd(User user);
	
	void insert(User user);
}
