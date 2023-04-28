package my.code.service;

import my.code.Dao.UserDaoImpl;
import my.code.bean.User;

import java.sql.ResultSet;

public class UserLoginImpl implements UserLogin{
    private String account;
    private String password;

    public UserLoginImpl(String account,String password){
        this.account=account;
        this.password=password;
    }

    @Override
    public boolean Login() {
        UserDaoImpl userDao = new UserDaoImpl("root","123456789");
        User user = new User();
        user.setAccount(account);
        boolean flag =false;
        try{
            String resultSetString=null;
            ResultSet resultSet= userDao.searchUserData(user);
            while(resultSet.next()){
                resultSetString=resultSet.getString("password");
            }
            flag=password.equals(resultSetString);
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean doService() {
        return Login();
    }
}
