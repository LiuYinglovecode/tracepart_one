package parse.product.download;

import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
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

public class MaiProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Wmb2bDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static void main(String[] args) {
        MaiProductDownload maiProductDownload = new MaiProductDownload();
        maiProductDownload.productInfo("http://bochuangjixie.86mai.com/sell/itemid-15372082.shtml");
    }

    //产品信息
    public void productInfo(String url) {

        JSONArray imgsList = new JSONArray();
        JSONObject productInfo = new JSONObject();
        try {
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "网站首页");
            Document parse = Jsoup.parse(html);
            productInfo.put("product_name", parse.select("div.main_head > div > strong").first().text().trim());
            Elements elements = parse.select("td.f_dblue");
            for (Element element : elements) {
                if (element.text().contains("品牌：")) {
                    productInfo.put("product_brand", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("型号：")) {
                    productInfo.put("product_specifications", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("单价：")) {
                    Element trim = element.nextElementSibling();
                    if (trim != null) {
                        productInfo.put("prices", trim.text().trim());
                    } else {
                        productInfo.put("prices", "面议");
                    }
                }
                if (element.text().contains("起订：")) {
                    productInfo.put("mini_order", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("供货总量：")) {
                    productInfo.put("total_supply", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("供货总量：")) {
                    productInfo.put("total_supply", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("发货期限：")) {
                    productInfo.put("delivery_period", element.nextElementSibling().text().trim());
                }
                if (element.text().contains("所在地：")) {
                    productInfo.put("delivery_place", element.nextElementSibling().text().trim());
                }
            }
            productInfo.put("product_desc", parse.select("#content").text().trim());
            Elements img = parse.select("div#content.content.c_b p img,#mid_pic");
            if (img.size() != 0) {
                for (Element element : img) {
                    if (!element.attr("href").contains("nopic320")) {
                        imgsList.add(element.attr("src"));
                        productInfo.put("images", imgsList.toString());//图片
                    }
                }
            }


            Elements name = parse.select("div.head > div > a > h1");
            if (!name.isEmpty()) {
                productInfo.put("company_name", name.text().trim());
                productInfo.put("company_id", MD5Util.getMD5String(name.text().trim()));
            } else {
                productInfo.put("company_name", "未知企业");
                productInfo.put("company_id", MD5Util.getMD5String("未知企业"));
            }

            ArrayList<String> list = new ArrayList<>();
            Elements select = parse.select("#side > div > ul > li");
            for (Element element : select) {
                if (element.text().contains("联系人：")) {
                    productInfo.put("contacts", element.text().replace("联系人：", "").trim());
                }
                if (element.text().contains("手机：")) {
                    list.add(element.text().replace("手机：", "").trim());
                } else if (element.text().contains("电话：")) {
                    list.add(element.text().replace("电话：", "").trim());
                }
                productInfo.put("contactInformation", list.toString());
            }

            productInfo.put("crawlerId", "61");
            productInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            productInfo.put("@timestamp", timestamp2.format(new Date()));
            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertProduct(productInfo);
//                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", nameId)) {
//                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                    }
            if (mysqlUtil.insertProduct(productInfo)) {
                RedisUtil.insertUrlToSet("catchedUrl-Product", url);
            }


        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }
    }
}
