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
import util.HttpUtil;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;


/**
 *
 * http://www.gb99.cn/biaozhunchaxun/  国标久久
 */
public class gb99 {
    private final static Logger LOGGER = LoggerFactory.getLogger(gb99.class);
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
        mainWebsite = "http://www.gb99.cn/";
        downMainURL = "http://www.gb99.cn/e/DownSys/";
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {

        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        gb99 gb99 = new gb99();
        gb99.shouye(mainWebsite);

    }

    private void shouye(String mainWebsite) {
        try {

            String[] industryStandard = {"hy", "gb", "DB", "Q"};
            for (String i : industryStandard) {

                if(i == "hy") {
                    standard_2(mainWebsite+i, "行业标准");  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }else if(i == "gb"){
                    standard_1(mainWebsite,i, "国家标准", "GB国家标准","http://www.gb99.cn/");
                }else if(i == "DB"){
                    standard_1(mainWebsite,i, "地方标准", "地方标准","http://www.gb99.cn/");
                }else{
                    standard_1(mainWebsite,i, "企业标准", "企业标准","http://www.gb99.cn/");
                }
            }
//            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void standard_1(String mainWebsite,String i, String category1, String category2, String p) {
        // i就充当首页中一次变量，后边翻页不再用到
        // 该函数的作用是将每个二级分类中的详细标准名称、链接进行收集
        // 将这些信息传递给detail方法，进行每个标准的详细信息进行提取
        try {
//            Thread.sleep(3000);
//            Document document = Jsoup.parse(new URL(p + i).openStream(), "GBK", p + i);
            String html = httpGetWithProxy(p + i);
            if ("" != html) {
                Document document = Jsoup.parse(html);
                // 获取当前页面链接
                Elements standardList = document.select(".p_list_7 b a");
                for (Element e : standardList) { // 例如：循环遍历国家标准、其他标准这些一级分类页中的标准名称
                    String detailUrl = e.attr("href");  // 获得各行业详细分类的地址，得到每个行业中的详细标准列表
                    String name = e.text().trim();  // 各行业中每个细节的标准名称，并去掉字符串首尾的空格
                    if(detailUrl.contains("www")){
                        detail(detailUrl, name, category1, category2);
                    }else{
                        detailUrl = mainWebsite + detailUrl;
                        detail(detailUrl, name, category1, category2);
                    }

                }
                // 翻页操作
                Elements pageList = document.select(".list_page td a");
                int endPage;
                // 由于翻页页码是正序排序的，所以不知道最后一页的页码数字，所以先获取尾页的页码数字再进行下一页操作
                for (Element e : pageList) {
                    if ("尾页".equals(e.text().trim())) {
                        endPage = Integer.valueOf(e.attr("href").split("_")[1].split("\\.")[0]);
                        for (Element j : pageList) {
                            if ("下一页".equals(j.text().trim())) {
                                if (endPage >= Integer.valueOf(j.attr("href").split("_")[1].split("\\.")[0])) {
                                    // 如果连接中的页码数字不是“1”，则重复standard的操作
                                    standard_1(mainWebsite,"", category1, category2, j.attr("href"));
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

    private void standard_2(String mainWebsite, String category1){
        /*
        * 由于国家标准和行业标准的二级分类结构不同，所以对提取行业标准二级分类行为单独命名一个函数
        * */
        try{
            String html = httpGetWithProxy(mainWebsite);
            if ("" != html) {
                Document document = Jsoup.parse(html);
                // 获取当前页面链接
                Elements standardList = document.select(".p_list_7 tr td a");// 获取行业标准详细分类
                for (Element e : standardList) { // 例如：迭代遍历机械标准、化工标准这些二级分类页中的三级分类标准名称
                    String category2_Url = e.attr("href");  // 获得各行业详细分类的地址，得到每个行业中的详细标准列表
                    String category2 = e.text().trim();  // 各行业中每个细节的标准名称，并去掉字符串首尾的空格
                    standard_1("http://www.gb99.cn/biaozhunchaxun","", category1, category2,category2_Url);  //将在网页中获取的行业分类category2传进去
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }


    private void detail(String url, String name, String category1, String category2) {
        // 该函数用于提取特定标准的标准代号和该标准的下载链接，传递给download函数。
        try {
//            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
            String html = httpGetWithProxy(url);
            if ("" != html) {
                Document document = Jsoup.parse(html);
                int nameLen = name.trim().split(" ").length;  //将名字根据空格分隔开，并计算出序列的长度
                String codeName = name.trim().split(" ")[nameLen-2] + " " + name.trim().split(" ")[nameLen-1];
                Element a = document.select("#download_box a").last();  // class值为contentdown的元素标签
                String downloadUrl;
                String a_onclick = a.select("a").attr("onclick");
                downloadUrl = a_onclick.split("\'")[1];
                download(downloadUrl, name, category1, category2, codeName);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void download(String url, String name, String category1, String category2, String codeName) {
        // 该函数用于创建某标准的json对象，用于存储详细信息，以备传入数据库。
        try {
            JSONObject info = new JSONObject();  // 创建一个json对象，用于传递标准的各个字段
            info.put("name", name);
            info.put("category", category1);  // category1 = “行业标准”，所有标准的该字段都一样
            info.put("industry", category2);  // 机械标准等二级分类名称
            info.put("codeName", codeName);  // 标准代码
            info.put("crawlerId", "42");  // 标准代码
//            Document document = Jsoup.parse(new URL(url).openStream(), "gb2312", url);

            String html = httpGetWithProxy(url);
            String regx = "\\.\\./";
            if (null != html) {
                Document document = Jsoup.parse(html);
                String downloadUrl2 = document.select("table tr td a").first().attr("href").trim().split(regx)[1];
                info.put("downloadUrl", downMainURL + downloadUrl2);  // 该标准的rar文件下载路径
            }
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
            insert(info);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String httpGetWithProxy(String url) {
        String ipProxy = null;
        try {
            if (ipProxyList.isEmpty()) {
                LOGGER.info("ipProxyList is empty");
                Set<String> getProxy = getProxy();
                ipProxyList.addProxyIp(getProxy);
            }
            ipProxy = ipProxyList.getProxyIp();
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (url != null && ipProxy != null) {
                    html = HttpUtil.httpGetWithProxy(url, header, ipProxy);
                }
                if (html != "") {
                    return html;
                }
                ipProxyList.removeProxyIpByOne(ipProxy);
                ProxyDao.delectProxyByOne(ipProxy);
                ipProxy = ipProxyList.getProxyIp();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
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
