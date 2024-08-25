package src;

import src.file.client.src.clientUserInputPanel;
import src.file.server.src.serverUserInputPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class test extends JFrame implements Runnable{

    private Socket socket = null;
    private String userName = null;

    //---------------窗体模块--------------------
    private Box up = Box.createHorizontalBox();     //上半部分Box,包含左私聊框和右群聊框,水平
    private Box bottom = Box.createHorizontalBox();     //下半部分Box,包含几个系统指令
    private Box map = Box.createVerticalBox();
    private Box privateChat = Box.createVerticalBox();
    private Box publicChat = Box.createVerticalBox();
    private Box privateChatOutput = Box.createVerticalBox();      //垂直
    private Box privateChatInput = Box.createHorizontalBox();         //水平
    private Box publicChatOutput = Box.createVerticalBox();
    private Box publicChatInput = Box.createHorizontalBox();
    //--------------------私聊模块-------------------
    private JLabel privateChatOutputLabel = new JLabel("私聊消息");
    private JTextArea privateChatOutputArea = new JTextArea(30,1);
    private JScrollPane privateChatOutputAreaScrollPane = new JScrollPane(privateChatOutputArea);
    private JLabel privateChatInputUserNameLabel = new JLabel("对用户:");
    private JTextField privateChatInputUserNameInput = new JTextField(1);
    private JLabel privateChatInputMsgLabel = new JLabel("说:");
    private JTextField privateChatInputMsgInput = new JTextField(20);
    private JButton privateChatInputSendMsg = new JButton("发送消息");
    private JButton privateChatInputSendFileServer = new JButton("服务端传输文件");
    private JButton privateChatInputSendFileClient = new JButton("客户端传输文件");
    //---------------------私聊模块结束-------------------
    //---------------------群聊模块----------------------
    private JLabel publicChatOutputLabel = new JLabel("群聊消息");
    private JTextArea publicChatOutputArea = new JTextArea(30,1);
    private JScrollPane publicChatOutputAreaScrollPane = new JScrollPane(publicChatOutputArea);
    private JLabel publicChatInputMsgLabel = new JLabel("在群聊里说:");
    private JTextField publicChatInputMsgInput = new JTextField(20);
    private JButton publicChatInputSend = new JButton("发送");
    //---------------------群聊模块结束-------------------
    //---------------------系统命令----------------------
    private JButton button1 = new JButton("查询在线好友");
    private JButton button2 = new JButton("查询离线好友");
    private JButton button3 = new JButton("添加用户框内用户好友");
    private JButton button4 = new JButton("删除用户框内用户好友");
    private JButton button5 = new JButton("查看用户框内用户历史消息");
    private JButton button6 = new JButton("查看好友申请");
    private JButton button7 = new JButton("同意首条好友申请");
    private JButton button8 = new JButton("拒绝首条好友申请");
    Thread reading;
    private PrintStream printStream = null;
    public test(String userName, Socket socket) {
        this.userName = userName;
        this.socket = socket;
        this.initJ();    // 初始化组件
        this.initBottom();  //添加按钮点击事件
    }

    private void initJ() {
        //初始化组件
        privateChatOutputArea.setEditable(false);
        publicChatOutputArea.setEditable(false);

        privateChatOutput.add(privateChatOutputLabel);
        privateChatOutput.add(Box.createHorizontalStrut(10));
        privateChatOutput.add(privateChatOutputAreaScrollPane);
        privateChatOutput.setBounds(0,0,250,300);

        privateChatInput.add(privateChatInputUserNameLabel);
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add(privateChatInputUserNameInput);
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add((privateChatInputMsgLabel));
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add(privateChatInputMsgInput);
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add(privateChatInputSendMsg);
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add(privateChatInputSendFileServer);
        privateChatInput.add(Box.createVerticalStrut(10));
        privateChatInput.add(privateChatInputSendFileClient);
        privateChatInput.setBounds(0,300,250,10);

        privateChat.add(privateChatOutput);
        privateChatOutput.add(Box.createHorizontalStrut(10));
        privateChat.add(privateChatInput);
        privateChat.setBounds(0,0,250,350);

        publicChatOutput.add(publicChatOutputLabel);
        publicChatOutput.add(Box.createHorizontalStrut(10));
        publicChatOutput.add(publicChatOutputAreaScrollPane);
        publicChatOutput.setBounds(250,0,250,300);

        publicChatInput.add(publicChatInputMsgLabel);
        publicChatInput.add(Box.createVerticalStrut(10));
        publicChatInput.add(publicChatInputMsgInput);
        publicChatInput.add(Box.createVerticalStrut(10));
        publicChatInput.add(publicChatInputSend);
        publicChatInput.setBounds(250,300,250,10);

        publicChat.add(publicChatOutput);
        publicChatOutput.add(Box.createHorizontalStrut(10));
        publicChat.add(publicChatInput);
        publicChat.setBounds(250,0,250,350);

        up.add(privateChat);
        up.add(Box.createHorizontalStrut(10));
        up.add(publicChat);
        up.setBounds(0,0,700,350);

        bottom.add(button1);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button2);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button3);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button4);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button5);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button6);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button7);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(button8);
        bottom.setBounds(0,350,700,200);

        map.add(up);
        publicChatInput.add(Box.createVerticalStrut(10));
        map.add(bottom);

        this.add(map);
        this.setSize(1500,600);
        this.setVisible(true);
    }
    private void initBottom(){
        privateChatInputSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(privateChatInputUserNameInput.getText()!=""&&privateChatInputMsgInput.getText()!=""){
                    String msg = "@" + privateChatInputUserNameInput.getText() + "-" + privateChatInputMsgInput.getText();
                    privateChatInputMsgInput.setText("");
                    sendDataToServerPrivate(msg);
                }
                else{
                    privateChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    privateChatOutputArea.append("#系统消息\n发送对象/消息为空,发送失败!\n");   // 在自己的聊天界面中显示
                }
            }
        });
        publicChatInputSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(publicChatInputMsgInput.getText()!=""){
                    String msg = "#@nobody-" + publicChatInputMsgInput.getText();
                    publicChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    publicChatOutputArea.append(publicChatInputMsgInput.getText() + "\n");   // 在自己的聊天界面中显示
                    sendDataToServerPublic(msg);
                }
                else{
                    publicChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    publicChatOutputArea.append("#系统消息\n发送消息为空,发送失败!\n");   // 在自己的聊天界面中显示
                }
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer("#queryOnlineFriend");
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer("#queryDeadFriend");
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Objects.equals(privateChatInputUserNameInput.getText(), ""))
                    sendDataToServer("#addFriend-"+privateChatInputUserNameInput.getText());
                else{
                    privateChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    privateChatOutputArea.append("#系统消息\n请求对象为空,发送失败!\n");   // 在自己的聊天界面中显示
                }
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Objects.equals(privateChatInputUserNameInput.getText(), ""))
                    sendDataToServer("#delete-"+privateChatInputUserNameInput.getText());
                else{
                    privateChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    privateChatOutputArea.append("#系统消息\n请求对象为空,发送失败!\n");   // 在自己的聊天界面中显示
                }
            }
        });
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Objects.equals(privateChatInputUserNameInput.getText(), ""))
                    sendDataToServer("#history-"+privateChatInputUserNameInput.getText());
                else{
                    privateChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
                    privateChatOutputArea.append("#系统消息\n请求对象为空,发送失败!\n");   // 在自己的聊天界面中显示
                }
            }
        });
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer("#queryFriendApplication");
            }
        });
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer("#passFirstFriendApplication");
            }
        });
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer("#refuseFirstFriendApplication");
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.out.println("程序关闭!");sendDataToServer("#exit");System.exit(0);}
        });
        // 文件发送按钮监听
        privateChatInputSendFileServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverUserInputPanel serverUserInputPanel = new serverUserInputPanel();
                serverUserInputPanel.run();
            }
        });
        privateChatInputSendFileClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientUserInputPanel clientUserInputPanel = new clientUserInputPanel();
                clientUserInputPanel.run();
            }
        });
    }

    private void sendDataToServerPrivate(String text){
        privateChatOutputArea.append(new Date().toString() + "\n");   // 在自己的聊天界面中显示
        privateChatOutputArea.append("#"+userName+"：" + text + "\n");   // 在自己的聊天界面中显示
        try {
            // 发送数据
            printStream = new PrintStream(socket.getOutputStream());
            printStream.println(text);
            printStream.flush();
            // 清空自己当前的会话框
            privateChatInputMsgInput.setText("");
        } catch (IOException el) {
            el.printStackTrace();
        }
    }

    private void sendDataToServerPublic(String text){
        try {
            // 发送数据
            printStream = new PrintStream(socket.getOutputStream());
            printStream.println(text);
            printStream.flush();
            // 清空自己当前的会话框
            publicChatInputMsgInput.setText("");
        } catch (IOException el) {
            el.printStackTrace();
        }
    }
    private void sendDataToServer(String text){
        try {
            // 发送数据
            printStream = new PrintStream(socket.getOutputStream());
            printStream.println(text);
            printStream.flush();
        } catch (IOException el) {
            el.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner scanner = null;
        while (true) {
            if(scanner == null){
                try {
                    scanner = new Scanner(socket.getInputStream());    // 获取接收服务器信息的扫描器
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            while (scanner.hasNext()) {
                String msg = scanner.next();
                if(Objects.equals(msg, "public:")){
                    publicChatOutputArea.append(scanner.next() + System.lineSeparator());    // 将受到的信息显示出来，换行
                }
                else{
                    //System.out.println("private");
                    privateChatOutputArea.append(msg + System.lineSeparator());    // 将受到的信息显示出来，换行
                }
            }
        }
    }
}
