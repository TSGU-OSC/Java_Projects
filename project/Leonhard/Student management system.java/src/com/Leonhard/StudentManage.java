package com.Leonhard;

import java.util.ArrayList;
import java.util.Scanner;

public class StudentManage {


    public static void main(String[] args) {
        ArrayList<Student> array=new  ArrayList<Student>();
        while(true){
        System.out.println("-------欢迎来到学生管理系统-------");
        System.out.println("1.添加学生");
        System.out.println("2.删除学生");
        System.out.println("3.修改学生");
        System.out.println("4.查看所有学生");
        System.out.println("5.退出");

        Scanner sc=new Scanner(System.in);
        String line = sc.nextLine();

        switch (line) {
            case "1":
                //System.out.println("添加学生");
               addStudent(array);
                break;
            case "2":
                //System.out.println("删除学生");
                deleteAllStudent(array);
                break;
            case "3":
                //System.out.println("修改学生");
                updeteStudent(array);
                break;
            case "4":
                //System.out.println("查看所有学生");
                findAllStudent(array);
                break;
            case "5":
                System.out.println("谢谢使用");

                System.exit(0);
          }
        }
    }

    public static void addStudent(ArrayList<Student> array){
        Scanner sc=new Scanner(System.in);

        System.out.println("请数入学生学号");
        String sid=sc.nextLine();
        System.out.println("请数入学生姓名");
        String name=sc.nextLine();
        System.out.println("请数入学生年龄");
        String age=sc.nextLine();
        System.out.println("请数入学生地址");
        String address=sc.nextLine();


        Student s=new Student();
        s.setSid(sid);
        s.setAddress(address);
        s.setAge(age);
        s.setName(name);

        array.add(s);

        System.out.println("添加学生成功");



    }

    public static void findAllStudent(ArrayList<Student> array){

        if(array.size()==0){
            System.out.println("没有学生信息，请添加学生");
            return;
        }
        System.out.println("学号\t\t姓名\t\t年龄\t\t地址");

        for(int i=0;i<array.size();i++){
            Student s=array.get(i);
            System.out.println(s.getSid() + "\t\t" + s.getName() + "\t\t" + s.getAge() +
                    "\t\t" + s.getAddress());
        }
    }



    public static void deleteAllStudent(ArrayList<Student> array){

        Scanner sc=new Scanner(System.in);

        System.out.println("去输入要删除学生的学号");
        String sid=sc.nextLine();

        for(int i=0;i< array.size();i++){
            Student s=array.get(i);
            if(s.getSid().equals(sid)){
                array.remove(i);
                break;
            }
        }
        System.out.println("删除成功");
    }

    public static void updeteStudent(ArrayList<Student> array){
        Scanner sc=new Scanner(System.in);

        System.out.println("请输入要修改学生的学号");
        String sid=sc.nextLine();

        System.out.println("请输入修改的姓名");
        String name= sc.nextLine();
        System.out.println("请输入修改的年龄");
        String age= sc.nextLine();
        System.out.println("请输入修改的地址");
        String address= sc.nextLine();

        Student s=new Student();
        s.setName(name);
        s.setSid(sid);
        s.setAge(age);
        s.setAddress(address);

        for(int i=0;i< array.size();i++){
            Student student=array.get(i);
            if(student.getSid().equals(sid)){
                array.set(i,s);
                break;
            }
        }
        System.out.println("修改成功");

    }
}


