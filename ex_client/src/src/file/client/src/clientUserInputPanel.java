package src.file.client.src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Thread.sleep;

public class clientUserInputPanel extends JFrame {
    Socket client;
    InputStream input;
    OutputStream output;
    private Box userInputBox;//用户输入部分的盒子，主要包含了用户名和密码,垂直排列
    private Box portNameBox;//用户名盒子，包含两个部分，一个label一个TextFiled,水平排列
    private Box serverNameBox;//密码盒子，包含两个部分，一个label一个passwordField,水平排列
    private Box buttonBox;//按钮盒子，包含登录和忘记密码两个按钮,水平排列
    private JLabel portName;//
    private JLabel serverName;//两个label组件提示用户
    private JTextField portNameInput;//用户名输入框
    private JTextField serverNameInput;
    private JButton runServer;//登录按钮
    private JButton closeClientSocket;//忘记密码按钮
    private JButton closeServer;//忘记密码按钮
    private Box serverText;//密码盒子，包含两个部分，一个label一个passwordField,水平排列
    private JLabel serverInputLabel;//
    private JTextField serverInput;//用户名输入框
    private JButton serverInputSendFile;//忘记密码按钮
    private JButton serverInputSend;//忘记密码按钮
    private Box clientText;//密码盒子，包含两个部分，一个label一个passwordField,水平排列
    private JLabel clientLabel;//
    private JTextField clientOutput;//用户名输入框
    Thread reading;

