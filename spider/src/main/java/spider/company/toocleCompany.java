package spider.company;

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
import util.MD5Util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author liyujie
 * http://chn.toocle.com/market/
 * Company
 */
public class toocleCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(toocleCompany.class);
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
    private static String baseUrl = "http://chn.toocle.com";
    private static Map<String, String> Map = null;
    private static String savePage = "F:\\DataCatch\\toocle\\page.txt";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    private void category(String url) {
        try {
            String html = httpGet(url, "把生意宝设为首页");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#list .mall .floor-hd.fl ul li a");
                for (Element e : categoryList) {
                    String companyList = baseUrl + e.attr("href");
                    write("category : " + companyList);
                    companyList(companyList);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = httpGet(url, "把生意宝设为首页");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements companyUrlList = document.select("#detail .text_box2 a:first-child");
                for (Element e : companyUrlList) {
                    String companyUrl = e.attr("href");
                    companyDetail(companyUrl);
                }
                if (0 != document.select(".next.sxpg").size()) {
                    companyList("http://chn.toocle.com/market/" + document.select(".next.sxpg").attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyDetail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGet(url, "免责声明");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements infoLisr = document.select(".blink_box tbody tr");
                String MD5 = MD5Util.getMD5String(document.select(".link_way1 .firmname1").text().trim());
                for (Element e : infoLisr) {
                    String key = e.select(".tname").text();
                    switch (key) {
                        case "公司名称：":
                            companyInfo.put("id", MD5Util.getMD5String(e.select(".cont1").text()));
                            companyInfo.put("name", e.select(".cont1").text());
                            break;
                        case "联 系 人：":
                            companyInfo.put("contact", e.select(".cont1").text());
                            break;
                        case "联系电话：":
                            companyInfo.put("landline", e.select(".cont1").text());
                            break;
                        case "手　　机：":
                            companyInfo.put("mobile_phone", e.select(".cont1").text());
                            break;
                        case "联系传真：":
                            companyInfo.put("fax", e.select(".cont1").text());
                            break;
                        case "网　　址：":
                            companyInfo.put("website", e.select(".cont1").text());
                            break;
                        case "生意旺铺：":
                            companyInfo.put("business", e.select(".cont1").text());
                            break;
                        case "联系地址：":
                            companyInfo.put("address", e.select(".cont1").text());
                            break;
                        case "邮　　编：":
                            companyInfo.put("zip_code", e.select(".cont1").text());
                            break;
                        default:
                    }
                }
                insertToMySQL(companyInfo, "bde.original_company_toocle", MD5);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insertToMySQL(JSONObject companyInfo, String tablename, String MD5) {
        Map = (Map) companyInfo;
        if (updateToMySQL.exist(Map, tablename, MD5)) {
            if (updateToMySQL.toocleUpdate(Map, MD5)) {
                LOGGER.info("更新中 : " + Map.toString());
            }
        } else {
            if (updateToMySQL.toocleInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        toocleCompany toocleCompany = new toocleCompany();
        toocleCompany.category("http://chn.toocle.com/market/");
        LOGGER.info("------完成了------");
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

    public static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

    private void write(String file) throws Exception {
        try {
            FileWriter out = new FileWriter(savePage, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
