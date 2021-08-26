package spider.company;

import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import util.ESUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IpProxyUtil;
import util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Company: 全天候贸易网<p>
 *
 * @author chenyan
 */
public class qth58Company {
    private final static Logger LOGGER = LoggerFactory.getLogger(qth58Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    //    按照省份分类，获取url进入省份企业列表
    private void qth58Province(String url) {

        try {
            String html = httpGet(url, "全天候贸易网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("#ypages > div.layout.yp > ul > li > a");
            for (Element link : select) {
                String href = link.attr("href");
                Thread.sleep(5000);
                companyList(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  企业列表，获取企业url
    private void companyList(String url) {
        try {
            String html = httpGet(url, "全天候贸易网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.message > ul > li > p > a");
            for (Element link : select) {
                String href = link.attr("href");
                Thread.sleep(5000);
                companyWebPage(href);
            }
//          获取下一页企业列表
            Elements nextPage = document.select("#Pagination > div > a");
            for (Element link : nextPage) {
                if (link.text().contains("下一页")) {
                    String nextPageLink = link.attr("href");
                    System.out.println("下一页：" + nextPageLink);
                    Thread.sleep(5000);
                    companyList(nextPageLink);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  进入公司网页获取公司联系方式及公司基本信息
    private void companyWebPage(String url) {
        JSONObject companyInfo = new JSONObject();
        companyInfo.put("website", url);//网址
        try {
            String html = httpGet(url, "全天候贸易网");
            Document document = Jsoup.parse(html);
//            联系方式
            Elements select = document.select("div.profile-main > div > ul > li");
            for (Element contactInformation : select) {
                if (contactInformation.select("span").text().contains("联系人：")) {
                    contactInformation.select("span").remove();
                    companyInfo.put("contact", contactInformation.text().trim());
                } else if (contactInformation.select("span").text().contains("移动电话：")) {
                    contactInformation.select("span").remove();
                    companyInfo.put("phone", contactInformation.text().trim());
                } else if (contactInformation.select("span").text().contains("电话：")) {
                    contactInformation.select("span").remove();
                    companyInfo.put("landline", contactInformation.text().trim());
                } else if (contactInformation.select("span").text().contains("邮编：")) {
                    contactInformation.select("span").remove();
                    companyInfo.put("postcode", contactInformation.text().trim());
                } else if (contactInformation.select("span").text().contains("地址：")) {
                    contactInformation.select("span").remove();
                    companyInfo.put("address", contactInformation.text().trim());
                }
            }
//          公司介绍
            companyInfo.put("company_info", document.select("div.company-intro.clearfix > div > p").text().trim());
//          公司基本信息
            Elements basicInformation = document.select("div.company-info > table > tbody > tr");
            for (Element element : basicInformation) {
                if (element.select(".c").text().contains("公司名称")) {
                    element.select(".c").remove();
                    companyInfo.put("name", element.select("tr").text().trim());
                    companyInfo.put("id", MD5Util.getMD5String(element.select("tr").text().trim()));
                } else if (element.select(".c").text().contains("企业类型")) {
                    element.select(".c").remove();
                    companyInfo.put("company_model", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("经营模式")) {
                    element.select(".c").remove();
                    companyInfo.put("management_model", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("注册资本")) {
                    element.select(".c").remove();
                    companyInfo.put("register_capital", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("年营业额")) {
                    element.select(".c").remove();
                    companyInfo.put("company_turnover", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("法定代表人/负责人")) {
                    element.select(".c").remove();
                    companyInfo.put("incorporator", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("公司注册号")) {
                    element.select(".c").remove();
                    companyInfo.put("from_where_table_id", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("员工人数")) {
                    element.select(".c").remove();
                    companyInfo.put("employees", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("质量控制")) {
                    element.select(".c").remove();
                    companyInfo.put("quality_control", element.select("tr").text().trim());
                } else if (element.select(".c").text().contains("公司成立时间")) {
                    element.select(".c").remove();
                    companyInfo.put("company_register_time", element.select("tr").text().trim());
                }
            }
            companyInfo.put("crawlerId", "30");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            insert(companyInfo);
            esUtil.writeToES(companyInfo, "crawler-company-", "doc", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.companyInsert(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        qth58Company qth58Company = new qth58Company();
        qth58Company.qth58Province("http://shop.qth58.cn/");
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

    private static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

}
