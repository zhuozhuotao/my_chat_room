package www.MultiThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 读取服务器信息线程
 */
class ReadFromServerThread implements Runnable{

    private Socket client;

    public ReadFromServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //获取客户端输入流
            Scanner in = new Scanner(client.getInputStream());
            in.useDelimiter("\n");

            while(true){
                if(in.hasNext()){
                    System.out.println("从服务器发来消息"+in.next());
                }
                //此客户端退出
                if(client.isClosed()){
                    System.out.println("客户端已退出");
                    break;
                }
            }
            in.close();
        } catch (IOException e) {
            System.err.println("客户端读线程异常"+e);
        }
    }
}

/**
 * 客户端发送信息给服务器线程
 */

class SendToServerThread implements Runnable{
    private Socket client;

    public SendToServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //获取键盘输入
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");

        //获取客户端输出流
        try {
            PrintStream out = new PrintStream(client.getOutputStream());

            while (true){
                System.out.println("请输入想发送的内容");
                String strToServer;
                if(sc.hasNext()){
                    strToServer = sc.nextLine().trim();
                    out.println(strToServer);

                    //客户端退出标志
                    if(strToServer.equals("byebye")){
                        System.out.println("关闭客户端");
                        sc.close();
                        out.close();
                        client.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("客户端写异常"+e);
        }
    }
}

public class MultiClient{
    public static void main(String[] args) {
        try {
            Socket client = new Socket("127.0.0.1",6666);

            //读取服务器消息线程
            Thread readFromServer = new Thread(new ReadFromServerThread(client));

            //给服务器发消息线程
            Thread sendToServer = new Thread(new SendToServerThread(client));
            readFromServer.start();
            sendToServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
