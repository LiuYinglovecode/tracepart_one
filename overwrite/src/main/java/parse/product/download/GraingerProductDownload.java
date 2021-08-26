package parse.product.download;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

public class GraingerProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(EhsyProductDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        JSONArray imgs = new JSONArray();

        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
//          ************************************ 企业名称 ************************************
            String name = "企业未知";
            productInfo.put("company_name", name);
            String md5String = MD5Util.getMD5String(name);
            productInfo.put("company_id", md5String);
            productInfo.put("product_name", document.select("div.proDetailCon > h3 > a").text().trim());
//          ************************************ 价格、图片、介绍 ************************************
            productInfo.put("prices", document.select(".price").text().replace("价格: ¥","")
                    .replace("价格： ¥","").replace("¥",""));

            Elements brand = document.select("div.proDetailCon > p > font > a,div.proDetailCon > div > font > a");
            if (0!=brand.size()) {
                productInfo.put("product_brand", brand.text().trim());
            }
            Elements deliveryPeriod = document.select("font.brNone span.shipping");
            if (0!=deliveryPeriod.size()) {
                productInfo.put("delivery_period", deliveryPeriod.text().trim());
            }

            Elements select2 = document.select("div.proDetailDiv div div div.box,div.proDetailDiv div div.box");
            if (0!=select2.size()){
                Element first = select2.first();
                productInfo.put("product_introduce",first.text());

            }
            Elements select = document.select("div.box.sku-picture > img,div.box.sku-picture > img,div.proDetailDiv div div div.box img");
            if (0!=select.size()){
                for (Element element : select) {
                    String attr = element.attr("src");
                    if (attr.contains("http:")) {
                        imgs.add(attr);
                    }else {
                        String s = "http:" + attr;
                        imgs.add(s);
                    }
                }
                productInfo.put("product_images",imgs.toString());
            }
            productInfo.put("detailUrl", url);
            productInfo.put("crawlerId", "26");
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
