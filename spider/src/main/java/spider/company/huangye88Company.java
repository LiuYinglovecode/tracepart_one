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
 * http://b2b.huangye88.com/
 */
public class huangye88Company {
    private final static Logger LOGGER = LoggerFactory.getLogger(huangye88Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
    private static String savePage = "F:\\DataCatch\\huangye88\\page.txt";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    private void category(String url) {
        try {
            String html = httpGet(url, "关于我们");
            Document document = Jsoup.parse(html);
            Elements categoryList = document.select(".qiyecont li a");
            for (int i = 40; i < categoryList.size(); i++) {
                write("城市 : " + String.valueOf(i), "F:\\DataCatch\\huangye88\\page.txt");
                String catrgoryUrl = categoryList.eq(i).attr("href");
                address(catrgoryUrl);
            }
//            for (Element e : categoryList) {
//                String catrgoryUrl = e.attr("href");
//                address(catrgoryUrl);
//            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void address(String url) {
        try {
            String html = httpGet(url, "关于我们");
            Document document = Jsoup.parse(html);
            Elements addressList = document.select(".main .box .ad_list a");
            for (Element e : addressList) {
                String addressUrl = e.attr("href");
                company(addressUrl);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void company(String url) {
        try {
            String html = httpGet(url, "关于我们");
            Document document = Jsoup.parse(html);
            Elements companyList = document.select(".mach_list2 dt h4 a");
            for (Element e : companyList) {
                String companyUrl = e.attr("href") + "company_detail.html";
                detail(companyUrl);
            }
            /**
             *  下一页
             */
            Elements pages = document.select(".page_tag.Baidu_paging_indicator a");
            for (Element e : pages) {
                if ("下一页".equals(e.text())) {
                    company(e.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGet(url, "公司首页");
            Document document = Jsoup.parse(html);
            companyInfo.put("name", document.select(".com-name").text());
            String id = MD5Util.getMD5String(document.select(".com-name").text());
            companyInfo.put("id", id);
            companyInfo.put("company_info", document.select(".r-content tbody").text());
            Elements topInfo = document.select(".data .con-txt li");
            for (Element e : topInfo) {
                String key = e.text().split("：", 2)[0];
                switch (key) {
                    case "所在地":
                        companyInfo.put("address", e.text().split("：", 2)[1]);
                        break;
                    case "企业类型":
                        companyInfo.put("company_model", e.text().split("：", 2)[1]);
                        break;
                    case "成立时间":
                        companyInfo.put("company_register_time", e.text().split("：", 2)[1]);
                        break;
                    case "主营行业":
                        companyInfo.put("industry", e.text().split("：", 2)[1]);
                        break;
                    case "主营产品":
                        companyInfo.put("main_product", e.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            Elements leftInfo = document.select(".c-left .w-layer .l-content .l-txt.none li");
            for (Element e : leftInfo) {
                String key = e.select("label").text();
                switch (key) {
                    case "联系人：":
                        companyInfo.put("contact", e.text().split("：", 2)[1]);
                        break;
                    case "手机：":
                        companyInfo.put("phone", e.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            insert(companyInfo, "original_company_huangye88_new", id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject companyInfo, String tableName, String MD5) {
        try {
            Map = (Map) companyInfo;
            if (updateToMySQL.exist(Map, tableName, MD5)) {
                if (updateToMySQL.huangye88Update(Map, MD5)) {
                    LOGGER.info("更新中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\huangye88\\update.txt");
                }
            } else {
                if (updateToMySQL.huangye88Insert(Map)) {
                    LOGGER.info("插入中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\huangye88\\insert.txt");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        huangye88Company huangye88Company = new huangye88Company();
        huangye88Company.category("http://b2b.huangye88.com/");
        LOGGER.info("---完成了---");
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
