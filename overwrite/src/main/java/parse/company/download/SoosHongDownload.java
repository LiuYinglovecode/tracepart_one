package parse.company.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.company.toRedis.SoosHongToRedis;
import util.ESUtil;
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SoosHongDownload {

    private final static Logger LOGGER = LoggerFactory.getLogger(SoosHongToRedis.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        SoosHongDownload soosHongDownload = new SoosHongDownload();
        soosHongDownload.companyinfo("");
    }

    public void companyinfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "sooshong");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String href = "http://www.sooshong.com" + parse.select("li.cur a").attr("href");
                companyInfo.put("name", parse.select("div.ctitle h1").text().trim());
                companyInfo.put("id", MD5Util.getMD5String(parse.select("div.ctitle h1").text().trim()));
                companyInfo.put("industry", MD5Util.getMD5String(parse.select("div.ctitle h1").text().trim()));
                Elements company_info = parse.select("div.intros table tr td");
                if (company_info.size() != 0) {
                    for (Element element : company_info) {
                        if (element.text().contains("联系人：")) {
                            companyInfo.put("contact", element.nextElementSibling().text());
                        }
                        if (element.text().contains("电话：")) {
                            companyInfo.put("landline", element.nextElementSibling().text());
                        }
                        if (element.text().contains("手机：")) {
                            companyInfo.put("phone", element.nextElementSibling().text());
                        }
                        if (element.text().contains("传真：")) {
                            companyInfo.put("fax", element.nextElementSibling().text());
                        }
                        if (element.text().contains("地址：")) {
                            companyInfo.put("address", element.nextElementSibling().text());
                        }
                        if (element.text().contains("邮编：")) {
                            companyInfo.put("postcode", element.nextElementSibling().text());
                        }
                        if (element.text().contains("公司主页：")) {
                            companyInfo.put("website", element.nextElementSibling().text());
                        }
                    }
                } else {
                    LOGGER.info("网页异常");
                }
                String htmlTwo = HttpUtil.httpGetwithJudgeWord(href, "sooshong");
                Document parseTwo = Jsoup.parse(htmlTwo);
                Elements select1 = parseTwo.select("p.MsoNormal");
                if (select1.size() != 0) {
                    companyInfo.put("company_info", select1.text().trim());
                } else {
                    companyInfo.put("company_info", parseTwo.select(".intros").text().trim());
                }
                Elements select = parseTwo.select("td.S.lh20");
                for (Element element : select) {
                    if (element.text().contains("主营产品或服务：")) {
                        companyInfo.put("main_product", element.nextElementSibling().text());
                    }
                    if (element.text().contains("主营行业：")) {
                        companyInfo.put("industry", element.nextElementSibling().text());
                    }
                    if (element.text().contains("企业类型：")) {
                        companyInfo.put("company_model", element.nextElementSibling().text());
                    }
                    if (element.text().contains("经营模式：")) {
                        companyInfo.put("management_model", element.nextElementSibling().text());
                    }
                    if (element.text().contains("法人代表/负责人：")) {
                        companyInfo.put("incorporator", element.nextElementSibling().text());
                    }
                    if (element.text().contains("公司注册地址：")) {
                        companyInfo.put("register_address", element.nextElementSibling().text());
                    }
                    if (element.text().contains("注册资金：")) {
                        companyInfo.put("register_capital", element.nextElementSibling().text());
                    }
                    if (element.text().contains("员工人数：")) {
                        companyInfo.put("employees", element.nextElementSibling().text());
                    }
                    if (element.text().contains("主要经营地点：")) {
                        companyInfo.put("register_address", element.nextElementSibling().text());
                    }
                    if (element.text().contains("公司成立日期：")) {
                        companyInfo.put("company_register_time", element.nextElementSibling().text());
                    }
                    if (element.text().contains("主要客户：")) {
                        companyInfo.put("company_clients", element.nextElementSibling().text());
                    }
                    if (element.text().contains("管理体系认证：")) {
                        companyInfo.put("qhse", element.nextElementSibling().text());
                    }
                    if (element.text().contains("开户银行：")) {
                        companyInfo.put("open_bank", element.nextElementSibling().text());
                    }
                    if (element.text().contains("研发部门人数：")) {
                        companyInfo.put("research_staff", element.nextElementSibling().text());
                    }
                    if (element.text().contains("厂房面积：")) {
                        companyInfo.put("company_area", element.nextElementSibling().text());
                    }
                    if (element.text().contains("质量控制：")) {
                        companyInfo.put("quality_control", element.nextElementSibling().text());
                    }
                    if (element.text().contains("月产量：")) {
                        companyInfo.put("monthly_production", element.nextElementSibling().text());
                    }
                }

                companyInfo.put("crawlerId", "62");
                companyInfo.put("createTime", creatrTime.format(new Date()));
                companyInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                companyInfo.put("@timestamp", timestamp2.format(new Date()));
                companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertCompany(companyInfo);
//            if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
//                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
//            }
                if (mysqlUtil.insertCompany(companyInfo)) {
                    RedisUtil.insertUrlToSet("catchedUrl-Company", url);
                }
            } else {
                LOGGER.info("网页异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
