package main.java.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SQLiteJDBC implements sqlite {

    String url = "db/sql.sqlite";
    // 加载SQLite JDBC驱动
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 关闭数据库连接
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 示例查询方法
    public void executeQuery() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);

            // 查询数据
            statement = connection.createStatement();
            String sql = "SELECT * FROM user";
            resultSet = statement.executeQuery(sql);

            // 处理查询结果
            while (resultSet.next()) {
                String name = resultSet.getString("userName");
                System.out.println("Name: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
    }

    @Override
    public int insertUser(String userName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "insert into user (userName) values ('"+ userName +"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            int affectedRows = pstmt.executeUpdate();
            // 处理查询结果
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public boolean queryUserIsExist(String userName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);

            // 查询数据
            String sql = "SELECT * FROM user WHERE userName = '"+ userName +"'";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            while(rs.next())
                return Objects.equals(rs.getString("userName"), userName);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return false;
    }

    @Override
    public List<List<String>> getHistoryChat(String getUserName,String sendUserName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "SELECT * FROM history WHERE getUserName = '"+ getUserName +"' and sendUserName = '"+ sendUserName+ "' ORDER BY msg_id DESC";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            List<List<String>> ans = new ArrayList<>();
            int i=0;
            while(rs.next()&&i<=10){//最多返回前十条历史消息
                ++i;
                List<String> msg = new ArrayList<>();
                msg.add(rs.getString("time"));
                msg.add(rs.getString("sendUserName"));
                msg.add(rs.getString("message"));
                ans.add(msg);
            }
/*
            sql = "delete from history where getUserName ="+ userName ;
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
*/
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return null;
    }

    @Override
    public void deleteFriend(String userName1, String userName2) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "delete from friend where user1 ='"+userName1+"' and user2 = '"+userName2+"'";
            pstmt = connection.prepareStatement(sql);
            // 执行操作
            pstmt.executeUpdate();
            sql = "delete from friend where user1 ='"+userName2+"' and user2 = '"+userName1+"'";
            pstmt = connection.prepareStatement(sql);
            // 执行操作
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public void addFriend(String userName1, String userName2) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "insert into friend (user1, user2) values ('"+ userName1 +"','"+userName2+"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            pstmt.executeUpdate();
            sql = "insert into friend (user1, user2) values ('"+ userName2 +"','"+userName1+"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public List<String> queryFriend(String userName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);

            // 查询数据
            String sql = "SELECT * FROM friend WHERE user1 = '"+ userName +"'";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            List<String> ans = new ArrayList<>();
            while(rs.next()){
                ans.add(rs.getString("user2"));
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return null;
    }

    @Override
    public int queryFriend(String userName1, String userName2) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);

            // 查询数据
            String sql = "SELECT * FROM friend WHERE user1 = '"+ userName1 +"' and user2 = '"+userName2+"'";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            if(rs.next()){
                return 1;
            }return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return 0;
    }

    @Override
    public List<List<String>> getWaitChat(String getUserName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "SELECT * FROM waitToSend WHERE getUserName = '"+ getUserName +"'";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            List<List<String>> ans = new ArrayList<>();
            while(rs.next()){
                List<String> msg = new ArrayList<>();
                msg.add(rs.getString("time"));
                msg.add(rs.getString("sendUserName"));
                msg.add(rs.getString("message"));
                ans.add(msg);
            }
            sql = "delete FROM waitToSend WHERE getUserName = '"+ getUserName +"'";
            pstmt = connection.prepareStatement(sql);
            // 删除
            pstmt.executeUpdate();
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return null;
    }

    @Override
    public int insertHistoryMessage(String sendUserName,String getUserName,String message,String time) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "insert into history (sendUserName, getUserName, message, time) values ('"+ sendUserName +"','"+ getUserName +"','"+message+"','"+time+"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            // 处理查询结果
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public int insertWaitToSendMessage(String sendUserName,String getUserName,String message,String time) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "insert into waitToSend (sendUserName, getUserName, message, time) values ('"+ sendUserName +"','"+ getUserName +"','"+message+"','"+time+"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            // 处理查询结果
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public void deleteFriendApplication(String userName1, String userName2) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "delete from friendApplication where getUserName = '"+userName1+"' and sendUserName = '"+userName2+"'";
            pstmt = connection.prepareStatement(sql);
            // 执行操作
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public List<List<String>> getFriendApplication(String getUserName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "SELECT * FROM friendApplication WHERE getUserName = '"+ getUserName +"'";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            List<List<String>> ans = new ArrayList<>();
            while(rs.next()){
                List<String> msg = new ArrayList<>();
                msg.add(rs.getString("time"));
                msg.add(rs.getString("sendUserName"));
                ans.add(msg);
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return null;
    }

    @Override
    public int insertFriendApplication(String sendUserName, String getUserName, String time) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "insert into friendApplication (sendUserName, getUserName, time) values ('"+ sendUserName +"','"+ getUserName + "','" +time+"')";
            pstmt = connection.prepareStatement(sql);
            // 执行插入操作
            // 处理查询结果
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public String getFriendApplicationOne(String getUserName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 创建连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+url);
            // 查询数据
            String sql = "SELECT * FROM friendApplication WHERE getUserName = '"+ getUserName +"' order by applicationId limit 1";
            pstmt = connection.prepareStatement(sql);
            // 执行查询
            rs = pstmt.executeQuery();
            String ans=null;
            while(rs.next()){
                ans = rs.getString("sendUserName");
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, pstmt, rs);
        }
        return null;
    }
}
