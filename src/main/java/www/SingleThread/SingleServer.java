package www.SingleThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


/**
 * 服务器端
 */
public class SingleServer {
    public static void main(String[] args) throws IOException {
        //创建服务端socket，端口号为6666
        ServerSocket serverSocket = new ServerSocket(6666);
        try{
            System.out.println("等待客户端连接中");
            //等待客户端连接，有客户端连接就返回客户端Socket对象，否则一直阻塞在此处
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为"+client.getPort());
            System.out.println("本机地址为"+serverSocket.getInetAddress());
            //获取客户端的输入输出流
            Scanner clientInput = new Scanner(client.getInputStream());
            clientInput.useDelimiter("\n");
            PrintStream clientOut = new PrintStream(client.getOutputStream(),
                    true,"UTF-8");

            //读取客户端输入
            if(clientInput.hasNext()){
                System.out.println(client.getInetAddress()+"说"+clientInput.next());
            }

            //向客户端输出
            clientOut.println("hello,i am server");

            //关闭输入输出流
            clientInput.close();
            clientOut.close();
            serverSocket.close();
        }catch (IOException e){
            System.out.println("服务端通信异常" + e);
        }
    }
}
