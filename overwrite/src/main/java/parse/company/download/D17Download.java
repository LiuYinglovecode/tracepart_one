package parse.company.download;

import Utils.HttpUtil;
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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class D17Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(D17Download.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        D17Download d17Download = new D17Download();
        d17Download.detail("https://flora1.d17.cc/introduce.html");
    }

    public void detail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String nameMD5 = "";
            String html = HttpUtil.httpGetWithProxy(url, "关于我们");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            companyInfo.put("company_info", document.select(".item").text().trim());
            Elements infoList = document.select(".tps_con_mode.pagein_information .information.clr .clr li");
            for (Element e : infoList) {
                String key = e.text().split("：", 2)[0];
                switch (key) {
                    case "公司名称":
                        companyInfo.put("name", e.text().split("：", 2)[1]);
                        nameMD5 = MD5Util.getMD5String(e.text().split("：", 2)[1]);
                        companyInfo.put("id", nameMD5);
                        break;
                    case "联系人":
                        companyInfo.put("contact", e.text().split("：", 2)[1]);
                        break;
                    case "联系手机":
                        companyInfo.put("phone", e.text().split("：", 2)[1]);
                        break;
                    case "QQ":
                        companyInfo.put("qq", e.text().split("：", 2)[1]);
                        break;
                    case "邮箱":
                        companyInfo.put("email", e.text().split("：", 2)[1]);
                        break;
                    case "固定电话":
                        companyInfo.put("landline", e.text().split("：", 2)[1]);
                        break;
                    case "公司传真":
                        companyInfo.put("fax", e.text().split("：", 2)[1]);
                        break;
                    case "公司地址":
                        companyInfo.put("address", e.text().split("：", 2)[1]);
                        break;
                    case "邮政编码":
                        companyInfo.put("postcode", e.text().split("：", 2)[1]);
                        break;
                    case "公司网址":
                        companyInfo.put("website", e.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            companyInfo.put("crawlerId", "2");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            if (mysqlUtil.insertCompany(companyInfo)) {
                RedisUtil.insertUrlToSet("catchedUrl-Company", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
