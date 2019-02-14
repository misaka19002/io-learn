package com.rainday.nio;

/**
 * Created by wyd on 2019/2/13 11:27:14.
 */
public class Bootstrap {
    
    public static void main(String[] args) {
        //启动nio server
        NIOServer server = new NIOServer();
        server.start();
    }
}
