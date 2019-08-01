package parse.company.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YellowurlDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void info(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String s = url + "contact/";
            String s1 = url + "introduce/";
            String html = HttpUtil.httpGetwithJudgeWord(s, "黄页网");
            Document document = Jsoup.parse(html);
            Elements name = document.select("html body div.m div.head div h1");
            companyInfo.put("name", name.text());
            String companyId = MD5Util.getMD5String(name.text());
            companyInfo.put("id",companyId);
            Elements elements = document.select("div.px13.lh18 table tbody tr td");
            for (Element el : elements) {
                if (el.text().contains("公司地址：")) {
                    companyInfo.put("address", el.nextElementSibling().text());
                } else if (el.text().contains("公司电话：")) {
                    companyInfo.put("landline", el.nextElementSibling().text());
                } else if (el.text().contains("公司网址：")) {
                    companyInfo.put("website", el.nextElementSibling().text());
                } else if (el.text().contains("联 系 人：")) {
                    companyInfo.put("contact", el.nextElementSibling().text());
                } else if (el.text().contains("手机号码：")) {
                    companyInfo.put("phone", el.nextElementSibling().text());
                }
            }
            String html1 = HttpUtil.httpGetwithJudgeWord(s1, "黄页网");
            Document document1 = Jsoup.parse(html1);
            companyInfo.put("company_info", document1.select("div.px13.lh18 table tbody tr td").eq(0).text().trim());
            Elements elements1 = document1.select("div.px13.lh18 table tbody tr td.f_b");
            for (Element element : elements1) {
                if (element.text().contains("公司类型：")) {
                    companyInfo.put("company_model", element.nextElementSibling().text());
                } else if (element.text().contains("公司规模：")) {
                    companyInfo.put("employees", element.nextElementSibling().text());
                } else if (element.text().contains("注册资本：")) {
                    companyInfo.put("register_capital", element.nextElementSibling().text());
                } else if (element.text().contains("注册年份：")) {
                    companyInfo.put("company_register_time", element.nextElementSibling().text());
                } else if (element.text().contains("主营行业：")) {
                    companyInfo.put("industry", element.nextElementSibling().text());
                }
            }
            companyInfo.put("crawlerId", "8");
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            mysqlUtil.insertCompany(companyInfo);
            if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}