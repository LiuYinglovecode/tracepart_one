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

public class Net114Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //企业信息
    public void companyInfo(String url) {


        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "net114");
            Document document = Jsoup.parse(html);
            Elements page = document.select("ul.m li.i,ul.nav_list li");
            for (Element element : page) {
                if (element.text().contains("企业介绍")) {
                    String link = element.select("a").attr("href");
                    System.out.println(element.attr("href"));
                    String html1 = HttpUtil.httpGetwithJudgeWord(link, "net114");
                    Document document1 = Jsoup.parse(html1);
                    companyInfo.put("company_info", document1.select("div.p_10.about_div p").text().trim());
                    Elements elements = document1.select("table.table_div tbody tr td");
                    for (Element ele : elements) {
                        if (ele.text().contains("主营行业")) {
                            companyInfo.put("industry", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("公司类型")) {
                            companyInfo.put("company_model", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("注册资本")) {
                            companyInfo.put("register_capital", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("成立时间")) {
                            companyInfo.put("company_register_time", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("年营业额：")) {
                            companyInfo.put("company_turnover", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("法定代表人/负责人：")) {
                            companyInfo.put("incorporator", ele.nextElementSibling().text().trim());
                        } else if (ele.text().contains("员工人数：")) {
                            companyInfo.put("employees", ele.nextElementSibling().text().trim());
                        }
                    }
                } else if (element.text().contains("联系地址") || element.text().contains("联系方式") || element.text().contains("联系我们")) {
                    String link = element.select("a").attr("href");
                    String html2 = HttpUtil.httpGetwithJudgeWord(link, "net114");
                    Document document2 = Jsoup.parse(html2);
                    Elements info = document2.select("table.table tbody tr td p");
                    for (Element el : info) {
                        if (el.text().contains("手机：")) {
                            companyInfo.put("phone", el.text().trim().replace("手机：", ""));
                        } else if (el.text().contains("联系电话：")) {
                            companyInfo.put("landline", el.text().trim().replace("联系电话：", ""));
                        } else if (el.text().contains("地址：")) {
                            companyInfo.put("address", el.text().trim().replace("地址：", ""));
                        } else if (el.text().contains("传真：")) {
                            companyInfo.put("fax", el.text().trim().replace("传真：", ""));
                        } else if (el.text().contains("联系人：")) {
                            companyInfo.put("contact", el.text().trim().replace("联系人：", ""));
                        }
                    }
                    Elements name = document2.select("table.table tbody tr td p").eq(0);
                    companyInfo.put("name", name.text().replace("公司名称：", ""));
                    String companyId = MD5Util.getMD5String(name.text().replace("公司名称：", ""));
                    companyInfo.put("id", companyId);


                    companyInfo.put("url", url);//链接地址
                    companyInfo.put("crawlerId", "9");
                    companyInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    companyInfo.put("@timestamp", timestamp2.format(new Date()));
                    companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertCompany(companyInfo);
                    if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
                        RedisUtil.insertUrlToSet("catchedUrl-Company", url);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
