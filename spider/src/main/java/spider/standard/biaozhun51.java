package spider.standard;

import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;


/**
 *
 * http://www.biaozhun51.com/
 */
public class biaozhun51 {
    private final static Logger LOGGER = LoggerFactory.getLogger(biaozhun51.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;  // Map集合存储的是键值对
    private static String tableName = "original_standard";
    private static String mainWebsite;  // 一级分类的主页
    private static String downMainURL;  // 下载页主链接
    private static String zookeeper = "172.17.60.213:2181";
//    private static String zookeeper = "172.20.4.213:2181";
    private static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66";



    static {
        mainWebsite = "http://www.biaozhun51.com";
        downMainURL = "http://www.biaozhun51.com/e/DownSys/";
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {

        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        biaozhun51 biaozhun51 = new biaozhun51();
        biaozhun51.shouye(mainWebsite);

    }

    private void shouye(String mainWebsite) {
        try {

            String[] industryStandard = {"/bs", "/gbt", "/weld", "/others", "/TSG", "/industry", "/Enterprise-standard"};
            for (String e : industryStandard) {

                if(e == "/bs") {
                    standard_1(mainWebsite+e, "BS标准", "BS标准");
                }else if(e == "/gbt"){
                    standard_1(mainWebsite+e, "国内标准", "GB国家标准");
                }else if(e == "/weld"){
                    standard_1(mainWebsite+e, "焊接标准", "焊接标准");
                }else if(e == "/others"){
                    standard_1(mainWebsite+e, "其它标准", "其它标准");
                }else if(e == "/TSG"){
                    standard_1(mainWebsite+e, "特种设备安全技术规范", "特种设备安全技术规范");  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }else if(e == "/industry"){
                    standard_1(mainWebsite+e, "行业标准", "行业标准");
                }else if(e == "/Enterprise-standard"){
                    standard_1(mainWebsite+e, "企业标准", "企业标准");  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }
            }
//            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void standard_1(String mainWebsite_url_end, String category1, String category2) {
        // 该函数的作用是将每个二级分类中的详细标准名称、链接进行收集
        // 将这些信息传递给detail方法，进行每个标准的详细信息进行提取
        try {

            Document document = Jsoup.parse(new URL(mainWebsite_url_end).openStream(), "GBK", mainWebsite_url_end);
            if (null != document) {
                // 获取当前页面链接
                Elements standardList = document.select(".listbox h2 a");
                for (Element e : standardList) { // 例如：循环遍历国家标准、其他标准这些一级分类页中的标准名称
                    String detailUrl = e.attr("href");  // 获得各行业详细分类的地址，得到每个行业中的详细标准列表
                    String name = e.text().trim();  // 各行业中每个细节的标准名称，并去掉字符串首尾的空格
                    detail(mainWebsite+detailUrl, name, category1, category2);
                }
                // 翻页操作
                Elements pageList = document.select(".list_page a");  // ".pagecss a"表示class值为pagecss的元素下子元素为a标签
                int endPage;
                // 由于翻页页码是正序排序的，所以不知道最后一页的页码数字，所以先获取尾页的页码数字再进行下一页操作
                for (Element e : pageList) {
                    if ("尾页".equals(e.text().trim())) {
                        endPage = Integer.valueOf(e.attr("href").split("_")[1].split("\\.")[0]);
                        for (Element i : pageList) {
                            if ("下一页".equals(i.text().trim())) {
                                if (endPage >= Integer.valueOf(i.attr("href").split("_")[1].split("\\.")[0])) {
                                    standard_1(i.attr("href"), category1, category2);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void standard_2(String mainWebsite, String url_end, String category1){
        /*
        * 由于国家标准和行业标准的二级分类结构不同，所以对提取行业标准二级分类行为单独命名一个函数
        * */
        try{
            Document document = Jsoup.parse(new URL(mainWebsite+url_end).openStream(), "GBK", mainWebsite+url_end);
            if (null != document) {
                // 获取当前页面链接
                Elements standardList = document.select(".hot_box a");// 获取行业标准详细分类
                for (Element e : standardList) { // 例如：迭代遍历机械标准、化工标准这些二级分类页中的三级分类标准名称
                    String category2_Url = e.attr("href");  // 获得各行业详细分类的地址，得到每个行业中的详细标准列表
                    String category2 = e.text().trim();  // 各行业中每个细节的标准名称，并去掉字符串首尾的空格
                    standard_1(mainWebsite+category2_Url, category1, category2);  //将在网页中获取的行业分类category2传进去
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url, String name, String category1, String category2) {
        // 该函数用于提取特定标准的标准代号和该标准的下载链接，传递给download函数。
        try {
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
            if (null != document) {
                String codeName = name.trim().split("[\\u4e00-\\u9fa5]")[0];  //从名字中将标准编号截取出
                Elements a = document.select(".soft_downurl ul li a");
                String a_href = a.attr("href");
                String downloadUrl =mainWebsite + a_href;
                download(downloadUrl, name, category1, category2, codeName);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void download(String url, String name, String category1, String category2, String codeName) {
        // 该函数用于创建某标准的json对象，用于存储详细信息，以备传入数据库。
        try {

            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
            String regx = "\\.\\./";
            if (null != document) {
                Elements a_down = document.select(".soft_l li a").eq(2);
                if(a_down.text().equals("本地下载通道")){
                }else{
                    JSONObject info = new JSONObject();  // 创建一个json对象，用于传递标准的各个字段
                    info.put("name", name);
                    info.put("category", category1);  // category1 = “行业标准”，所有标准的该字段都一样
                    info.put("industry", category2);
                    info.put("codeName", codeName);  // 标准代码
                    info.put("crawlerId", "44");  // 爬虫代码
                    String state; // 标准状态  提取图像中src变量值
                    String releaseDate; // 发布时间
                    String implementationDate; // 实施时间
                    String departmentIssued; // 颁发部门
                    String abolitionDate;  // 废止时间
                    info.put("state", null);
                    info.put("releaseDate", null);
                    info.put("implementationDate", null);
                    info.put("departmentIssued", null);
                    info.put("abolitionDate", null);
                    String downloadUrl2 = a_down.attr("href").trim().split(regx)[1];
                    info.put("downloadUrl", downMainURL + downloadUrl2);  // 该标准的rar文件下载路径
                    insert(info);
                }

            }


        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
        // 将信息存入数据库
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.standardInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

    private void write(String file, String savePath) throws Exception {
        try {
            FileWriter out = new FileWriter(savePath, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
