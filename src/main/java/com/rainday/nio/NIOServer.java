package com.rainday.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wyd on 2019/2/12 17:15:04.
 */
public class NIOServer {
    
    //port
    private int port;
    
    //selector、channel
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(64);
    
    //num of cpu
    private int cpu = Runtime.getRuntime().availableProcessors();
    
    public NIOServer() {
        //default port 800
        this(800);
    }
    
    public NIOServer(int port) {
        this.port = port;
        //step1:打开通道,与selector
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            //step2:设置为非阻塞模式，阻塞模式、非阻塞模式区别
            //http://ifeve.com/server-socket-channel/
            //ServerSocketChannel可以设置成非阻塞模式。在非阻塞模式下，accept() 方法会立刻返回，
            // 如果还没有新进来的连接,返回的将是null。 因此，需要检查返回的SocketChannel是否是null.
            serverSocketChannel.configureBlocking(false);
            //step3:监听端口。两种写法的区别参考如下
            // https://stackoverflow.com/questions/26459002/accept-and-bind-in-serversocket-and-serversocketchannel
//            serverSocketChannel.socket().bind(new InetSocketAddress(DEFAULT_PORT));
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("绑定端口" + port + "成功!");
            //step4:将通道注册到选择器，并且"指定监听接受事件"。
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            this.stop();
        }
    }
    
    public void start() {
        try {
            //step5:轮询selector,获取并处理已经就绪的事件
            while (selector.select() > 0) {
                //step6:遍历已经就绪的事件
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    skDispatcher(sk);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.stop();
        }
    }
    
    public void stop() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException ex) {
        }
    }
    
    public void skDispatcher(SelectionKey sk) {
        try {
            switch (sk.readyOps()) {
                case SelectionKey.OP_ACCEPT:
                    accept(sk);
                    break;
                case SelectionKey.OP_READ:
                    read(sk);
                    break;
                case SelectionKey.OP_WRITE:
                    write(sk);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void accept(SelectionKey sk) throws IOException {
        //由于设置了socketChannel的非阻塞模式，所以如果没有就绪的事件，那么返回的sockChannel则为null
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)sk.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            //将该socketchannel注册到selector
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }
    
    public void read(SelectionKey sk) throws IOException {
        SocketChannel socketChannel = (SocketChannel) sk.channel();
        String temp = null;
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while ((len = socketChannel.read(readBuffer)) > 0) {
            readBuffer.flip();
            temp = new String(readBuffer.array(), 0, len);
            sb.append(temp);
            readBuffer.clear();
        }
        System.out.println(sb.toString());
        //将需要输出的字符串附加在sk中
        sk.attach(sb.toString());
        //将key切换为write
        sk.interestOps(SelectionKey.OP_WRITE);
    }
    
    public void write(SelectionKey sk) throws IOException {
        StringBuilder rb = new StringBuilder();
        rb.append("HTTP/1.1 200 OK");
        rb.append("\r\n");
        rb.append("Content-type: text/html");
        rb.append("\r\n");
        rb.append("Connection: keep-alive");
        rb.append("\r\n\r\n");
        rb.append("<!DOCTYPE html>");
        rb.append("<html lang=\"zh-CN\">");
        rb.append("<head>");
        rb.append("<meta charset=\"UTF-8\">");
        rb.append("</head>");
        rb.append("<body>");
        rb.append("<h1>访问成功！:</h1>" + new Date());
        Thread current = Thread.currentThread();
        
        rb.append("<h1>thread:" + current.getName() + "  " + current.getId() + "</h1>");
        rb.append("<h1>headers & body:" + sk.attachment().toString() + "</h1>");
        rb.append("</body>");
        rb.append("</html>");
        rb.append("\r\n\r\n");
        ByteBuffer writeBuffer = ByteBuffer.wrap(rb.toString().getBytes());
        //将缓冲区的字节数组写入到通道中
        SocketChannel socketChannel = (SocketChannel) sk.channel();
        socketChannel.write(writeBuffer);
        //关闭channcel
        sk.cancel();
        socketChannel.close();
    }
}
