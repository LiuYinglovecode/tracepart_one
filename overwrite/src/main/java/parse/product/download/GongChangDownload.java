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
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GongChangDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(GongChangDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);


    //产品信息
    public void productInfo(String url) {
        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "gongchang");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document parse = Jsoup.parse(html);
                String title = parse.select("#title").text().trim();
                if (!title.isEmpty()) {
                    productInfo.put("product_name", title);//标题
                    /**
                     * prices:价格
                     */
                    Elements prices = parse.select("ul.price_ul > li > p.s");
                    if (!prices.isEmpty()) {
                        productInfo.put("prices",prices.text().replace("￥","").trim());
                    }else {
                        productInfo.put("prices","面议");
                    }

                    /**
                     * miniOrder：最小起订量
                     */
                    Elements miniOrder = parse.select("ul.price_ul > li > p.ft-14");
                    if (!miniOrder.isEmpty()){
                        productInfo.put("mini_order",miniOrder.text().trim());
                    }

                    /**
                     * 发货地址、发货期限、供货总量、联系方式
                     */
                    Elements select = parse.select("ul.product_message > li");
                    if (!select.isEmpty()){
                        for (Element element : select) {
                            if (element.text().contains("发货地址")){
                                productInfo.put("delivery_place",element.text().replace("发货地址","").trim());
                            }else if (element.text().contains("发货期限")){
                                productInfo.put("delivery_period",element.text().replace("发货期限","").trim());
                            }else if (element.text().contains("供货总量")){
                                productInfo.put("total_supply",element.text().replace("供货总量","").trim());
                            }else if (element.text().contains("联系方式")){
                                productInfo.put("contactInformation",element.select("div.link-item.link-phone > p.kind").text().trim());
                                productInfo.put("contacts",element.select("div.link-item.link-phone > p.kind").text().trim());
                            }
                        }
                    }

                    /**
                     * 产品介绍
                     */
                    Elements elements = parse.select("#content");
                    if (!elements.isEmpty()){
                        productInfo.put("product_desc",elements.text().trim());
                    }

                    /**
                     * 产品图片
                     */
                    Elements img = parse.select("p > span > span > img,p > strong > img,div.product_content > p > img,div.product_content > img,div.product_content > table > tbody > tr > td > img,p > strong > img,p > span > span > img,td > font > img,#t_0 > div > img,#t_1 > div > img,#t_2 > div > img");
                    if (!img.isEmpty()){
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                        }
                        productInfo.put("product_images",imgsList.toString());
                    }

                    /**
                     * 公司名
                     */
                    String nameId = null;
                    Elements name = parse.select("div.h1_box > div > div > a");
                    if (!name.isEmpty()) {
                        String trim = name.text().trim();
                        productInfo.put("company_name", trim);
                        nameId = MD5Util.getMD5String(trim);
                    } else {
                        Elements name1 = parse.select(",div.column_xx > p > a");
                        productInfo.put("company_name", name1.text().trim());
                        nameId = MD5Util.getMD5String(name1.text().trim());
                    }

                    productInfo.put("company_id", nameId);

                    productInfo.put("crawlerId", "116");
                    productInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    productInfo.put("@timestamp", timestamp2.format(new Date()));
                    productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertProduct(productInfo);
//                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", nameId)) {
//                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                    }
                    if (mysqlUtil.insertProduct(productInfo)){
                        RedisUtil.insertUrlToSet("catchedUrl-GongChangProduct",url);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
