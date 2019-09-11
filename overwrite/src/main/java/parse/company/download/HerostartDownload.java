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

public class HerostartDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(HerostartDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void info(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String companyId = null;
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            Thread.sleep(SleepUtils.sleepMin());
            if (null!=html) {
                Document document = Jsoup.parse(html);
                Elements name = document.select("div.left_box > h1");
                if (null != name) {
                    companyInfo.put("name", name.text());
                    companyId = MD5Util.getMD5String(name.text());
                }

                Elements info = document.select("div.lh18.px13.pd10");
                if (0 != info.size()) {
                    companyInfo.put("company_info", info.text().trim());
                }

                Elements elements = document.select("div:nth-child(7) > ul > li");
                for (Element el : elements) {
                    if (el.text().contains("地址：")) {
                        companyInfo.put("address", el.text().replace("地址：", "").trim());
                    } else if (el.text().contains("电话：")) {
                        companyInfo.put("landline", el.text().replace("电话：", "").trim());
                    } else if (el.text().contains("商铺：")) {
                        companyInfo.put("website", el.text().replace("商铺：", "").trim());
                    } else if (el.text().contains("联系人：")) {
                        companyInfo.put("contact", el.text().replace("联系人：", "").trim());
                    } else if (el.text().contains("手机：")) {
                        companyInfo.put("phone", el.text().replace("手机：", "").trim());
                    } else if (el.text().contains("传真：")) {
                        companyInfo.put("fax", el.text().replace("传真：", "").trim());
                    } else if (el.text().contains("邮件：")) {
                        companyInfo.put("emali", el.text().replace("邮件：", "").trim());
                    }
                }

                Elements elements1 = document.select("div.lh18 table tbody tr td");
                for (Element element : elements1) {
                    if (element.text().contains("公司类型：")) {
                        companyInfo.put("company_model", element.nextElementSibling().text());
                    } else if (element.text().contains("注册资本：")) {
                        companyInfo.put("register_capital", element.nextElementSibling().text());
                    } else if (element.text().contains("注册年份：")) {
                        companyInfo.put("company_register_time", element.nextElementSibling().text());
                    } else if (element.text().contains("法人代表：")) {
                        companyInfo.put("incorporator", element.nextElementSibling().text());
                    } else if (element.text().contains("营业执照号码：")) {
                        companyInfo.put("from_where_table_id", element.nextElementSibling().text());
                    } else if (element.text().contains("经营模式：")) {
                        companyInfo.put("management_model", element.nextElementSibling().text());
                    } else if (element.text().contains("经营模式：")) {
                        companyInfo.put("management_model", element.nextElementSibling().text());
                    } else if (element.text().contains("经营范围：")) {
                        companyInfo.put("industry", element.nextElementSibling().text());
                    } else if (element.text().contains("销售的产品：")) {
                        companyInfo.put("main_product", element.nextElementSibling().text());
                    } else if (element.text().contains("公司规模：")) {
                        companyInfo.put("employees", element.nextElementSibling().text());
                    }
                }

                companyInfo.put("id", companyId);
                companyInfo.put("crawlerId", "114");
                companyInfo.put("createTime", creatrTime.format(new Date()));
                companyInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                companyInfo.put("@timestamp", timestamp2.format(new Date()));
                companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertCompany(companyInfo);
                if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", companyId)) {
                    RedisUtil.insertUrlToSet("catchedUrl-Company", url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
