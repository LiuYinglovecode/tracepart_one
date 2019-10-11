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

public class ShopCompanyDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void info(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String ch2 = url + "/ch2";
            String ch6 = url + "/ch6";
            String memberCredit = url.replace("com/","com/memberCredit/");
            String html = HttpUtil.httpGetwithJudgeWord(ch2, "99114");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            Elements name = document.select("div.sj-line.sjDiv1 > p > span > a");
            companyInfo.put("name", name.text());
            String companyId = MD5Util.getMD5String(name.text());
            companyInfo.put("id",companyId);
            Elements elements = document.select("div.sj-line.sjDiv2 > p,div.addIntro_div.clearfix > div > ul > li,li.picContact.clearfix > div > p");
            for (Element el : elements) {
                if (el.text().contains("联系卖家：")) {
                    companyInfo.put("contact", el.text().replace("联系卖家：",""));
                } else if (el.text().contains("经营模式：")) {
                    companyInfo.put("management_model", el.text().replace("经营模式：",""));
                }else if (el.text().contains("主营业务：")) {
                    companyInfo.put("industry", el.text().replace("主营业务：",""));
                }else if (el.text().contains("手 机：")) {
                    companyInfo.put("phone", el.text().replace("手 机：",""));
                }else if (el.text().contains("联系我：")) {
                    companyInfo.put("qq", el.select("a").attr("title").replace("联系人QQ:",""));
                } else if (el.text().contains("电 话：")) {
                    companyInfo.put("landline", el.text().replace("电 话：",""));
                } else if (el.text().contains("传 真：")) {
                    companyInfo.put("fax", el.text().replace("传 真：",""));
                } else if (el.text().contains("地 址：")) {
                    companyInfo.put("address", el.text().replace("地 址：",""));
                } else if (el.text().contains("邮 箱：")) {
                    companyInfo.put("email", el.text().replace("邮 箱：",""));
                }
            }
            String html1 = HttpUtil.httpGetwithJudgeWord(ch6, "99114");
            Thread.sleep(SleepUtils.sleepMin());
            Document info = Jsoup.parse(html1);
            Elements select = info.select("div.jspPane > p");
            if (0!=select.size()){
                companyInfo.put("company_info",select.text());
            }

            String html2 = HttpUtil.httpGetwithJudgeWord(memberCredit, "99114");
            Thread.sleep(SleepUtils.sleepMin());
            Document info1 = Jsoup.parse(html2);
            Elements select1 = info1.select("div.manage.clearfix");
            if (null!=select1){
                for (Element element : select1) {
                    if (element.text().contains("注册资本：")){
                        companyInfo.put("register_capital", element.text().replace("注册资本：",""));
                    }else if (element.text().contains("注册号：")){
                        companyInfo.put("from_where_table_id", element.text().replace("注册号：",""));
                    }else if (element.text().contains("企业类型：")){
                        companyInfo.put("company_model", element.text().replace("企业类型：",""));
                    }else if (element.text().contains("经营范围：")){
                        companyInfo.put("industry", element.text().replace("经营范围：",""));
                    }else if (element.text().contains("经营范围：")){
                        companyInfo.put("industry", element.text().replace("经营范围：",""));
                    }else if (element.text().contains("法定代表人：")){
                        companyInfo.put("incorporator", element.text().replace("法定代表人：",""));
                    }else if (element.text().contains("注册地址：")){
                        companyInfo.put("register_address", element.text().replace("注册地址：",""));
                    }else if (element.text().contains("成立日期：")){
                        companyInfo.put("company_register_time", element.text().replace("成立日期：",""));
                    }
                }

            }

            companyInfo.put("crawlerId", "100");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertCompany(companyInfo);
//            if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
//                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
//            }
            if (mysqlUtil.insertCompany(companyInfo)){
                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
