package spider.download;

import java.io.*;
import java.sql.*;
import java.util.Date;

/**
 * 数据库中的url取出，并写入txt文件中
 */
public class dataWriteTxt {
    public static void main(String[] args) {

        // 定义数据库驱动
        String driver = "com.mysql.cj.jdbc.Driver";
        // 数据库连接URL
        String url = "jdbc:mysql://127.0.0.1:3306/crawler_data?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
        Connection conn = null;
        String downloadUrl;
        try {
            Class.forName(driver);
            // 创建数据库连接
            conn = DriverManager.getConnection(url, "root", "admin");
            // 创建预编译SQL对象
            PreparedStatement ps = conn
                    .prepareStatement("select downloadUrl from original_standard_bzko");
            // 执行SQL,获取结果集rs
            ResultSet rs = ps.executeQuery();
            // 处理结果集
            while (rs.next()) {
                downloadUrl = rs.getString("downloadUrl");
                downLoad(downloadUrl);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("加载数据库失败");
            System.exit(1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("数据库连接错误");
            System.exit(1);
        }
    }


//  将数据写入txt文件
    private static void downLoad(String url) {
        try {
            DataOutputStream os = new DataOutputStream(
                    new FileOutputStream("C:\\Users\\cyan_\\Downloads\\URL\\biaozhun.txt", true));
            os.writeBytes(url + "\r\n");
            System.out.println(new Date()+"数据写入成功");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}