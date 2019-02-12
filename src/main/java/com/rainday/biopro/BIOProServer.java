package com.rainday.biopro;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wyd on 2019/1/31 15:05:37.
 * bio工作原理
 */
public class BIOProServer {
    
    private static final int DEFAULT_PORT = 800;
    private static final int THREAD_SIZE = 3;
    
    public static void main(String[] args) {
        AtomicLong count = new AtomicLong();
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_SIZE);
        try {
            //创建serverSocket，并监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器已启动，端口号：" + DEFAULT_PORT);
            while (true) {
                System.out.println("loop: " + count.getAndIncrement());
                //当有链接建立。accept方法会阻塞执行，直到链接建立成功
                Socket socket = serverSocket.accept();
                //当有新的客户端接入时，会执行下面的代码
                //使用现有的线程池执行提交的任务,下面三种写法均可
                CompletableFuture.runAsync(new ProServerHandler(socket), executorService);
//                executorService.submit(new ProServerHandler(socket));
//                executorService.execute(new ProServerHandler(socket));
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
