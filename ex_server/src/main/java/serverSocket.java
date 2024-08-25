package main.java;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import main.java.utils.SQLiteJDBC;
import main.java.utils.sqlite;

public class serverSocket implements Runnable {
    private SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
    private static Map<String,Socket> map = new ConcurrentHashMap<String, Socket>();
    private Socket socket;    // 对应处理的socket
    private JTextArea output;
    private String userName;
    private PrintStream printStream;
    private PrintStream printStream2;
    SimpleDateFormat strFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");

    serverSocket(Socket serverSocket, JTextArea output){
        socket = serverSocket;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());    // 获取客户端的输入流
            String msg = null;    // 接收用户的信息
            while(true){
                if(scanner.hasNextLine()){
                    msg = scanner.nextLine();    // 读取客户端传来的数据信息
                    // 用户登录
                    if(msg.startsWith("Login:")){
                        // 将用户名保存在userName中
                        String userName = msg.split("\\:")[1];    // 获取用户名
                        // 注册该用户
                        userRegist(userName,socket);
                        if(!sqLiteJDBC.queryUserIsExist(userName)){
                            sqLiteJDBC.insertUser(userName);
                        }
                        //上线通知好友
                        List<String> friends = sqLiteJDBC.queryFriend(userName);
                        for(String i : friends){
                            if(map.containsKey(i)){//在线才通知
                                Socket client = map.get(i);    // 取得私聊用户名对应的客户端
                                printStream = new PrintStream(client.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                                printStream.println(strFormat.format(new Date())+"\n");
                                printStream.println(userName + "上线啦!!!!!\n");
                                printStream.println("---------------------------------\n");
                                printStream.flush();
                            }
                        }
                        //获取未读消息
                        List<List<String>> waitToGet = sqLiteJDBC.getWaitChat(userName);
                        printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                        printStream.println("未读消息:\n");
                        for(List<String> i : waitToGet){
                            printStream.println("----------------------------\n");
                            printStream.printf("时间:%s\n%n", i.get(0));
                            printStream.println("发信人:"+i.get(1)+"\n");
                            printStream.println("消息:"+i.get(2)+"\n");
                            printStream.println("----------------------------\n");
                        }printStream.println("----------------------------\n");
                        printStream.flush();
                    }
                    else if(msg.startsWith("@") && msg.contains("-")){ // 用户选择私聊, 私聊的格式为:@userName-私聊信息
                        String userName = msg.split("@")[1].split("-")[0];
                        // 保存私聊的信息
                        String str = msg.split("@")[1].split("-")[1];
                        // 发送私聊信息
                        privateChat(userName,str);
                    }
                    else if (msg.startsWith("#history-")) { //用户查询与某用户的历史消息,格式为:#history-userName
                        String sendUserName = msg.split("#history-")[1];
                        // 发送历史信息
                        historyChat(socket,sendUserName);
                    }
                    else if (msg.startsWith("#delete-")) { //用户删除某好友,格式为:#delete-userName
                        String deleteUserName = msg.split("#delete-")[1];
                        //System.out.println(deleteUserName);
                        sqLiteJDBC.deleteFriend(userName,deleteUserName);
                        printStream = new PrintStream(socket.getOutputStream());    // 获取客户端的输出流,将信息发送到指定客户端
                        printStream.println(strFormat.format(new Date())+"\n");
                        printStream.println("删除好友"+deleteUserName+"成功!yeah!\n");
                        printStream.println("---------------------------------\n");
                        printStream.flush();
                        output.append(userName+"删除了"+deleteUserName+"的好友\n");
                    }
                    else if (msg.startsWith("#addFriend-")) { //用户添加某好友,格式为:#addFriend-userName
                        String addUserName = msg.split("#addFriend-")[1];
                        printStream = new PrintStream(socket.getOutputStream());    // 获取客户端的输出流,将信息发送到指定客户端
                        sqLiteJDBC.insertFriendApplication(userName,addUserName,strFormat.format(new Date()));
                        if(map.containsKey(addUserName)){
                            Socket client = map.get(addUserName);    // 取得私聊用户名对应的客户端
                            printStream2 = new PrintStream(client.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                            printStream2.println(strFormat.format(new Date())+"\n");
                            printStream2.println("#系统消息:\n");
                            printStream2.println(userName+"请求添加你为好友!");
                            printStream.println("---------------------------------\n");
                            printStream2.flush();
                        }else{
                            sqLiteJDBC.insertWaitToSendMessage("#系统消息:",addUserName,userName+"请求添加你为好友!",strFormat.format(new Date()));
                        }
                        printStream.println(strFormat.format(new Date())+"\n");
                        printStream.println("发送好友请求: "+addUserName+" 成功!yeah!\n");
                        printStream.println("---------------------------------\n");
                        printStream.flush();
                        output.append(userName+"请求成为"+addUserName+"的好友\n");
                    }
                    else if (msg.equals("#queryFriendApplication")){
                        List<List<String>> friends = sqLiteJDBC.getFriendApplication(userName);
                        printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                        printStream.println(strFormat.format(new Date())+"\n");
                        printStream.println("#系统消息:\n");
                        printStream.println("好友请求:\n");
                        for(List<String> i : friends){
                            printStream.println("时间:"+i.get(0)+"\n");
                            printStream.println("请求人:"+i.get(1)+"\n");
                        }
                        printStream.println("---------------------------------\n");
                        printStream.flush();
                    }
                    else if (msg.equals("#passFirstFriendApplication")){
                        String sendUserName = sqLiteJDBC.getFriendApplicationOne(userName);
                        if(sendUserName == null){
                            printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                            printStream.println(strFormat.format(new Date())+"\n");
                            printStream.println("#系统消息:\n");
                            printStream.println("很可惜...没有人请求添加你为好友哦^_^\n");
                            printStream.println("---------------------------------\n");
                        }else{
                            sqLiteJDBC.deleteFriendApplication(userName,sendUserName);
                            if(sqLiteJDBC.queryFriend(sendUserName,userName)==0)
                                sqLiteJDBC.addFriend(userName,sendUserName);
                            printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                            printStream.println(strFormat.format(new Date())+"\n");
                            printStream.println("#系统消息:\n");
                            printStream.println("已添加"+sendUserName+"为好友!"+"\n");
                            printStream.println("---------------------------------\n");
                        }
                    }
                    else if(msg.equals("#refuseFirstFriendApplication")){
                        String sendUserName = sqLiteJDBC.getFriendApplicationOne(userName);
                        if(sendUserName == null){
                            printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                            printStream.println(strFormat.format(new Date())+"\n");
                            printStream.println("#系统消息:\n");
                            printStream.println("很可惜...没有人请求添加你为好友哦^_^\n");
                            printStream.println("---------------------------------\n");
                        }else{
                            sqLiteJDBC.deleteFriendApplication(userName,sendUserName);
                            printStream = new PrintStream(socket.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                            printStream.println(strFormat.format(new Date())+"\n");
                            printStream.println("#系统消息:\n");
                            printStream.println("已拒绝"+sendUserName+"的好友申请!"+"\n");
                            printStream.println("---------------------------------\n");
                            privateChat(sendUserName,"我拒绝了你的好友申请...你值得更好的");
                        }
                    }
                    else if (msg.equals("#queryOnlineFriend")) { //用户查询在线好友
                        List<String> friends = sqLiteJDBC.queryFriend(userName);
                        printStream = new PrintStream(socket.getOutputStream());    // 获取客户端的输出流,将信息发送到指定客户端
                        printStream.println(strFormat.format(new Date())+"\n");
                        printStream.println("#系统消息:"+"\n");
                        printStream.println("在线好友:\n");
                        printStream.flush();
                        for(String i : friends){
                            if(map.containsKey(i)){
                                printStream.println(i);
                            }
                        }
                        printStream.println("---------------------------------\n");
                        printStream.flush();
                    }
                    else if (msg.equals("#queryDeadFriend")) { //用户查询离线好友
                        List<String> friends = sqLiteJDBC.queryFriend(userName);
                        printStream = new PrintStream(socket.getOutputStream());    // 获取客户端的输出流,将信息发送到指定客户端
                        printStream.println(strFormat.format(new Date())+"\n");
                        printStream.println("#系统消息:"+"\n");
                        printStream.println("离线好友:"+"\n");
                        printStream.flush();
                        for(String i : friends){
                            if(!map.containsKey(i)){
                                printStream.println(i);
                            }
                        }
                        printStream.println("---------------------------------\n");
                        printStream.flush();
                    }
                    else if(msg.equals("#exit")){   // 用户退出聊天, 用户退出格式为:#exit
                        userExit(socket);
                        break;
                    }
                    else if(msg.startsWith("#@nobody-")){// 群聊信息
                        String msg1 = msg.split("#@nobody-")[1];
                        groupChat(socket, msg1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册用户信息
     * @param userName 用户名
     * @param socket 用户客户端Socket对象
     */
    private void userRegist(String userName, Socket socket){
        map.put(userName, socket);
        this.userName=userName;
        output.append("[用户: " + userName + "] 上线了，ta的[客户端为: " + socket + "]!\n");
        output.append("当前在线人数为:" + map.size() + "人\n");
    }

    /**
     * 群聊流程(将Map集合转换为Set集合,从而取得每个客户端Socket,将群聊信息发送给每个客户端)
     * @param socket 发出群聊的客户端
     * @param msg 群聊信息
     */
    private void groupChat(Socket socket,String msg) throws IOException, ParseException {
        String userName = this.userName;    // 遍历Set集合找到发起群聊信息的用户
        Set<Map.Entry<String,Socket>> set = map.entrySet();    // 将Map集合转换为Set集合
        output.append(userName + "在群聊说:" + msg +"\n");    // 在服务器上显示，用于调试
        // 遍历Set集合将群聊信息发给每一个客户端(除了自己以外)
        for(Map.Entry<String,Socket> entry : set){
            //取得客户端的Socket对象
            if (!entry.getValue().equals(socket)) {
                Socket client = entry.getValue();
                printStream = new PrintStream(client.getOutputStream());    //取得client客户端的输出流
                printStream.println("public:");
                printStream.println(strFormat.format(new Date())+"\n");
                printStream.println("public:");
                printStream.println(userName + "在群聊说:" + msg +"\n");
                printStream.println("public:");
                printStream.println("---------------------------------\n");
            }
        }
    }
    /**
     * 私聊流程(利用userName取得客户端的Socket对象,从而取得对应输出流,将私聊信息发送到指定客户端)
     * @param userName 私聊的用户名
     * @param msg 私聊的信息
     */
    private void privateChat(String userName, String msg) throws IOException, ParseException {
        String curUser = this.userName;
        Date date = new Date();
        if(map.containsKey(userName)){
            Socket client = map.get(userName);    // 取得私聊用户名对应的客户端
            printStream = new PrintStream(client.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
            printStream.println(strFormat.format(new Date())+"\n");
            printStream.println(curUser + "悄悄对你说:" + msg +"\n");
            printStream.println("---------------------------------\n");
            output.append(date.toString());
            output.append(curUser + "私聊" + userName + "说:" + msg +"\n");    // 服务器端显示，用于调试
            sqLiteJDBC.insertHistoryMessage(curUser,userName,msg, strFormat.format(new Date()));
        }else{//用户离线,发送离线消息
            output.append(curUser + "向" + userName + "发送离线消息:" + msg +"\n");    // 服务器端显示，用于调试
            sqLiteJDBC.insertWaitToSendMessage(curUser,userName,msg, strFormat.format(new Date()));
        }
    }

    /**
     * 查询客户端跟userName的历史消息,最多十条
     * @param socket 当前客户端
     * @param userName 私聊人
     */
    private void historyChat(Socket socket,String userName) throws IOException {
        String curUser = this.userName;
        List<List<String>> historyMsg = sqLiteJDBC.getHistoryChat(curUser,userName);
        printStream = new PrintStream(socket.getOutputStream());    // 获取客户端的输出流,将信息发送到指定客户端
        for (List<String> i : historyMsg){
            System.out.println(i.get(0));
            System.out.println(i.get(1));
            System.out.println(i.get(2));
            printStream.println(i.get(0)+"\n"+i.get(1) + " 对你说: " + i.get(2) +"\n");
            printStream.println("---------------------------------\n");
        }
        output.append(curUser + "查询了ta跟"+userName+"的最近十条历史消息\n");    // 服务器端显示，用于调试
    }

    /**
     * 用户退出
     * @param socket
     */
    private void userExit(Socket socket) throws IOException, ParseException {
        socket.close();
        map.remove(userName,socket);    // 将userName,Socket元素从map集合中删除

        //下线通知好友
        List<String> friends = sqLiteJDBC.queryFriend(userName);
        for(String i : friends){
            if(map.containsKey(i)){//在线才通知
                Socket client = map.get(i);    // 取得私聊用户名对应的客户端
                printStream = new PrintStream(client.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
                printStream.println(strFormat.format(new Date())+"\n");
                printStream.println(userName + "下线啦!!!!!\n");
                printStream.println("---------------------------------\n");
                printStream.flush();
            }
        }

        // 提醒服务器该客户端已下线
        output.append("用户:"+ userName +"已下线!\n");
        output.append("当前在线人数为:" + map.size() + "人\n");
    }
}