package www.MultiThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiServer {

    //存储所有注册的客户
    private static Map<String,Socket> clientMap = new ConcurrentHashMap<String,Socket>();

    //具体处理与某个客户端通信的内部类
    private static class ExecuteClient implements Runnable{
        private Socket client;

        public ExecuteClient(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(client.getInputStream());
                String strFromClient;

                while(true){
                    if(in.hasNext()){
                        strFromClient = in.nextLine();
                        //windows下默认将行/r/n中的/r替换成了空字符串
                        //进行/r过滤，windows下进行换行过滤
                        Pattern pattern = Pattern.compile("\r");//识别要处理的特殊字符
                        Matcher matcher =pattern.matcher(strFromClient);//过滤
                        strFromClient = matcher.replaceAll("");//替换

                        //注册流程
                        if(strFromClient.startsWith("userName")){
                            String userName = strFromClient.split("\\:")[1];
                            registerUser(userName,client);
                            continue;
                        }

                        //群聊流程
                        if(strFromClient.startsWith("G")){
                            String msg = strFromClient.split("\\:")[1];
                            groupChat(msg);
                            continue;
                        }

                        //私聊流程
                        if(strFromClient.startsWith("P")){
                            String userName = strFromClient.split("\\:")[1]
                                    .split("-")[0];
                            String msg = strFromClient.split("\\:")[1]
                                    .split("-")[1];
                            privateChat(userName,msg);
                        }
                        //用户退出
                        if(strFromClient.contains("byebye")){
                            String useName = null;

                            //根据socket找到user
                            for (String keyName:clientMap.keySet()
                                    ) {
                                if(clientMap.get(keyName).equals(client)){
                                    useName = keyName;
                                }
                            }
                            System.out.println("用户"+useName+"下线了");
                            clientMap.remove(useName);
                            continue;
                        }

                    }
                }
            } catch (IOException e) {
                System.err.println("服务器通信异常，错误为"+e);
            }

        }

        //注册方法
        private void registerUser(String userName,Socket client){
            System.out.println("用户名为"+userName);
            System.out.println("用户"+userName+"上线了");
            System.out.println("当前群聊人数为"+(clientMap.size()+1)+"人");
            //将用户信息保存在map中
            clientMap.put(userName,client);

            try {
                PrintStream out = new PrintStream(client.getOutputStream(),
                        true,"UTF-8");

                //告知用户注册成功
                out.println("用户注册成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //群聊流程
        private void groupChat(String msg){
            //取出clientMap中的Entry遍历发送群聊消息
            Set<Map.Entry<String,Socket>> clientSet = clientMap.entrySet();

            for(Map.Entry<String,Socket> entry:clientSet){
                //取出每个客户端的输出流
                try {
                    Socket socket = entry.getValue();
                    PrintStream out = new PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println("群聊信息为:"+msg);
                } catch (IOException e) {
                    System.err.println("群聊异常"+e);
                }
            }
        }
        //私聊流程
        private void privateChat(String userName,String msg){
            Socket privateSocket = clientMap.get(userName);
            try {
                PrintStream out = new PrintStream(privateSocket.getOutputStream(),
                        true,"UTF-8");
                out.println("私聊信息为:"+msg);
            } catch (IOException e) {
                System.err.println("私聊异常"+e);
            }

        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ServerSocket serverSocket = new ServerSocket(6666);
        for(int i = 0;i < 20;i++){
            System.out.println("等待客户端连接");
            Socket client = serverSocket.accept();
            System.out.println("有新的端口号连接，端口号为："+client.getPort());
            executorService.submit(new ExecuteClient(client));
        }
        executorService.shutdown();
        serverSocket.close();
    }
}
