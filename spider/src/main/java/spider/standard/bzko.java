package spider.standard;

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
import config.IConfigManager;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Set;

/**
 * @author liyujie
 * http://www.bzko.com/
 */
public class bzko {
    private final static Logger LOGGER = LoggerFactory.getLogger(bzko.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String tableName = "original_standard_bzko";
    private static String mainWebsite = "http://www.bzko.com";
    private static String zookeeper = "172.17.60.213:2181";
    //    private static String zookeeper = "172.20.4.213:2181";
    private static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";


    static {
        header = new HashMap();
        header.put("User-Agent", UserAgent);
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        bzko bzko = new bzko();
        bzko.shouye(mainWebsite);
//        JSONObject obj = new JSONObject();
//        obj.put("name","name");
//        bzko.insert(obj);
    }

    private void shouye(String url) {
        try {
            String html = httpGet(url, "标准库");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements industryStandard = document.select(".subnav li a");
                for (Element e : industryStandard) {
                    String industryUrl = e.attr("href");
                    String category2 = e.text().trim();
                    Standard(mainWebsite + industryUrl, "行业标准", category2);
                }
                Elements type = document.select(".menu li a");
                for (Element e : type) {
                    String key = e.select("span").text().trim();
                    switch (key) {
                        case "国家标准":
                            Standard(mainWebsite + e.attr("href"), key, "");
                            break;
                        case "标准汇编":
                            Standard(mainWebsite + e.attr("href"), key, "");
                            break;
                        case "国外标准":
                            Standard(mainWebsite + e.attr("href"), key, "");
                            break;
                        case "其他标准":
                            Standard(mainWebsite + e.attr("href"), key, "");
                        default:
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private void Standard(String url, String category1, String category2) {
        try {
            String html = httpGet(url, "标准库");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements standardList = document.select(".childclasslist_box .c_main_box .childclasslist_title");
                for (Element e : standardList) {
                    String detailUrl = e.select("a").attr("href");
                    String name = e.select("a").text().trim();
                    detail(mainWebsite + detailUrl, name, category1, category2);
                }
                Elements pageList = document.select(".pagecss a");
                for (Element e : pageList) {
                    if ("下一页".equals(e.text().trim())) {
                        if (!"1".equals(e.attr("href").split("_")[1].split("\\.")[0])) {
                            Standard(mainWebsite + e.attr("href"), category1, category2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url, String name, String category1, String category2) {
        try {

            String html = httpGet(url, "标准库");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements td = document.select("#s_info_box tbody tr td");
                for (Element e : td) {
                    if (e.text().trim().contains("标准代号")) {
                        String codeName = e.text().trim().split("：")[1];
                        String downloadUrl = document.select(".s_page a").attr("onclick").split("\\('")[1].split("'\\)")[0];
                        download(mainWebsite + downloadUrl, name, category1, category2, codeName);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private void download(String url, String name, String category1, String category2, String codeName) {
        try {
            JSONObject info = new JSONObject();
            info.put("name", name);
            info.put("category", category1);
            info.put("industry", category2);
            info.put("codeName", codeName);
            String html = httpGet(url, "标准库");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String downloadUrl2 = document.select("#content a").attr("href");
                info.put("downloadUrl", downloadUrl2);
            }
            insert(info);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
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


