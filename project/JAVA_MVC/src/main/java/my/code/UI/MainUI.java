package my.code.UI;

import my.code.bean.User;
import my.code.service.*;

import java.lang.reflect.Constructor;
import java.util.Scanner;

public class MainUI {
    private static final MainUI instance=new MainUI();
    public MainUI(){

    }

    public void generateUI(){
        System.out.println("welcome MVC test");
        System.out.println("plz select login/register");
        Scanner scanner=new Scanner(System.in);
        String command="";
        while(!command.equals("exit")){
            command=scanner.nextLine();
            if(command.equals("login")){
                String[] strings=new String[2];
                System.out.println("plz write account");
                strings[0]= scanner.nextLine();
                System.out.println("plz write password");
                strings[1]= scanner.nextLine();
                analyzer(command,strings);
            }else if(command.equals("register")){
                String[] strings=new String[3];
                System.out.println("plz write account");
                strings[0]= scanner.nextLine();
                System.out.println("plz write username");
                strings[1]= scanner.nextLine();
                System.out.println("plz write password");
                strings[2]= scanner.nextLine();
                analyzer(command,strings);
            }
        }
    }

    public void analyzer(String command,String[] parameters){
        switch(command){
            case "login" -> {
                if(call(new UserLoginImpl(parameters[0],parameters[1]))){
                    System.out.println("welcome "+parameters[0]);
                }else{
                    System.out.println("invalid account or password");
                }
            }
            case "register" -> {
                User user = new User();
                user.setAccount(parameters[0]);
                user.setUsername(parameters[1]);
                if(call(new UserRegisterImpl(user,parameters[2]))){
                    System.out.println("account "+parameters[0]+"success");
                }else{
                    System.out.println("account "+parameters[0]+"fail");
                }
            }
        }
    }
    public boolean call(Service service){
        return service.doService();
    }
    public static MainUI getInstance(){
        return instance;
    }

}
