package main.java.window;

import javax.swing.*;
import java.net.*;

import static java.lang.Thread.sleep;

public class Window extends JFrame {

    public JLabel portLabel = new JLabel("端口");
    public JTextField portInput = new JTextField();;
    public Box portBox = Box.createHorizontalBox();;
    public JButton portButton = new JButton("启动");;
    public Box outputBox = Box.createVerticalBox();
    public JTextArea output = new JTextArea();
    public JLabel outputLabel = new JLabel("服务器信息");

    public Window(){
        //设置输出框滑动
        JScrollPane scroll=new JScrollPane(output);
        scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        output.setEditable(false);

        setLayout(null);
        setBounds(50,50,700,550);//该窗口本身的位置和大小

        portBox.add(portLabel);
        portBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        portBox.add(portInput);
        portBox.add(Box.createHorizontalStrut(10));//输入框和label的横向间距
        portBox.add(portButton);
        outputBox.add(outputLabel);
        outputBox.add(Box.createVerticalStrut(10));//输入框和label的横向间距
        outputBox.add(scroll);
        add(portBox);
        add(outputBox);
        //设置这两个大Box的位置和大小
        portBox.setBounds(20,30,500,60);
        outputBox.setBounds(20,140,500,300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }
    public void run(){
        setVisible(true);
    }

}




