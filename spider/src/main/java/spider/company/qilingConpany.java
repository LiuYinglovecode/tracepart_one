package spider.company;

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
import util.ESUtil;
import util.HttpUtil;
import util.IpProxyUtil;
import util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.707070.cn/city/</a>
 * <p>Compangy：企领网</p>
 *
 * @author chenyan
 */
public class qilingConpany {

    private final static Logger LOGGER = LoggerFactory.getLogger(qth58Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static final String homepage = "http://www.707070.cn/city";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        qilingConpany qilingConpany = new qilingConpany();
        qilingConpany.qilingProvince("http://www.707070.cn/city/");
        LOGGER.info("qilingConpany DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //    按照省份分类，获取url进入省份企业列表
    private void qilingProvince(String url) {
        try {
            String html = httpGet(url, "企领网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("dd.cities a.keys");
            for (Element link : select) {
                String href = homepage + "/" + link.attr("href");
                companyList(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  企业列表，获取企业url
    private void companyList(String url) {
        try {
            String html = httpGet(url, "企领网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.item-infos h2.title a");
            for (Element link : select) {
                String href = link.attr("href");
                String name = link.text();
                companyWebPage(href, name);


            }
//          获取下一页企业列表
            Elements nextPage = document.select("div.paginator a");
            for (Element link : nextPage) {
                if (link.text().contains("下一页")) {
                    String nextPageLink = homepage.replace("/city", "") + link.attr("href");
                    System.out.println("下一页：" + nextPageLink);
                    companyList(nextPageLink);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  进入公司网页获取公司联系方式及公司基本信息
    private void companyWebPage(String url, String name) {
        JSONObject companyInfo = new JSONObject();
        companyInfo.put("website", url);//网址
        try {
            String html = httpGet(url, "企领网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                companyInfo.put("company_info", document.select("div.box_con.company-profile").text().trim());
                Elements info = document.select("ul.box_con-dlist.row2 li");
                for (Element element : info) {
                    if (element.text().contains("注册资金：")) {
                        companyInfo.put("register_capital", element.text().replace("注册资金：", ""));
                    }
                    if (element.text().contains("经营模式：")) {
                        companyInfo.put("management_model", element.text().replace("经营模式：", ""));
                    }
                    if (element.text().contains("年营业额：")) {
                        companyInfo.put("company_turnover", element.text().replace("年营业额：", ""));
                    }
                    if (element.text().contains("企业人员：")) {
                        companyInfo.put("employees", element.text().replace("企业人员：", ""));
                    }
                    if (element.text().contains("经营面积：")) {
                        companyInfo.put("company_area", element.text().replace("经营面积：", ""));
                    }
                    if (element.text().contains("质量标准：")) {
                        companyInfo.put("quality_control", element.text().replace("质量标准：", ""));
                    }
                }
                Elements navigation = document.select("li.contact.current a");
                for (Element element : navigation) {
                    if (element.text().contains("联系我们")) {
                        String href = homepage + element.attr("href");
                        String page = httpGet(href, "企领网");
                        Document doc = Jsoup.parse(page);
                        Elements contactUs = doc.select("ul.box_con-dlist li");
                        for (Element us : contactUs) {
                            if (us.text().contains("联系人：")) {
                                companyInfo.put("contact", us.text().split("：")[1]);
                            }
                            if (us.text().contains("电话：")) {
                                companyInfo.put("landline", us.text().split("：")[1]);
                            }
                            if (us.text().contains("网址：")) {
                                companyInfo.put("website", us.text().split("：")[1]);
                            }
                            if (us.text().contains("邮编：")) {
                                companyInfo.put("postcode", us.text().split("：")[1]);
                            }
                        }
                    }
                }
            }
            companyInfo.put("name", name);
            companyInfo.put("id", MD5Util.getMD5String(name));
            companyInfo.put("crawlerId", "56");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            insert(companyInfo);
            esUtil.writeToES(companyInfo, "crawler-company-", "doc", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.dataUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
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
}
