package main.java;

import main.java.window.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Window window = new Window();
    Server(){
        window.portButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent a){
                Thread thread = new Thread(()->{
                    execute();
                });window.portInput.setEditable(false);window.portButton.setEnabled(false);thread.start();
            }
        });
        window.run();
    }
    public void execute(){
        ServerSocket serverSocket = null;    // 创建服务端套接字
        try {
            serverSocket = new ServerSocket(Integer.parseInt(window.portInput.getText()));
            // 创建线程池,从而可以处理多个客户端
            ExecutorService executorService= Executors.newFixedThreadPool(20);//最大20个
            int i = 0;
            while (true) {
                Socket socket = serverSocket.accept();    // 监听客户端的情况，等待客户端连接
                executorService.execute(new serverSocket(socket,window.output));    // 对每一个客户端，创建一个套接字处理器线程
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();    // 关闭serverSocket通道
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
