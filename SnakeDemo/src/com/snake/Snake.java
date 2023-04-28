package com.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Snake extends JFrame implements KeyListener, ActionListener, MouseListener {

    int slong = 2;//蛇当前长度
    //蛇坐标
    int[] Snakex = new int[100];
    int[] Snakey = new int[100];
    int fx = 1;//蛇的方向 0-左 1-右 2-上 3-下
    Timer timer = new Timer(100, this);//设置定时器，每100毫秒一次
    //食物位置
    int foodx;
    int foody;
    Random random = new Random();//随机数，随机位置生成食物
    int started = 0;//游戏信息 0-未开始 1-开始 2-结束


    //---------------------------------------------------------------------------------------------------------------------
    //窗体
    public void myJFrame() {

        this.setTitle("贪吃蛇"); //标题
        this.setSize(800, 600); //窗口大小
        this.setResizable(false); //窗口是否可以改变大小=否
        this.setDefaultCloseOperation(Snake.EXIT_ON_CLOSE); //窗口关闭方式为关闭窗口同时结束程序

        int width = Toolkit.getDefaultToolkit().getScreenSize().width; //获取屏幕宽度
        int height = Toolkit.getDefaultToolkit().getScreenSize().height; //获取屏幕高度
//        System.out.println("宽度："+width);//测试
//        System.out.println("高度："+height);//测试

        this.setLocation((width - 800) / 2, (height - 600) / 2); //设置窗口默认位置以屏幕居中

        this.setFocusable(true);
        this.addKeyListener(this);


        this.setVisible(true); //窗口是否显示=是

//        蛇的初识位置
        Snakex[0] = 60;
        Snakey[0] = 100;
        Snakex[1] = 40;
        Snakey[1] = 100;

//        随机食物的初识位置
        foodx = random.nextInt(39);
        foody = random.nextInt(22);
        foodx = foodx * 20;
        foody = foody * 20 + 80;

        System.out.println(foodx + "，" + foody);
    }


    //---------------------------------------------------------------------------------------------------------------------
    //覆写paint方法,绘制界面
    public void paint(Graphics g) {
//        绘制背景
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 800, 600);

        //绘制游戏区域
        g.setColor(Color.black);
        g.fillRect(0, 80, 800, 520);

        //绘制蛇
        for (int i = 0; i < slong; i++) {
            g.setColor(Color.CYAN);
            g.fillRect(Snakex[i], Snakey[i], 20, 20);
        }


        //        绘制食物
        g.setColor(Color.yellow);
        g.fillOval(foodx, foody, 20, 20);


        if (started == 0) {
            g.setColor(Color.BLACK);//设置画笔颜色
            g.setFont(new Font("微软雅黑", 10, 20)); //设置字体
            g.drawString("按下“空格键”开始游戏", 300, 65); //绘制字符
        } else if (started == 1) {
            g.setColor(Color.BLACK);//设置画笔颜色
            g.setFont(new Font("微软雅黑", 10, 20)); //设置字体
            g.drawString("当前分数为：", 300, 65); //绘制字符
            g.drawString(String.valueOf(slong - 2), 420, 65); //绘制字符
        } else if (started == 2) {
            g.setColor(Color.BLACK);//设置画笔颜色
            g.setFont(new Font("微软雅黑", 10, 20)); //设置字体
            g.drawString("游戏结束-", 250, 65); //绘制字符
            g.drawString("最终分数为：", 350, 65); //绘制字符
            g.drawString(String.valueOf(slong - 2), 470, 65); //绘制字符
        }

    }


    //    操作监听——控制蛇的不断移动
    @Override
    public void actionPerformed(ActionEvent e) {
//        判断游戏是否开始
        if (started == 1) {
//            通过循环控制蛇移动
            for (int i = slong - 1; i > 0; i--) {
                Snakex[i] = Snakex[i - 1];
                Snakey[i] = Snakey[i - 1];
            }

//            判断蛇移动的方向
            if (fx == 0) {
                Snakex[0] = Snakex[0] - 20;
            } else if (fx == 1) {
                Snakex[0] = Snakex[0] + 20;
            } else if (fx == 2) {
                Snakey[0] = Snakey[0] - 20;
            } else if (fx == 3) {
                Snakey[0] = Snakey[0] + 20;
            }

//            判断蛇是否撞到墙外
            if (Snakex[0] < 0 || Snakex[0] > 780 || Snakey[0] < 80 || Snakey[0] > 580) {
                started = 2;
            }

//            判断蛇是否吃到了食物
            if (Snakex[0] == foodx && Snakey[0] == foody) {

                slong++;

                foodx = random.nextInt(39);
                foody = random.nextInt(22);
                foodx = foodx * 20;
                foody = foody * 20 + 80;

                System.out.println(foodx + "，" + foody);
            }

//            判断是否吃到了自己
            for (int i = 1; i < slong; i++) {
                if (Snakex[0] == Snakex[i] && Snakey[0] == Snakey[i]) {
                    started = 2;
                }
            }

//            判断食物是否随机在了蛇身上
            for (int i = 0; i < slong; i++) {
                if (foodx == Snakex[i] && foody == Snakey[i]) {

                    //        随机食物的初识位置
                    foodx = random.nextInt(39);
                    foody = random.nextInt(22);
                    foodx = foodx * 20;
                    foody = foody * 20 + 80;

                    System.out.println(foodx + "，" + foody);
                }
            }


            repaint();
        }
        timer.start();
    }

    //    输入
    @Override
    public void keyTyped(KeyEvent e) {

    }

    //    键盘按下——控制游戏的开始以及蛇的移动方向
    @Override
    public void keyPressed(KeyEvent e) {
//        获取从键盘输入的键
        int key = e.getKeyCode();
//        判断是否为空格
        if (key == KeyEvent.VK_SPACE) {
            if (started == 0) {
                started = 1;
            } else if (started == 1) {
                started = 0;
            } else if (started == 2) {
                started = 0;

                slong = 2;//蛇当前长度

                //        蛇的初识位置
                Snakex[0] = 60;
                Snakey[0] = 100;
                Snakex[1] = 40;
                Snakey[1] = 100;

                //        随机食物的初识位置
                foodx = random.nextInt(39);
                foody = random.nextInt(22);
                foodx = foodx * 20;
                foody = foody * 20 + 80;

                //                初始化方向
                fx = 1;
            }

            repaint();
            timer.start();

//            左
        } else if (key == KeyEvent.VK_LEFT) {
            if (fx != 1) {
                fx = 0;
            }
//            右
        } else if (key == KeyEvent.VK_RIGHT) {
            if (fx != 0) {
                fx = 1;
            }
//            上
        } else if (key == KeyEvent.VK_UP) {
            if (fx != 3) {
                fx = 2;
            }
//下
        } else if (key == KeyEvent.VK_DOWN) {
            if (fx != 2) {
                fx = 3;
            }
        }

    }

    //    弹起
    @Override
    public void keyReleased(KeyEvent e) {

    }

    //    点击
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    //    按下
    @Override
    public void mousePressed(MouseEvent e) {

    }

    //    抬起
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    //    进入
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    //    离开
    @Override
    public void mouseExited(MouseEvent e) {

    }
}


