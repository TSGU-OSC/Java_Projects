import java.util.Random;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Random r =new Random();
        int number=r.nextInt(100)+1;
        while(true) {
            System.out.println("请输入你要猜的数字");
            Scanner sc = new Scanner(System.in);
            int guessnumber = sc.nextInt();

            if (guessnumber > number) {
                System.out.println("你猜的数字" + guessnumber + "大了");

            } else if (guessnumber < number) {
                System.out.println("你猜的数字" + guessnumber + "小了");

            } else {
                System.out.println("恭喜 你，猜对了");
                break;
            }
        }

    }
}