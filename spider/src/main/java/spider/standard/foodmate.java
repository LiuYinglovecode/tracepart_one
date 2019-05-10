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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;


/**
 * state:状态
 * releaseDate：发行日期
 * implementationDate：实施日期
 * abolitionDate：废除日期
 * departmentIssued：颁发部门
 * http://down.foodmate.net/standard/
 */
public class foodmate {
    private final static Logger LOGGER = LoggerFactory.getLogger(bzmfxz.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;  // Map集合存储的是键值对
    private static String tableName = "original_standard";
    private static String mainWebsite;  // 一级分类的主页
    private static String startWebsite;  // 一级分类的主页
    private static String zookeeper = "172.17.60.213:2181";
    //    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static String zookeeper = "172.20.4.213:2181";
    private static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66";



    static {
        mainWebsite = "http://down.foodmate.net/standard/";
        startWebsite = "http://down.foodmate.net/standard/";
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {

        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        foodmate foodmate = new foodmate();
        foodmate.shouye(mainWebsite);

    }

    private void shouye(String mainWebsite) {
        try {

            String[] industryStandard = {"sort/1/", "sort/2/"};
            for (String e : industryStandard) {

                if(e == "sort/1/") {
                    Standard(mainWebsite + e, "食品标准", "国内标准");  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }else{
                    Standard(mainWebsite + e, "食品标准", "国外标准");  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }
            }
//            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void Standard(String url, String category1, String category2) {
        // 该函数的作用是将每个二级分类中的详细标准名称、链接进行收集
        // 将这些信息传递给detail方法，进行每个标准的详细信息进行提取
        try {
            String html = httpGet(url, "站长");
            if (null != html) {
                // 获取当前页面链接
                Document document = Jsoup.parse(html);
                Elements standardList = document.select(".bz_list a");
                for (Element e : standardList) { // 例如：循环遍历每页中的标准名称
                    String detailUrl = e.attr("href");  // 获得各标准的地址，得到每个标准的详细信息列表
                    String name = e.select("b").text().trim();
//                    String nameByte = new String(e.select("b").text().trim().getBytes("gbk"));  // 每个标准名称，并去掉字符串首尾的空格
//                    name = getUTF8BytesFromGBKString(nameByte);
                    detail(detailUrl, name, category1, category2);
                }
                // 翻页操作
                Elements pageList = document.select(".pages a");  // ".pages a"表示class值为pages的元素下子元素为a标签
                String endPage;
                endPage = document.select(".page cite").text().split("/")[1].split("页")[0];  // 取出尾页数值
                // 由于翻页页码是正序排序的，所以不知道最后一页的页码数字，所以先获取尾页的页码数字再进行下一页操作
                for (Element e : pageList) {
                    if ("下一页".equals(e.text().trim())) {
                        if (!endPage.equals(e.attr("href").split("-")[1].split("\\.")[0])) {
                            // 如果连接中的页码数字不是尾页对应的数字，则重复standard的操作
                            Standard(e.attr("href"), category1, category2);
                        }
                    }else{
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url, String name, String category1, String category2) {
        // 该函数用于提取特定标准的标准代号和该标准的下载链接、标准状态、发布时间、实施时间、颁发部门、废止时间，传递给download函数。
        try {
            String html = httpGet(url, "站长");  // url为三级分类中每个标准的下载页链接
            if (null != html) {
                JSONObject info = new JSONObject();  // 创建一个json对象，用于存放信息

                Document document = Jsoup.parse(html);
                Elements td = document.select(".xztable tbody th,td");  // class值为xztable的tbody 下的td标签
                // 变量font用于判断是否有可供下载的文本
                Elements font = document.select(".title2 span font");
                String downloadUrl;  // 文本下载链接
                if ("（暂无文本）".equals(font.text())) {
                    downloadUrl = null;
                }else{
                    downloadUrl = document.select(".telecom").attr("href");  // 获取下载地址
                }

                String codeName = name.split(" ")[0] + " " + name.split(" ")[1];  // 提取出标准代号
                String state; //= bzState(td[2].select("img").attr("src"));// 标准状态  提取图像中src变量值
                String releaseDate; // = td[1].text().trim();  // 发布时间
                String implementationDate; //= td[3].text().trim();  // 实施时间
                String departmentIssued; //= td[4].text().trim();  // 颁发部门
                String abolitionDate;  //td[5].text().trim();  // 废止时间

                info.put("name", name);
                info.put("codeName", codeName);  // 标准代码
                info.put("category", category1);  // category1 = “行业标准”，所有标准的该字段都一样
                info.put("industry", category2);  // 机械标准等二级分类名称
                info.put("crawlerId", "30");  // 爬虫代码
                info.put("downloadUrl", downloadUrl);  // 下载文本地址

                for (Element e : td) {
                    if (e.text().contains("发布日期")) {
                        releaseDate = e.nextElementSibling().text().trim();
                        info.put("releaseDate", releaseDate);  // 发布时间
                    } else if (e.text().contains("标准状态")) {
                        state = bzState(e.nextElementSibling().select("img").attr("src"));
                        info.put("state", state);  // 标准状态
                    } else if (e.text().contains("实施日期")) {
                        implementationDate = e.nextElementSibling().text().trim();
                        info.put("implementationDate", implementationDate);  // 实施时间
                    } else if (e.text().contains("颁发部门")) {
                        departmentIssued = e.nextElementSibling().text().trim();
                        info.put("departmentIssued", departmentIssued);  // 颁发部门
                    } else if (e.text().contains("废止日期")) {
                        abolitionDate = e.nextElementSibling().text().trim();
                        info.put("abolitionDate", abolitionDate);  // 废止时间
                    } else {
                        continue;
                    }
                }

                insert(info);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String bzState(String e){
        // 该函数根据状态图片的后缀名来判断标准状态
        if(e.contains("yjfz")){
            return "已经废止";
        }else if(e.contains("jjfz")){
            return "即将废止";
        }else if(e.contains("xxyx")){
            return "现行有效";
        }else if(e.contains("jjss")){
            return "即将实施";
        }else {
            return "未知";
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

    private String httpGetWithProxy(String url, String judgeWord) {
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
                if (html != null && html.contains(judgeWord)) {
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

    public static String getUTF8BytesFromGBKString(String gbkStr)  throws UnsupportedEncodingException {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
            utfBytes[k++] = (byte) m;
            continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            utfBytes = tmp;
        }
        return new String(utfBytes,"UTF-8");
    }

    private String httpGet(String url, String judgeWord) {
        try {
            String html = null;
            for (int i = 0; i < 5; i++) {  // 接连获取五次网页内容
                if (null != url) {  // 如果url不为空，进行抓取
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null) {  // 如果抓取的网页不为空，并且包含关键字，则返回
//                    return URLEncoder.encode(html, "utf-8");  // 返回网页的字符串形式
//                    return getUTF8StringFromGBKString(html);
//                    String html1 = new String(html.getBytes("UTF-8"),"UTF-8");
                    return html;

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
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
