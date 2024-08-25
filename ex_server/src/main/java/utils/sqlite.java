package main.java.utils;

import java.util.List;

public interface sqlite {
    public int insertUser(String userName);
    public boolean queryUserIsExist(String userName);
    public List<List<String>> getHistoryChat(String getUserName,String sendUserName);//返回: 时间, 用户名, 消息
    public void deleteFriend(String userName1,String userName2);
    public void addFriend(String userName1,String userName2);
    public List<String> queryFriend(String userName);
    public int queryFriend(String userName1,String userName2);
    public List<List<String>> getWaitChat(String getUserName);//返回: 时间, 用户名, 消息
    public int insertHistoryMessage(String sendUserName,String getUserName,String message,String time);
    public int insertWaitToSendMessage(String sendUserName,String getUserName,String message,String time);
    void deleteFriendApplication(String userName1, String userName2);
    List<List<String>> getFriendApplication(String getUserName);
    int insertFriendApplication(String sendUserName, String getUserName, String time);
    String getFriendApplicationOne(String getUserName);
}
