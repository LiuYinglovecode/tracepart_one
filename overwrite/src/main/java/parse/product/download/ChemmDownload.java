package parse.product.download;

import Utils.NewsMd5;
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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChemmDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChemmDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    //产品信息
    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            String product_desc = parse.select("#MaininfoContent").text().trim();
            productInfo.put("product_desc", product_desc);
            productInfo.put("productId", NewsMd5.newsMd5(product_desc));
            Elements select = parse.select("#ProductInfoList ul li");
            for (Element info : select) {
                switch (info.text().split("：")[0]) {
                    case "产品名称":
                        productInfo.put("product_name", info.text().split("：", 2)[1]);
                        break;
                    case "产品编号":
                        productInfo.put("product_number", info.text().split("：", 2)[1]);
                        break;
                    case "产品商标":
                        productInfo.put("product_brand", info.text().split("：", 2)[1]);
                        break;
                    case "产品规格":
                        productInfo.put("product_specifications", info.text().split("：", 2)[1]);
                        break;
                    case "参考价格":
                        productInfo.put("prices", info.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            Elements contact = parse.select("#contactusRight ul li");
            if (contact.size() != 0) {
                for (Element info : contact) {
                    switch (info.select(".contactusRightTitle").text().trim()) {
                        case "公司名称：":
                            productInfo.put("company_name", info.select("span.contactusRightInfo a.bLinkFont").text().trim());
                            productInfo.put("company_id", MD5Util.getMD5String(info.select("span.contactusRightInfo a.bLinkFont").text().trim()));
                            break;
                        case "联 系 人：":
                            productInfo.put("contacts", info.select("span.contactusRightInfo").text().trim());
                            break;
                        case "手　　机：":
                            productInfo.put("contactInformation", info.select("span.contactusRightInfo").text().trim());
                            break;
                        case "主　　页：":
                            productInfo.put("detailUrl", info.select("span.contactusRightInfo a").attr("href"));
                            break;
                        default:
                    }
                }
            } else {
                Elements contact1 = parse.select("#contactus2Right ul li");
                for (Element info1 : contact1) {
                    switch (info1.select(".contactusRightTitle").text().trim()) {
                        case "公司名称：":
                            productInfo.put("company_name", info1.select("span.contactusRightInfo a.bLinkFont").text().trim());
                            productInfo.put("company_id", MD5Util.getMD5String(info1.select("span.contactusRightInfo a.bLinkFont").text().trim()));
                            break;
                        case "联 系 人：":
                            productInfo.put("contacts", info1.select("span.contactusRightInfo").text().trim());
                            break;
                        case "手　　机：":
                            productInfo.put("contactInformation", info1.select("span.contactusRightInfo").text().trim());
                            break;
                        case "主　　页：":
                            productInfo.put("detailUrl", info1.select("span.contactusRightInfo a").attr("href"));
                            break;
                        default:
                    }
                }
            }
            productInfo.put("crawlerId", "47");
            productInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            productInfo.put("@timestamp", timestamp2.format(new Date()));
            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            if (esUtil.writeToES(productInfo, "crawler-product-", "doc", NewsMd5.newsMd5(product_desc))) {
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