    public clientUserInputPanel() {
        //实例化所有的对象
        portName =new JLabel("端口名");
        serverName =new JLabel("IP地址");
        portNameInput =new JTextField();
        portNameInput.setText("9528");
        serverNameInput =new JTextField("127.0.0.1");
        runServer =new JButton("启动客户端");
        closeClientSocket =new JButton("断开连接");
        closeServer = new JButton("关闭客户端");
        serverInputLabel = new JLabel("输入内容");
        serverInput = new JTextField();
        serverInputSendFile = new JButton("发送文件");
        clientLabel = new JLabel("收到内容");
        clientOutput = new JTextField();
        serverInputSend = new JButton("发送信息");
        clientOutput.setEditable(false);
        serverInput.setEditable(false);
        closeClientSocket.setEnabled(false);//变灰
        closeServer.setEnabled(false);//变灰
        serverInputSendFile.setEnabled(false);//变灰
        serverInputSend.setEnabled(false);//变灰
        userInputBox=Box.createVerticalBox();//内部组件，垂直排列
        buttonBox=Box.createHorizontalBox();//内部组件，水平排列
        portNameBox =Box.createHorizontalBox();//内部组件，水平排列
        serverNameBox =Box.createHorizontalBox();//内部组件，水平排列
        serverText = Box.createHorizontalBox();
        clientText = Box.createHorizontalBox();
        //将用户名相关内容加入用户名盒子
        portNameBox.add(portName);
        portNameBox.add(Box.createHorizontalStrut(10));//输入框与label的横向间隔
        portNameBox.add(portNameInput);
        //将密码相关内容加入密码盒子
        serverNameBox.add(serverName);
        serverNameBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        serverNameBox.add(serverNameInput);
        //将用户名盒子和密码盒子加入用户输入userInputBox盒子
        userInputBox.add(portNameBox);
        userInputBox.add(Box.createVerticalStrut(10));//两行之间的间距
        userInputBox.add(serverNameBox);
        //将两个按钮加入到按钮盒子
        buttonBox.add(Box.createHorizontalStrut(20));//第一个按钮和左边界的距离
        buttonBox.add(runServer);
        buttonBox.add(Box.createHorizontalStrut(10));//两个按钮之间的间距
        buttonBox.add(closeClientSocket);
        buttonBox.add(Box.createHorizontalStrut(10));//两个按钮之间的间距
        buttonBox.add(closeServer);
        //
        serverText.add(Box.createHorizontalStrut(20));//第一个按钮和左边界的距离
        serverText.add(serverInputLabel);
        serverText.add(Box.createHorizontalStrut(10));
        serverText.add(serverInput);
        serverText.add(Box.createHorizontalStrut(10));
        serverText.add(serverInputSendFile);
        serverText.add(Box.createHorizontalStrut(10));
        serverText.add(serverInputSend);
        //
        clientText.add(Box.createHorizontalStrut(20));//第一个按钮和左边界的距离
        clientText.add(clientLabel);
        clientText.add(Box.createHorizontalStrut(10));
        clientText.add(clientOutput);
        //这个面板为空布局
        //设置这两个大盒子userInputBox和buttonBox的位置
        setLayout(null);
        setBounds(50,50,700,550);//该窗口本身的位置和大小
        //加入这两个大Box
        add(userInputBox);
        add(buttonBox);
        add(serverText);
        add(clientText);
        //设置这两个大Box的位置和大小
        userInputBox.setBounds(20,20,400,100);
        buttonBox.setBounds(20,120,400,100);
        serverText.setBounds(20,220,500,100);
        clientText.setBounds(20,350,500,100);
        //setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("客户端");
        validate();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                //加入动作
                if(closeClientSocket.isEnabled()){closeClientSocket.doClick();}
            }
        });

        runServer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent a){
                try {
                    runServer.setEnabled(false);
                    clientOutput.setText("等待连接中...");
                    try {
                        client = new Socket(serverNameInput.getText(), Integer.parseInt(portNameInput.getText()));
                        if (client.isConnected()) {
                            clientOutput.setText("成功连接!!");
                        }
                    } catch (IOException e) {
                        // 连接失败
                        clientOutput.setText("连接失败");
                        runServer.setEnabled(true);
                        return;
                    }
                    input = client.getInputStream();
                    output = client.getOutputStream();
                    runServer.setEnabled(false);portNameInput.setEditable(false);serverNameInput.setEditable(false);
                    closeServer.setEnabled(true);closeClientSocket.setEnabled(true);
                    serverInput.setEditable(true);
                    serverInputSendFile.setEnabled(true);
                    serverInputSend.setEnabled(true);
                    reading = new Thread(()->{
                        BufferedOutputStream bosFile = null;    // 与输出文件流相关联
                        while(true){
                           byte[] bytes = new byte[1026];
                           // 读一下服务端发来的东西
                           InputStream inputStream = null;
                           int read = 0;
                           try {
                               inputStream = client.getInputStream();
                               read = inputStream.read(bytes);
                           } catch (SocketException e){
                               closeClientSocket.doClick();
                               clientOutput.setText("关闭连接!");
                               return;
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
                           if (bytes[1] == 1) // 说明传的是文件
                           {
                               //String filePath = String.format("./directoryTest/src/用户%d传送来的IO流的框架图.png", sendUser);
                               try {
                                   String currentDirectory = System.getProperty("user.dir");
                                   String FilePath = currentDirectory + File.separator + "\\客户端收到的文件\\file.zip";
                                   bosFile = new BufferedOutputStream(new FileOutputStream(FilePath, true));
                                   //System.out.println(bosFile.toString());
                                   bosFile.write(bytes, 2, read-2);
                                   bosFile.flush();
                               } catch (IOException e) {
                                   throw new RuntimeException(e);
                               }
                               if (read<1026)   // 说明是最后一次在传送文件，所以传送的字节数才会小于字节数组by的大小
                               {
                                   clientOutput.setText("服务器发送的文件传输完成\n");
                               }
                           }else if (bytes[1] == 2){
                               try{
                                   String str = new String(bytes, 2, read-2, Charset.defaultCharset());
                                   System.out.println(str.length());
                                   System.out.println("服务器已关闭连接".length());
                                   clientOutput.setText("服务器：\n" + str);
                               }catch (Exception e){
                                   return;
                               }
                           }else if (bytes[1] == -1){
                               closeClientSocket.doClick();
                               clientOutput.setText("客户端关闭连接!");
                               return;
                           }
                       }
                    });reading.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        closeClientSocket.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent a){
                try {
                    sendCloseMessage();
                    client.close();
                    input.close();output.close();
                    clientOutput.setEditable(false);
                    serverInput.setEditable(false);
                    closeClientSocket.setEnabled(false);//变灰
                    closeServer.setEnabled(false);//变灰
                    serverInputSendFile.setEnabled(false);//变灰
                    serverInputSend.setEnabled(false);//变灰
                    portNameInput.setEditable(true);
                    serverNameInput.setEditable(true);
                    runServer.setEnabled(true);
                    closeClientSocket.setEnabled(false);
                    clientOutput.setText("服务端关闭连接!");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        serverInputSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取输入框的文本内容
                JFileChooser fileChooser = new JFileChooser();
                File file = null;
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int option = fileChooser.showOpenDialog(createWindow());
                if (option == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    System.out.println(file);
                    //serverInput.setText(file.toString());
                } else {
                    serverInput.setText("已取消，请重新选择");
                    return;
                }
                try {
                    // 使用TcpSocketServer的sendMessage方法发送消息
                    sendFile(file.toString());
                    // 可以在这里更新UI，表示消息已发送
                    //serverInputSend.setEnabled(false);
                    //serverInputSendFile.setEnabled(false);
                    //serverInput.setEditable(false);
                    clientOutput.setText("文件发送成功！文件:\n"+file.toString());
                } catch (IOException ex) {
                    // 处理可能发生的IOException
                    clientOutput.setText("发送文件失败！");
                    ex.printStackTrace();
                }
            }
        });
        serverInputSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取输入框的文本内容
                String str = serverInput.getText();
                try {
                    // 使用TcpSocketServer的sendMessage方法发送消息
                    sendMessage(str);
                    // 可以在这里更新UI，表示消息已发送
                    clientOutput.setText("信息发送成功！信息:\n"+str);
                    serverInput.setText("");
                    //serverInputSend.setEnabled(false);
                    //serverInputSendFile.setEnabled(false);
                    //serverInput.setEditable(false);
                } catch (IOException ex) {
                    // 处理可能发生的IOException
                    clientOutput.setText("信息文件失败！");
                    ex.printStackTrace();
                }
            }
        });
        closeServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeClientSocket.doClick();
            }
        });
    }
    private JFrame createWindow() {
        JFrame frame = new JFrame("Swing选择文件或目录");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
        return frame;
    }
    public void sendFile(String fileName) throws IOException {
        if (output != null) {
            // 创建压缩文件
            // 获取当前工作目录的路径
            String currentDirectory = System.getProperty("user.dir");
            // 构造压缩文件的路径，这里假设压缩文件名为 "file.zip"
            String zipFilePath = currentDirectory + File.separator + "\\客户端发送的文件\\file.zip";
            File zipFile = new File(zipFilePath);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            // 要压缩的文件
            File fileToZip = new File(fileName);
            // 添加文件到压缩包
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zos.putNextEntry(zipEntry);
            FileInputStream fis = new FileInputStream(fileToZip);
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            // 完成压缩
            zos.closeEntry();
            zos.close();
            // 发送压缩文件
            FileInputStream zis = new FileInputStream(zipFile);
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            byte[] bytes = new byte[1024];
            while ((length = zis.read(bytes)) != -1) {
                //System.out.println("i" + i + " res: " + res);
                byte[] newByteArr = reviseArr(bytes, length);;
                newByteArr[1] = 1;  // 表示第二个位置上的值为1时表示传输的是文件
                dos.write(newByteArr, 0, length+2);
                dos.flush();
            }
            //zipFile.delete();
            output.flush(); // 确保消息发送完毕
            //serverInput.setEditable(false);
        }
    }
    public byte[] reviseArr(byte[] by, int res) {
        byte[] newByteArr = new byte[by.length + 2];
        // 将by字节数组的内容都往后移动两位，即头部的两个位置空出来作为标志位
        for (int i = 0; i < by.length; i++)
        {
            newByteArr[i+2] = by[i];
        }
        return newByteArr;
    }
    public void sendMessage(String message) throws IOException {
        if (output != null) {
            byte[] newByteArr = new byte[1026];
            byte[] messageBytes = message.getBytes();
            newByteArr[1] = 2;// 表示第二个位置上的值为2时表示传输的是聊天内容
            System.arraycopy(messageBytes, 0, newByteArr, 2, messageBytes.length);
            output.write(newByteArr);  // 把内容发给服务器
            output.flush(); // 确保消息发送完毕
        }
    }
    public void sendCloseMessage() throws IOException {
        if (output != null) {
            byte[] newByteArr = new byte[1026];
            newByteArr[1] = -1;// 表示第二个位置上的值为2时表示传输的是聊天内容
            output.write(newByteArr);  // 把内容发给服务器
            output.flush(); // 确保消息发送完毕
        }
    }
    public void run(){
        setVisible(true);
    }

    public static void main(String[] args) throws SocketException {
        clientUserInputPanel clientUserInputPanel = new clientUserInputPanel();
        clientUserInputPanel.run();
    }
}