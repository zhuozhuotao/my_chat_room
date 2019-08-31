package www.SingleThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleClient {
    public static void main(String[] args) {
        String serverName = "127.0.0.1";
        Integer port = 6666;
        try {
            //创建客户端Socket来连接服务器
            Socket client = new Socket(serverName,port);
            System.out.println("服务器连接成功，地址为"+ client.getInetAddress());
            //获取输入输出流
            PrintStream out = new PrintStream(client.getOutputStream(),
                    true,"UTF-8");
            Scanner in = new Scanner(client.getInputStream());
            in.useDelimiter("\n");

            //向服务器输出内容
            out.println("hello,i am client");

            //读取服务器输入
            if(in.hasNext()){
                System.out.println("服务器发送消息为"+in.next());
            }
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.err.println("客户端通信异常"+e);
        }

    }
}
