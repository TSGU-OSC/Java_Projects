package my.code.service;
import my.code.Dao.UserDaoImpl;
import my.code.bean.User;

public class UserRegisterImpl implements UserRegister{
    private User user;
    private String password;

    public UserRegisterImpl(User user,String password){
        this.user=user;
        this.password=password;
    }



    @Override
    public boolean Register() {
        UserDaoImpl userDao =new UserDaoImpl("root","123456789");
        boolean flag=false;
        try{
            userDao.createUserData(user,password);
            flag=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean doService() {
        return Register();
    }
}
