package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class LoginFrame extends JFrame{
    private Socket socket;    // 客户的socket
    private String userName;    // 客户端的名称

    private JPanel jp;    // 面板
    private JTextField name;    // 文本框
    private JTextField IP;    // 文本框
    private JTextField port;
    private JButton jb;    // 按键
    private Box IPBox = Box.createHorizontalBox();
    private Box portBox = Box.createHorizontalBox();
    private Box nameBox = Box.createHorizontalBox();
    private JLabel IPLabel = new JLabel("IP地址");
    private JLabel portLabel = new JLabel("端口");
    private JLabel nameLabel = new JLabel("用户名");

    public LoginFrame() {
        this.init();
    }

    /**
     * 初始化
     */
    public void init() {
        // 初始化组件
        jp = new JPanel();    // 面板

        IP = new JTextField(20);    //文本框， 指定文本框大小
        // 设置提示文字
        IP.setText("127.0.0.1");     // 提示用户输入用户名
        IP.setForeground(Color.gray);     // 提示文字颜色
        IPBox.add(IPLabel);
        IPBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        IPBox.add(IP);

        port = new JTextField(20);    //文本框， 指定文本框大小
        // 设置提示文字
        port.setText("8888");     // 提示用户输入用户名
        port.setForeground(Color.gray);     // 提示文字颜色
        portBox.add(portLabel);
        portBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        portBox.add(port);

        name = new JTextField(20);    //文本框， 指定文本框大小
        // 设置提示文字
        name.setText("");     // 提示用户输入用户名
        name.setForeground(Color.gray);     // 提示文字颜色
        nameBox.add(nameLabel);
        nameBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        nameBox.add(name);

        jb = new JButton("确定");    // 按键
        jb.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent a){
                try {
                    SIGN();    //用户登录
                } catch (IOException e) {
                    port.setText("连接超时,请稍后再试!");
                    IP.setText("连接超时,请稍后再试!");
                }
            }
        });
        // 将组件添加到面板上
        jp.add(IPBox);
        jp.add(portBox);
        jp.add(nameBox);
        jp.add(jb);
        // 将面板添加到窗体中
        this. add(jp);
        // 设置标题，大小， 位置， 是否可见
        this.setTitle("用户登录");
        this.setSize(300, 200);
        this.setLocation(700, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // 窗口关闭 程序退出
        this.setVisible(true);     // 窗口可见
    }

    /**
     * 用户登录
     */
    public void SIGN() throws IOException {
        PrintStream printStream = null;
        userName = name.getText();    // 获取用户输入的名称
        String strPort = port.getText();
        String strIP = IP.getText();
        if (userName.equals("")) {
            name.setText("用户名不能为空");    // 提示用户名不能为空
            name.setForeground(Color.gray);     // 提示文字颜色
        }else if (strIP.equals("")||strIP.equals("连接超时,请稍后再试!")) {
            IP.setText("IP地址不能为空");    // 提示用户名不能为空
            IP.setForeground(Color.gray);     // 提示文字颜色
        }else if (strPort.equals("")||strPort.equals("连接超时,请稍后再试!")) {
            port.setText("端口不能为空");    // 提示用户名不能为空
            port.setForeground(Color.gray);     // 提示文字颜色
        } else {
            Socket socket = new Socket(strIP, Integer.parseInt(strPort));
            String temp = "Login:" + userName;
            try {
                //1.获取服务器端的输出流
                printStream = new PrintStream(socket.getOutputStream());
                if (!userName.equals("")) {
                    printStream.println(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.setVisible(false);    // 登录窗口设为不可见
            // 启动客户端
            Thread client = new Thread(new test(userName, socket));
            client.start();
        }
    }
}