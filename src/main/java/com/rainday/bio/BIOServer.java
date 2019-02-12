package com.rainday.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wyd on 2019/1/31 15:05:37.
 * bio工作原理
 */
public class BIOServer {
    
    private static final int DEFAULT_PORT = 800;
    
    public static void main(String[] args) {
        AtomicLong count = new AtomicLong();
        ServerSocket serverSocket = null;
        try {
            //创建serverSocket，并监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器已启动，端口号：" + DEFAULT_PORT);
            while (true) {
                System.out.println("loop: " + count.getAndIncrement());
                //当有链接建立。accept方法会阻塞执行，直到链接建立成功
                Socket socket = serverSocket.accept();
                //当有新的客户端接入时，会执行下面的代码
                //然后创建一个新的线程处理这条Socket链路
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (IOException e) {
        
        } finally {
            //一些必要的清理工作
            if (serverSocket != null) {
                System.out.println("即将关闭服务器。。。");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
