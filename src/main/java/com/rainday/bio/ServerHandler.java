package com.rainday.bio;

import com.rainday.Calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by wyd on 2019/1/31 19:15:02.
 */
public class ServerHandler implements Runnable {
    
    private Socket socket;
    
    public ServerHandler() {
    }
    
    public ServerHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter writer = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.defaultCharset()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            String queryString = null;
            String bodyString = null;
            
            /**
             * 接受HTTP请求
             */
            String requestHeader;
            int contentLength = 0;
            while ((requestHeader = in.readLine()) != null && !requestHeader.isEmpty()) {
                System.out.println("header:" + requestHeader);
                /**
                 * 获得GET参数
                 */
                if (requestHeader.startsWith("GET")) {
                    int begin = requestHeader.indexOf("/?") + 2;
                    int end = requestHeader.indexOf("HTTP/");
                    queryString = requestHeader.substring(begin, end);
                    System.out.println("GET参数是：" + queryString);
                }
                /**
                 * 获得POST参数
                 * 1.获取请求内容长度
                 */
                if (requestHeader.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(requestHeader.split(":")[1].trim());
                    System.out.println("POST参数长度是：" + contentLength);
                }
            }
            StringBuffer sb = new StringBuffer();
            if (contentLength > 0) {
                for (int i = 0; i < contentLength; i++) {
                    sb.append((char) in.read());
                }
                bodyString = sb.toString();
                System.out.println("POST参数是：" + bodyString);
            }
            //发送回执
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-type:text/html");
            writer.println();
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"zh-CN\">");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\">");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<h1>访问成功！:</h1>" + new Date());
            Thread current = Thread.currentThread();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            writer.println("<h1>thread:" + current.getName() + "  " + current.getId() + "</h1>");
            writer.println("<h1>query:" + queryString + "</h1>");
            writer.println("<h1>body:" + bodyString + "</h1>");
            writer.println("<h1>result:" + Calculator.cal(queryString) + "</h1>");
            writer.println("</body>");
            writer.println("</html>");
            
            writer.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
