package parse.product.download;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EhsyProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(EhsyProductDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        JSONArray imgs = new JSONArray();
        ArrayList<String> list = new ArrayList<>();

        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            String name = "企业未知";
            productInfo.put("company_name", name);
            String md5String = MD5Util.getMD5String(name);
            productInfo.put("company_id", md5String);
            productInfo.put("product_name", document.select("div.product-info-detail > h1").text().trim());
            productInfo.put("prices", document.select("div.price.clearfix > div > span")
                    .text().replace("西域价：","").replace("¥","").trim());
            Elements select = document.select("div.product-info-detail-other > div");
            for (Element info : select) {
                if (info.text().contains("品牌型号")) {
                    productInfo.put("product_specifications", info.text().trim().replace("品牌型号 ：", ""));
                } else if (info.text().contains("库存")) {
                    productInfo.put("total_supply", info.text().trim().replace("库存 ：", ""));
                } else if (info.text().contains("最小订货量")) {
                    productInfo.put("total_supply", info.text().trim().replace("最小订货量 ：", ""));
                }
            }
            Elements select1 = document.select("tbody > tr");
            for (Element el : select1) {
                if (el.text().contains("品牌")) {
                    productInfo.put("product_brand", el.text().replace("品牌", ""));
                }
            }

            Elements select2 = document.select("div.tec-description > p > img");
            for (Element element : select2) {
                if (element!=null){
                    imgs.add(element.attr("href"));
                }
                productInfo.put("product_images",imgs.toString());
            }

            Elements tradeParameter = document.select("div.tabContent.js-tabs-content.js-tabs-1 > div > table > tbody > tr.keyValue");
            for (Element element : tradeParameter) {
                list.add(element.text().replace(" ","："));
            }
            productInfo.put("tradeParameter",list.toString());


            productInfo.put("detailUrl", url);
            productInfo.put("crawlerId", "106");
            productInfo.put("createTime", creatrTime.format(new Date()));
            productInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            productInfo.put("@timestamp", timestamp2.format(new Date()));
            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertProduct(productInfo);
//            if (esUtil.writeToES(productInfo, "crawler-product-", "doc", md5String)) {
//                RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//            }
            if (mysqlUtil.insertProduct(productInfo)){
                RedisUtil.insertUrlToSet("catchedUrl-Product",url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
