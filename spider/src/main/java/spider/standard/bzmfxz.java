package spider.standard;

import com.alibaba.fastjson.JSONObject;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.os.windows.Windows;
import util.HttpUtil;
import util.IConfigManager;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;


/**
 * @author liyujie
 * http://www.bzko.com/
 */
public class bzmfxz {
    private final static Logger LOGGER = LoggerFactory.getLogger(bzmfxz.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;  // Map集合存储的是键值对
    private static String tableName = "original_standard_bzko";
    private static String mainWebsite;  // 一级分类的主页
    private static String startWebsite;  // 一级分类的主页
    private static String zookeeper = "172.17.60.213:2181";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static String zookeeper = "172.20.4.213:2181";
    private static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66";



    static {
        mainWebsite = "http://www.bzmfxz.com";
        startWebsite = "http://www.bzmfxz.com/biaozhun/Soft/index.html";
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {

        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        bzmfxz bzmfxz = new bzmfxz();
        bzmfxz.shouye(startWebsite);

    }

    private void shouye(String startUrl) {
        try {
            String html = httpGet(startUrl, "站长统计");  // 网页的字符串形式
            if (null != html) {
                Document document = Jsoup.parse(html);  // 生成html文档
                Elements industryStandard = document.select(".childclass_main .childclass_main_box");
                for (Element e : industryStandard) {  // for循环迭代其中的每个行业的div
                    // 获取href的属性值，即每个行业的标准详细列表
                    Elements industryCategory = e.select(".childclass_title a");
                    String industryUrl = industryCategory.attr("href");
                    String category2 = industryCategory.text().trim().split(" ")[1];  // 行业标准里的细分标准名称
                    Standard(mainWebsite + industryUrl, "国内标准", category2);  // url已经是二级各行业标准的细分类页，即显示的三类分类的标题
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void Standard(String url, String category1, String category2) {
        // 该函数的作用是将每个二级分类中的详细标准名称、链接进行收集
        // 将这些信息传递给detail方法，进行每个标准的详细信息进行提取
        try {

            String html = httpGet(url, "站长统计");
            if (null != html) {
                // 获取当前页面链接
                Document document = Jsoup.parse(html);
                Elements standardList = document.select(".childclasslist_box .c_main_box a");
                for (Element e : standardList) { // 例如：迭代遍历机械标准、化工标准这些二级分类页中的三级分类标准名称
                    String detailUrl = e.attr("href");  // 获得各行业详细分类的地址，得到每个行业中的详细标准列表
                    String name = e.text().trim();  // 各行业中每个细节的标准名称，并去掉字符串首尾的空格
                    detail(mainWebsite + detailUrl, name, category1, category2);
                }
                // 翻页操作
                Elements pageList = document.select(".pagecss a");  // ".pagecss a"表示class值为pagecss的元素下子元素为a标签
                String endPage;
                // 由于翻页页码是正序排序的，所以不知道最后一页的页码数字，所以先获取尾页的页码数字再进行下一页操作
                for (Element e : pageList) {
                    if ("尾页".equals(e.text().trim())) {
                        endPage = e.attr("href").split("_")[1].split("\\.")[0];
                        for (Element i : pageList) {
                            if ("下一页".equals(i.text().trim())) {
                                if (!endPage.equals(i.attr("href").split("_")[1].split("\\.")[0])) {
                                    // 如果连接中的页码数字不是“1”，则重复standard的操作
                                    Standard(mainWebsite + i.attr("href"), category1, category2);
                                }
                            }
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
        // 该函数用于提取特定标准的标准代号和该标准的下载链接，传递给download函数。
        try {
            String html = httpGet(url, "站长统计");  // url为三级分类中每个标准的下载页链接
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements td = document.select("#fontzoom table tbody tr td table tbody tr td table tbody tr td");  // class值为s_info_box的tbody tr td标签
                for (Element e : td) {
                    if (e.text().trim().contains("标准编号：")) {  // 提取出标准代号
                        String codeName = e.text().trim().split("：")[1];
                        String downloadUrl = document.select(".c_content_overflow a").attr("onclick").split("\\('")[1].split("' \\)")[0];
                        // onclick="window.open('/Common/ShowDownloadUrl.aspx?urlid=0&id=210019')"  从括号分裂这个值，截取出下载地址
                        download(mainWebsite + downloadUrl, name, category1, category2, codeName);
                    }
                }
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
            info.put("crawlerId", "22");  // 标准代码
            info.put("createTime", creatrTime.format(new Date()));  // 标准代码
            String html = httpGet(url, "点击下载");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String downloadUrl2 = document.select("#content table tbody tr td a").attr("href");
                info.put("downloadUrl", downloadUrl2);  // 该标准的rar文件下载路径
            }
             insert(info);

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

    private String httpGet(String url, String judgeWord) {
        try {
            String html = null;
            for (int i = 0; i < 5; i++) {  // 接连获取五次网页内容
                if (null != url) {  // 如果url不为空，进行抓取
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null) {  // 如果抓取的网页不为空，并且包含关键字，则返回
                    return html;  // 返回网页的字符串形式

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
