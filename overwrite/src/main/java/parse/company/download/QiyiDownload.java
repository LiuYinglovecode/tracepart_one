package parse.company.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.news.download.CableabcDownload;
import util.ESUtil;
import util.HttpUtil;
import util.MD5Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class QiyiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiDownload.class);
    private static java.util.Map<String, String> Map = null;

    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    private void insertToMySQL(JSONObject companyInfo) {
        Map = (Map) companyInfo;
        if (updateToMySQL.dataUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }


    //企业信息
    public void companyInfo(String url) {
        String aboutUs = url + "AboutUs.html";
        String contactInfo = url + "ContactInfo.html";

        JSONObject companyInfo = new JSONObject();
        try {
            String aboutUsHtml = HttpUtil.httpGetwithJudgeWord(aboutUs, "71.net");
            String contactInfoHtml = HttpUtil.httpGetwithJudgeWord(contactInfo, "71.net");
            Document aboutUsParse = Jsoup.parse(aboutUsHtml);
            Document contactInfoParse = Jsoup.parse(contactInfoHtml);
            companyInfo.put("company_info", aboutUsParse.select("div.company-intro div.con p").text().trim());
            Elements elements = aboutUsParse.select("div.con table tbody tr th");
            for (Element element : elements) {
                if (element.text().contains("公司类型")) {
                    companyInfo.put("company_model", element.nextElementSibling().text().trim());
                } else if (element.text().contains("经营模式")) {
                    companyInfo.put("management_model", element.nextElementSibling().text().trim());
                } else if (element.text().contains("主营行业")) {
                    companyInfo.put("industry", element.nextElementSibling().text().trim());
                } else if (element.text().contains("成立时间")) {
                    companyInfo.put("company_register_time", element.nextElementSibling().text().trim());
                } else if (element.text().contains("注册地址")) {
                    companyInfo.put("register_address", element.nextElementSibling().text().trim());
                } else if (element.text().contains("邮政编码")) {
                    companyInfo.put("postcode", element.nextElementSibling().text().trim());
                }
            }

            Elements name = aboutUsParse.select("div.con p.company");
            if (name.size() != 0) {
                companyInfo.put("name", name.text().trim());
                companyInfo.put("id", MD5Util.getMD5String(name.text().trim()));
            }
            Elements contact = aboutUsParse.select("div.con ul li.name");
            if (contact.size() != 0) {
                companyInfo.put("contact", contact.text().trim());
            }
            Elements contacts = contactInfoParse.select("div.contact-card01 dl dt");
            for (Element element : contacts) {
                if (element.text().contains("手机：")) {
                    companyInfo.put("phone", element.nextElementSibling().text().trim());
                } else if (element.text().contains("电话：")) {
                    companyInfo.put("landline", element.nextElementSibling().text().trim());
                } else if (element.text().contains("邮箱：")) {
                    companyInfo.put("email", element.nextElementSibling().text().trim());
                } else if (element.text().contains("传真：")) {
                    companyInfo.put("fax", element.nextElementSibling().text().trim());
                }
            }

            Elements email = contactInfoParse.select("div.contact-detail dl dt");
            for (Element element : email) {
                if (element.text().contains("邮　　编：")) {
                    companyInfo.put("postcode", element.nextElementSibling().text().trim());
                }
            }

            companyInfo.put("url", url);//链接地址
            companyInfo.put("crawlerId", "70");
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            insertToMySQL(companyInfo);
            if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", MD5Util.getMD5String(name.text().trim()))) {
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
