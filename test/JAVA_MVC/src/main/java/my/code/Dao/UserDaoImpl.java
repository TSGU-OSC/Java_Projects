package my.code.Dao;

import my.code.bean.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDaoImpl implements UserDao{
    private Connection connection;
    private Statement statement;
    public UserDaoImpl(String username,String password){
        String url="jdbc:mysql://127.0.0.1:3306";
        try{
            connection = DriverManager.getConnection(url,username,password);

        }catch (Exception e){
            e.printStackTrace();
        }
        initialize();
    }

    private void initialize(){
        try{
            statement= connection.createStatement();
            statement.executeUpdate("use test");
            String sql= """
                    CREATE TABLE IF NOT EXISTS userData(
                    username varchar(20),
                    account varchar(20),
                    password varchar(20)
                    )CHARSET=utf8
                    """;
            statement.executeUpdate(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void createUserData(User user,String password) throws Exception{
        if(statement.isClosed()){
            initialize();
        }
        String sql= "insert into userData(username,account,password) values ("+"'"+user.getUsername()+"','"+user.getAccount()+"','"+password+"')";
        statement.executeUpdate(sql);

    }

    @Override
    public void deleteUserData() throws Exception{

    }

    @Override
    public ResultSet searchUserData(User user) throws Exception{
        if(statement.isClosed()){
            initialize();
        }
        String sql="select password from userdata where account="+"'"+user.getAccount()+"'";
        return statement.executeQuery(sql);
    }

    @Override
    public void updateUserData(User user) throws Exception{
        if(statement.isClosed()){
            initialize();
        }
        String sql="update userData set username"+"'"+user.getUsername()+"','"+user.getAccount()+"'where account ="+"'"+user.getAccount()+"'";
        statement.executeUpdate(sql);
    }
}
