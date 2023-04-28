package my.code.Dao;

import my.code.bean.User;

import java.sql.ResultSet;

public interface UserDao {
    void createUserData(User user,String password)throws Exception;
    void deleteUserData()throws Exception;
    ResultSet searchUserData(User user)throws Exception;
    void updateUserData(User user)throws Exception;
}
