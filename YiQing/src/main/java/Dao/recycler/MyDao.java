package Dao.recycler;
import Dao.DaoUtils.ExeRes;
import Dao.DaoUtils.TestDataUtil;
import Dao.MainDBDao;
import Dao.YiqingDTO.TestDTO;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.data.sql.SqlCondition;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MyDao {
    @Autowired
    private TestDao testdao;
    public static void main(String[] args){
        MainDBDao dao = new MainDBDao();
        String project,jsonstring;
        ExeRes exeRes;

        project = "product";
        jsonstring = "{\n" +"\"source_id\":\"123132\",\n"+"\"category_id\":\"00000000\",\n"+
                "\t\"specs\": \"17.5*9.5cm\",\n" +
                "\t\"image\": [\n" +
                "\t\t\"https://image.cn.made-in-china.com/prod/000-OakESsNGEogK.jpg\",\n" +
                "\t\t\"https://image.cn.made-in-china.com/prod/000-rQkTspFlHbuy.jpg\",\n" +
                "\t\t\"https://image.cn.made-in-china.com/prod/000-cToQjlyKfbrn.jpg\"\n" +
                "\t],\n" +
                "\t\"company_id\": \"4eca83d1ec19d7c\",\n" +
                "\t\"material\": \"活性炭\",\n" +
                "\t\"price\": \"89.00\",\n" +
                "\t\"classify_name\": \"口罩\",\n" +
                "\t\"charge_unit\": \"（元/件）\",\n" +
                "\t\"description\": \"“一次性无纺布口罩 口罩厂家”详细介绍 品 名：一次性无纺布口罩（外贸产品） 型 号：平面口罩 材 质：一次性无纺布 颜 色：多色 包 装：2000只/箱 2500只/箱 功 能：防尘、防尾气、防异味、阻挡颗粒 注意事项：一次性无纺布口罩不产生氧气，不可用于缺氧环境，每天佩戴时间不要超过3小时。本品为一次性口罩，请勿重复使用。 使用范围：电子制造业、 、食品加工、学校、骑机车、手、美容、 工厂、公共场合等多种用途 产品结构说明： 1、一次性无纺布口罩：采用进口技术，过滤效率高，呼吸阻力小。 2、可调节鼻夹条：确保口罩与面部贴合。 3、升级版松紧耳带：高弹力松紧耳带，弹性好，适合所有头型佩戴\",\n" +
                "\t\"tel\": \"15587888080\",\n" +
                "\t\"inventory\": \"99999件\",\n" +
                "\t\"product_name\": \"一次性无纺布口罩 口罩厂家\",\n" +
                "\t\"contacts\": \"甘雨\"\n" +
                "}";
        JSONObject jo = JSONObject.parseObject(jsonstring),jo1;
        jo1 = (JSONObject)JSONObject.toJSON(new ExeRes());
//        System.out.println(jo);
//        exeRes = dao.addRecord(project,jo);
//        exeRes = dao.do_insert(project,jo);
        exeRes = dao.do_select(project,jo,null);
        System.out.println("测试");
    }

    public void store1(){
        //        String project = "company";
//        JSONObject jo = TestDataUtil.getProjectJsonObject(project);
//        MainDBDao maindao = new MainDBDao();
////        ExeRes exeRes = tdbJDBC.addRecord(jo,project);
//        ExeRes exeRes;
//        exeRes = maindao.addRecord(project,jo);
////        exeRes = tdbJDBC.do_select(null,project,null);
////        jo = new JSONObject();
////        jo.put("name","孙睿智");
//        List<SqlCondition> condition_list = new ArrayList<>();
//        SqlCondition sc = new SqlCondition("name","really",SqlCondition.EQ);
//        condition_list.add(sc);
////        exeRes = tdbJDBC.do_update(jo,project,condition_list);
////        exeRes = tdbJDBC.do_delete(project,condition_list);
//
//        System.out.println(exeRes);

        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";

        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://106.74.152.45:19362/testcksdb";

        // MySQL配置时的用户名
        String user = "mysql";

        // MySQL配置时的密码
        String password = "U2qafVuvDH";

        try {
            // 加载驱动程序
            Class.forName(driver);

            // 连续数据库
            Connection conn = DriverManager.getConnection(url, user, password);
//            DriverManager.getConnection()
            Properties prop = new Properties();
            prop.setProperty(",","");
            if(!conn.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
            }

            // statement用来执行SQL语句
            Statement statement = conn.createStatement();

            // 要执行的SQL语句
            String sql = "select count(*) as num from ai_company_topics_tmp";

            // 结果集
            ResultSet rs = statement.executeQuery(sql);

            System.out.println("-----------------");
            System.out.println("执行结果如下所示:");
            System.out.println("-----------------");
            System.out.println(" 学号" + "\t" + " 姓名");
            System.out.println("-----------------");

            String name = null;

            while(rs.next()) {

                // 选择sname这列数据
                name = rs.getString("num");

                // 首先使用ISO-8859-1字符集将name解码为字节序列并将结果存储新的字节数组中。
                // 然后使用GB2312字符集解码指定的字节数组
//                name = new String(name.getBytes("ISO-8859-1"),"GB2312");
//
//                // 输出结果
//                System.out.println(rs.getString("sno") + "\t" + name);
                System.out.println(name);
            }

            rs.close();
            conn.close();
            TestDao testdao = new TestDao();
            TestDTO res = testdao.get(null);
            System.out.println(testdao.get(null));

        } catch(ClassNotFoundException e) {


            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();


        } catch(SQLException e) {


            e.printStackTrace();


        } catch(Exception e) {


            e.printStackTrace();


        }
    }
}