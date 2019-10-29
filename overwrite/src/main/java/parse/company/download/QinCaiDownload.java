package parse.company.download;

import Utils.RedisUtil;
import Utils.SleepUtils;
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

public class QinCaiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QinCaiDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    //企业信息
    public void companyInfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Document document = Jsoup.parse(html);
            Elements elements = document.select("span.general-name,span.contact-name");
            for (Element element : elements) {
                switch (element.text()) {
                    case "法人代表":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("incorporator", element.nextElementSibling().text());
                        }
                        break;
                    case "注册资本":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("register_capital", element.nextElementSibling().text());
                        }
                        break;
                    case "经营模式":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("management_model", element.nextElementSibling().text());
                        }
                        break;
                    case "员工数量":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("employees", element.nextElementSibling().text());
                        }
                        break;
                    case "所属行业":
                        companyInfo.put("industry", element.nextElementSibling().text());
                        break;
                    case "主要市场":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("sell_area", element.nextElementSibling().text());
                        }
                        break;
                    case "产品信息":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("main_product", element.nextElementSibling().text());
                        }
                        break;
                    case "联系人":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("contact", element.nextElementSibling().text());
                        }
                        break;
                    case "网站":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("website", element.nextElementSibling().text());
                        }
                        break;
                    case "邮编":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("postcode", element.nextElementSibling().text());
                        }
                        break;
                    case "地址":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("address", element.nextElementSibling().text());
                        }
                        break;
                    case "电话":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("landline", element.nextElementSibling().text());
                        }
                        break;
                    case "传真":
                        if (!element.nextElementSibling().text().isEmpty()) {
                            companyInfo.put("fax", element.nextElementSibling().text());
                        }
                        break;
                }
            }

            String id = null;
            String name = document.select("div.leftunit > h1").text();
            if (!name.isEmpty()){
                companyInfo.put("name",name);
                id = MD5Util.getMD5String(name);
            }

            String info = document.select("div.intro-item").text();
            if (!info.isEmpty()){
                companyInfo.put("company_info",info);
            }
            companyInfo.put("id",id);


            companyInfo.put("website", url);//链接地址
            companyInfo.put("crawlerId", "115");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            System.out.println(companyInfo);

//            if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
//                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
//            }
            if (mysqlUtil.insertCompany(companyInfo)) {
                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
