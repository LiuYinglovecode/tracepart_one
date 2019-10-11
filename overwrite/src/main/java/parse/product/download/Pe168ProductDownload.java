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

public class Pe168ProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Pe168ProductDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        ArrayList<String> md5Id = new ArrayList<>();
        JSONArray imgs = new JSONArray();

        productInfo.put("detailUrl", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "公司首页");
            Thread.sleep(SleepUtils.sleepMin());
            if (html!=null) {
                Document document = Jsoup.parse(html);
                productInfo.put("product_name", document.select("div.main_head > div > h1").text().trim());//产品名称，title
                Elements select = document.select("#selldata > dt");
                for (Element element : select) {
                    if (element.text().contains("品牌：")){
                        productInfo.put("product_brand",element.nextElementSibling().text());
                    }else if (element.text().contains("单 价")){
                        productInfo.put("prices",element.nextElementSibling().text());
                    }else if (element.text().contains("最小起订量")){
                        productInfo.put("mini_order",element.nextElementSibling().text());
                    }else if (element.text().contains("供货总量")){
                        productInfo.put("total_supply",element.nextElementSibling().text());
                    }else if (element.text().contains("发货期限")){
                        productInfo.put("delivery_period",element.nextElementSibling().text());
                    }else if (element.text().contains("更新日期")){
                        productInfo.put("release_time",element.nextElementSibling().text().split("有效期至：")[0]);
                    }
                }
                Elements companyName = document.select("#side > div > div > strong");
                for (Element element : companyName) {
                    if (!element.text().contains("产品分类")&&!element.text().contains("站内搜索")&&!element.text().contains("新闻动态")){
                        productInfo.put("company_name",element.text());
                        String md5String = MD5Util.getMD5String(element.text());
                        md5Id.add(md5String);
                        productInfo.put("company_id",md5String);
                    }
                }
                Elements elements = document.select("#side > div.side_body > ul > li");
                for (Element element : elements) {
                    if (element.text().contains("地址：")){
                        productInfo.put("delivery_place",element.text().replace("地址：",""));
                    }else if (element.text().contains("电话：")){
                        productInfo.put("contactInformation",element.text().replace("电话：",""));
                    }else if (element.text().contains("联系人：")){
                        productInfo.put("contacts",element.text().replace("联系人：",""));
                    }
                }

//                productInfo.put("product_desc", document.select("div.canshu-content").text().trim());//产品详情
                Elements img = document.select("#t_0,#t_1,#t_2");
                if (0 != img.size()) {
                    for (Element element : img) {
                        String attr = element.attr("src");
                        if (!attr.contains("nopic60")) {
                            imgs.add(attr);
                        }
                    }
                    productInfo.put("product_images", imgs.toString());
                }
            }else {
                LOGGER.info("网页不存在");
            }
            productInfo.put("crawlerId", "103");
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



        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
