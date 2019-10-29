package parse.product.download;

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
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JdzjProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(JdzjProductDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        JdzjProductDownload jdzjProductDownload = new JdzjProductDownload();
        jdzjProductDownload.productInfo("https://www.jdzj.com/chanpin/ku1_2927501.html");
    }


    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        JSONArray imgs = new JSONArray();

        productInfo.put("detailUrl", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jdzj");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                productInfo.put("product_name", document.select("div.ProductDel > h1").text().trim());//产品名称，title
                Elements select = document.select("div.price > table > tbody > tr > td:nth-child(2)");
                if (!select.isEmpty()){
                    productInfo.put("prices",select.text().trim());
                }

                Elements select1 = document.select("div.detail-Parameter.clearfix > ul > li");
                for (Element element : select1) {
                    if (element.text().contains("最小起订：")){
                        productInfo.put("mini_order",element.text().replace("最小起订：",""));
                    }else if (element.text().contains("供货总量：")){
                        productInfo.put("total_supply",element.text().replace("供货总量：",""));
                    }else if (element.text().contains("发货地址： ")){
                        productInfo.put("delivery_period",element.text().replace("发货地址：",""));
                    }else if (element.text().contains("发布日期：")){
                        productInfo.put("release_time",element.text().replace("发布日期：",""));
                    }
                }

                String md5String = null;
                Elements companyName = document.select("div.logoTit > h2 > a");
                if (!companyName.isEmpty()){
                    productInfo.put("company_name",companyName.text().trim());
                    md5String = MD5Util.getMD5String(companyName.text().trim());

                }else {
                    Elements elements = document.select("div.m-content.clearfix > div.zcbw > h2 > a");
                    productInfo.put("company_name",elements.text().trim());
                    md5String = MD5Util.getMD5String(elements.text().trim());
                }


                Elements contacts = document.select("div.contactinfo > ul > li > span");
                if (!contacts.isEmpty()){
                    productInfo.put("contacts",contacts.first().text().replace("联系人：","").trim());
                }


                Elements phone = document.select("div.contactinfo > ul > li");
                for (Element element : phone) {
                    if (element.text().contains("手机：")){
                        productInfo.put("contactInformation",element.text().replace("手机：","").trim());
                    }
                }

//                productInfo.put("product_desc", document.select("div.canshu-content").text().trim());//产品详情

                Elements desc = document.select("#pdetail");
                if (!desc.isEmpty()){
                    productInfo.put("product_desc", desc.text().trim());
                    Elements img = document.select("p img");
                    if (!img.isEmpty()) {
                        for (Element element : img) {
                            String attr = element.attr("src");
                            imgs.add(attr);

                        }
                        productInfo.put("product_images", imgs.toString());
                    }
                }



                productInfo.put("company_id",md5String);
                productInfo.put("crawlerId", "136");
                productInfo.put("createTime", creatrTime.format(new Date()));
                productInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                productInfo.put("@timestamp", timestamp2.format(new Date()));
                productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertProduct(productInfo);
//            for (String s : md5Id) {
//                if (esUtil.writeToES(productInfo, "crawler-product-", "doc", s)) {
//                    RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                }
//            }
                if (mysqlUtil.insertProduct(productInfo)){
                    RedisUtil.insertUrlToSet("catchedUrl-Product",url);
                }
            }else {
                LOGGER.info("网页不存在");
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
