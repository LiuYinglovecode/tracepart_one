package parse.company.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.company.toRedis.JdzjToRedis;
import util.ESUtil;
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JdzjDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdzjDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        JdzjDownload jdzjDownload = new JdzjDownload();
        jdzjDownload.companyInfo("http://jinling123.jdzj.com/");
    }

    //企业信息
    public void companyInfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url.concat("aboutus.html"), "jdzj");
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div.fl > table > tbody > tr > td");
            String id = null;
            for (Element element : elements) {
                switch (element.text()) {
                    case "商铺名称:":
                        if (!element.nextElementSibling().nextElementSibling().text().isEmpty()) {
                            companyInfo.put("name", element.nextElementSibling().nextElementSibling().text().trim());
                            id = MD5Util.getMD5String(element.nextElementSibling().nextElementSibling().text().trim());

                        }
                        break;
                    case "主营产品或服务:":
                        if (!element.nextElementSibling().nextElementSibling().text().isEmpty()) {
                            companyInfo.put("main_product", element.nextElementSibling().nextElementSibling().text().trim());
                        }
                        break;
                    case "经营模式:":
                        if (!element.nextElementSibling().nextElementSibling().text().isEmpty()) {
                            companyInfo.put("management_model", element.nextElementSibling().nextElementSibling().text().trim());
                        }
                        break;
                    case "所属行业:":
                        if (!element.nextElementSibling().nextElementSibling().text().isEmpty()) {
                            companyInfo.put("industry", element.nextElementSibling().nextElementSibling().text().trim());
                        }
                        break;
                }
            }


            Elements select = document.select("div.content > ul > li > p");
            for (Element element : select) {
                if (element.text().contains("手 机：")){
                    companyInfo.put("phone",element.text().replace("手 机：","").trim());
                }
            }

            Elements select1 = document.select("div.botomtab > ul > li > span");
            for (Element element : select1) {
                if (element.text().contains("地址：")){
                    companyInfo.put("address",element.text().replace("地址：","").trim());
                }else if (element.text().contains("联系人")){
                    companyInfo.put("contact",element.text().replace("联系人","").trim());
                }
            }

            String info = document.select("div.detail.fl.mt10 > div:nth-child(5)").text();
            if (!info.isEmpty()){
                companyInfo.put("company_info",info.trim());
            }
            companyInfo.put("id",id);


            companyInfo.put("website", url);//链接地址
            companyInfo.put("crawlerId", "135");
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
                RedisUtil.insertUrlToSet("catchedUrl-Company",url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
