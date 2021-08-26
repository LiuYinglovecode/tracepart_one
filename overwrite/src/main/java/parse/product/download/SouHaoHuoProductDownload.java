package parse.product.download;

import Utils.HttpUtil;
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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SouHaoHuoProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Wmb2bDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static void main(String[] args) {
        SouHaoHuoProductDownload souHaoHuoProductDownload = new SouHaoHuoProductDownload();
        souHaoHuoProductDownload.productInfo("https://www.912688.com/supply/318563203.html");
    }

    //产品信息
    public void productInfo(String url) {
        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "912688");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document parse = Jsoup.parse(html);
                String title = parse.select("div.main-data-param.R > h1").text().trim();
                if (null != title) {
                    productInfo.put("product_name", title);//标题
                    //价格、起订量、可售量
                    Elements elements = parse.select("div.main-data-param.R > table > tbody > tr");
                    if (elements.size() != 0) {
                        for (Element element : elements) {
                            if (element.text().contains("格")) {
                                productInfo.put("prices", element.text().split("格")[1].replace("¥", "").trim());//价格
                            } else if (element.text().contains("起订量")) {
                                productInfo.put("mini_order", element.text()
                                        .replace("起订量", "")
                                        .replace("≥","")
                                        .replace("件","")
                                        .replace("个","")
                                        .replace("台","")
                                        .replace("张","")
                                        .replace("套","")
                                        .replace("pcs","")
                                        .replace("把","")
                                        .replace("千克","")
                                        .replace("部","")
                                        .replace("米","")
                                        .replace("K","")
                                        .trim());//品牌

                            } else if (element.text().contains("可售量")) {
                                productInfo.put("total_supply", element.text()
                                        .replace("可售量", "")
                                        .replace("台","")
                                        .replace("件","")
                                        .replace("个","")
                                        .replace("张","")
                                        .replace("套","")
                                        .replace("pcs","")
                                        .replace("把","")
                                        .replace("千克","")
                                        .replace("部","")
                                        .replace("米","")
                                        .replace("K","")
                                        .trim());//可售量
                            }
                        }
                    }

                    //品牌
                    Elements select = parse.select("div.three-con > table > tbody > tr > td");
                    if (!select.isEmpty()){
                        for (Element element : select) {
                            if (element.text().contains("品牌")){
                                productInfo.put("product_brand",element.nextElementSibling().text().trim());
                            }
                        }
                    }

                    /**
                     * 产品图片
                     */
                    Elements img = parse.select("li > img.prod-pic-list,#prodDetailDiv > p > img,#prodDetailDiv > p > span > img");
                    if (0 != img.size()) {
                        for (Element element : img) {
                            if (!element.attr("src").contains("nopic60")) {
                                imgsList.add(element.attr("src"));
                            }
                        }
                        productInfo.put("product_images", imgsList.toString());
                    }

                    /**
                     * 产品信息
                     */
                    Elements productDesc = parse.select("#prodDetailDiv");
                    if (0 != productDesc.size()) {
                        productInfo.put("product_desc", productDesc.text().trim());
                    }

                    /**
                     * 公司名
                     */
                    String nameId = null;
                    Elements name = parse.select("div > ul > li > a.com-name.b2b-statics");
                    if (0 != name.size()) {
                        String trim = name.text().trim();
                        productInfo.put("company_name", trim);
                        nameId = MD5Util.getMD5String(trim);
                    } else {
                        String s = "企业未知";
                        productInfo.put("company_name", s);
                        nameId = MD5Util.getMD5String(s);
                    }
                    Elements select1 = parse.select("#bot-nav > div.member.act > div > ul > li > span.name");
                    for (Element element : select1) {
                        if (element.text().contains("联系姓名")){
                            productInfo.put("contacts",element.nextElementSibling().text().trim());
                        }else if (element.text().contains("电话号码：")){
                            productInfo.put("contactInformation",element.nextElementSibling().text().trim());
                        }else if (element.text().contains("所在地区：")){
                            productInfo.put("production_place",element.nextElementSibling().text().trim());
                        }
                    }
                    productInfo.put("company_id", nameId);



                    productInfo.put("crawlerId", "146");
                    productInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    productInfo.put("@timestamp", timestamp2.format(new Date()));
                    productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertProduct(productInfo);
//                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", nameId)) {
//                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                    }
                    if (mysqlUtil.insertProduct(productInfo)){
                        RedisUtil.insertUrlToSet("catchedUrl-Product",url);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
