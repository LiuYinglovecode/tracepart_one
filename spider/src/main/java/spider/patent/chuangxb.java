package spider.patent;

import com.alibaba.fastjson.JSONObject;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IConfigManager;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Set;

/**
 * @author liyujie
 * http://www.chuangxb.com/pattransaction/list_01_01_01_01_01_01_01_list_3_01.html
 */
public class chuangxb {
    private final static Logger LOGGER = LoggerFactory.getLogger(chuangxb.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String tableName = "original_patent_chuangxb";
    private static String mainWebsite = "http://www.chuangxb.com";
    private static String start = "http://www.chuangxb.com/pattransaction/list_01_01_01_01_01_01_01_list_";
    private static String end = "_01.html";
    private static String zookeeper;
    private static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    static {
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {
        zookeeper = args[0];
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, zookeeper);
        chuangxb chuangxb = new chuangxb();
//        chuangxb.patent();
        JSONObject object = new JSONObject();
        object.put("name", "上传实验2");
        chuangxb.insert(object);
        LOGGER.info("------done------");
    }

    private void patent() {
        try {
            for (int i = 1; i <= 231; i++) {
                String html = httpGet(start + i + end, "创新宝");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements patentList = document.select(".table.table-striped.table-hover .tab-div a");
                    for (Element e : patentList) {
                        String patentUrl = mainWebsite + e.attr("href");
                        String name = e.text().trim();
                        patentDetail(patentUrl, name);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void patentDetail(String url, String name) {
        try {
            JSONObject info = new JSONObject();
            info.put("name", name);
            String html = httpGet(url, "创新宝");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements div = document.select(".row3col1-1-1 dl div");
                for (Element e : div) {
                    String key = e.select("dt").text().trim();
                    switch (key) {
                        case "申请号":
                            info.put("applicationNumber", e.select("dd").text().trim());
                            break;
                        case "申请日":
                            info.put("applicationDate", e.select("dd").text().trim());
                            break;
                        case "公开/公告号":
                            info.put("publicNumber", e.select("dd").text().trim());
                            break;
                        case "公开/公告日":
                            info.put("publicDate", e.select("dd").text().trim());
                            break;
                        case "申请/专利权人":
                            info.put("applicant", e.select("dd").text().trim());
                            break;
                        case "发明/设计人":
                            info.put("inventor", e.select("dd").text().trim());
                            break;
                        case "主分类号":
                            info.put("mainClassificationNumber", e.select("dd").text().trim());
                            break;
                        case "分案申请地址":
                            info.put("address", e.select("dd").text().trim());
                            break;
                        case "国省代码":
                            info.put("nationalCode", e.select("dd").text().trim());
                            break;
                        default:
                    }
                }
                String zhaiyao = document.select(".abContent.abstract.contenttext").text().trim();
                info.put("abstract", zhaiyao);
                insert(info);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.baitengInsert(Map)) {
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
            for (int i = 0; i < 5; i++) {
                if (null != url) {
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null && html.contains(judgeWord)) {
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





