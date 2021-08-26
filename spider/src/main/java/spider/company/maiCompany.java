package spider.company;

import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


public class maiCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(maiCompany.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header = null;
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.companyInsert(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }


    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        maiCompany maiCompany = new maiCompany();
        maiCompany.industryList("http://www.86mai.com/company/");
        LOGGER.info("maiCompany DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    private void industryList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("td.catalog_tds p a.px15");
            for (Element e : select) {
                String link = e.attr("href");
                nextPage(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPage(String url) {
        String replace = url.replace(".html", "");
        String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
        Document parse = Jsoup.parse(html);
        String Total = parse.select("div.pages cite").text().split("/")[1].replace("页", "");
        int total = Integer.valueOf(Total).intValue() + 1;//类型转换
        int nextPage = 1;
        for (nextPage = 1; nextPage < total; nextPage++) {
            String link = replace + "_" + nextPage + ".html";//拼接链接地址
            System.out.println("下一页：" + link);
            companyList(link);
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.list table tbody tr td ul li a");
            for (Element e : select) {
                String link = e.attr("href");
                companyinfo(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void companyinfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中麦网");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements navigation = parse.select("div#menu.menu ul li a");//企业页面导航栏
                if (navigation.size() != 0) {
                    for (Element element : navigation) {
                        if (element.text().contains("公司介绍")) {
                            String href1 = element.attr("href");
                            String introduce = HttpUtil.httpGetwithJudgeWord(href1, "中麦网");
                            Document introduceParse = Jsoup.parse(introduce);
                            Elements select = introduceParse.select("td.f_b");
                            for (Element e : select) {
                                if (e.text().contains("公司类型：")) {
                                    companyInfo.put("company_model", e.nextElementSibling().text());
                                }
                                if (e.text().contains("公司规模：")) {
                                    companyInfo.put("employees", e.nextElementSibling().text());
                                }
                                if (e.text().contains("注册资本：")) {
                                    companyInfo.put("register_capital", e.nextElementSibling().text());
                                }
                                if (e.text().contains("注册年份：")) {
                                    companyInfo.put("company_register_time", e.nextElementSibling().text());
                                }
                                if (e.text().contains("经营模式：")) {
                                    companyInfo.put("management_model", e.nextElementSibling().text());
                                }
                                if (e.text().contains("销售的产品：")) {
                                    companyInfo.put("main_product", e.nextElementSibling().text());
                                }
                                if (e.text().contains("主营行业：")) {
                                    companyInfo.put("industry", e.nextElementSibling().text());
                                }
                            }
                        } else if (element.text().contains("联系方式")) {
                            String href2 = element.attr("href");
                            String contact = HttpUtil.httpGetwithJudgeWord(href2, "中麦网");
                            Document contactParse = Jsoup.parse(contact);
                            Elements select1 = contactParse.select("div.px13.lh18 table tbody tr td");
                            for (Element element1 : select1) {
                                if (element1.text().contains("公司名称：")) {
                                    companyInfo.put("name", element1.nextElementSibling().text().trim());
                                    companyInfo.put("id", MD5Util.getMD5String(element1.nextElementSibling().text().trim()));
                                }
                                if (element1.text().contains("所在地区：")) {
                                    companyInfo.put("address", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("邮政编码：")) {
                                    companyInfo.put("postcode", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("公司电话：")) {
                                    companyInfo.put("landline", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("公司网址：")) {
                                    companyInfo.put("website", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("联 系 人：")) {
                                    companyInfo.put("contact", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("手机号码：")) {
                                    companyInfo.put("phone", element1.nextElementSibling().text().trim());
                                }
                                if (element1.text().contains("公司传真：")) {
                                    companyInfo.put("fax", element1.nextElementSibling().text().trim());
                                }
                            }
                        }
                    }
                } else {
                    LOGGER.info("公司主页正在等待开通");
                }
            } else {
                LOGGER.info("网页异常");
            }


            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("crawlerId", "63");
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
}
