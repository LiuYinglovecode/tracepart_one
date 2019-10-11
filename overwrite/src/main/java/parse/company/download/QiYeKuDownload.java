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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class QiYeKuDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiYeKuDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void companyInfo(String url) {
        JSONObject companyInfo = new JSONObject();
        ArrayList<String> list = new ArrayList<>();
        try {

            String html = HttpUtil.httpGetwithJudgeWord(url, "企业库");
            Thread.sleep(SleepUtils.sleepMin());
            if (null!=html) {
                Document document = Jsoup.parse(html);
                companyInfo.put("company_info", document.select("#m_page > table > tbody > tr > td > div > p").text().trim());
                Elements name = document.select("#m_page > div > h1");
//                ============================================================================================================================================
                if (0!=name.size()) {
                    String companyId = MD5Util.getMD5String(name.text());
                    list.add(companyId);
                    companyInfo.put("name", name.text());
                    companyInfo.put("id", companyId);
                }
//                ============================================================================================================================================

                Elements address = document.select("#m_page > div > div");
                if (0!=address.size()&&address.text().contains("地址：")){
                    companyInfo.put("address", address.text().split("地址：")[1]);
                }
//                ============================================================================================================================================

                Elements elements = document.select("#company_table > tbody > tr");
                for (Element ele : elements) {
                    if (ele.text().contains("法人：")) {
                        companyInfo.put("incorporator", ele.text().replace("法人：", ""));
                        companyInfo.put("contact", ele.text().replace("法人：", ""));
                    } else if (ele.text().contains("电话：")) {
                        companyInfo.put("landline", ele.text().replace("电话：", ""));
                    } else if (ele.text().contains("传真：")) {
                        companyInfo.put("fax", ele.text().replace("传真：", ""));
                    } else if (ele.text().contains("邮编：")) {
                        companyInfo.put("postcode", ele.text().replace("邮编：", ""));
                    } else if (ele.text().contains("邮箱：")) {
                        companyInfo.put("email", ele.text().replace("邮箱：", ""));
                    } else if (ele.text().contains("注册金额：")) {
                        companyInfo.put("register_capital", ele.text().replace("注册金额：", ""));
                    } else if (ele.text().contains("主营产品：")) {
                        companyInfo.put("main_product", ele.text().replace("主营产品：", ""));
                    } else if (ele.text().contains("职工人数：")) {
                        companyInfo.put("employees", ele.text().replace("职工人数：", ""));
                    } else if (ele.text().contains("成立时间：")) {
                        companyInfo.put("company_register_time", ele.text().replace("成立时间：", ""));
                    } else if (ele.text().contains("公司网址：")) {
                        companyInfo.put("company_register_time", ele.text().replace("公司网址：", ""));
                    } else if (ele.text().contains("行业/区域：")) {
                        companyInfo.put("industry", ele.text().replace("行业/区域：", "").split(" ")[0]);
                    }
                }
//                ============================================================================================================================================

                companyInfo.put("url", url);//链接地址
                companyInfo.put("crawlerId", "102");
                companyInfo.put("createTime", creatrTime.format(new Date()));
                companyInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                companyInfo.put("@timestamp", timestamp2.format(new Date()));
                companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            }
//            System.out.println(companyInfo);
//            mysqlUtil.insertCompany(companyInfo);
//            for (String s : list) {
//                if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", s)) {
//                    RedisUtil.insertUrlToSet("catchedUrl-Company", url);
//                }
//            }
            if (mysqlUtil.insertCompany(companyInfo)){
                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
