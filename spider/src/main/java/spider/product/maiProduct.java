package spider.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.*;


public class maiProduct {
    private final static Logger LOGGER = LoggerFactory.getLogger(maiProduct.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header = null;
    private static final String homepage = "http://www.86mai.com/sell/";
//    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
//    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }
    //首页
    private void maiProduct(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("a.px16");
            for (Element e : select){
                String href = e.attr("href");
                more(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void more(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.m2l > div:nth-child(1) > div.sort-v > ul > li > a");
            for (Element element : elements) {
                String href = element.attr("href");
                mores(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mores(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.m2l > div:nth-child(1) > div.sort-v > ul > li > a");
            for (Element element : elements) {
                String href = element.attr("href");
                paging(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String url) {
        try {
            String replace = url.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("div.pages cite").text().split("/")[1].replace("页","");//获取总结数
            int total = Integer.valueOf(pagesNumber).intValue()+1;//类型转换
            int number = 1;
            for (number = 1; number < total; number++) {
                String link = replace + "_" + number + ".html";//拼接链接地址
                productList(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品列表
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.list table tbody tr td div a");
            for (Element element : elements) {
                String href = element.attr("href");
                productInfo(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject productInfo = new JSONObject();
        try {
            productInfo.put("detailUrl",url);
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            productInfo.put("product_name",parse.select("div.main_head > div > strong").first().text().trim());
            Elements elements = parse.select("td.f_dblue");
            for (Element element : elements) {
                if (element.text().contains("品牌：")){
                    productInfo.put("product_brand",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("型号：")){
                    productInfo.put("product_specifications",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("单价：")){
                    Element trim = element.nextElementSibling();
                    if (trim!=null) {
                        productInfo.put("prices", trim.text().trim());
                    }else {
                        productInfo.put("prices", "面议");
                    }
                }
                if (element.text().contains("起订：")){
                    productInfo.put("mini_order",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("供货总量：")){
                    productInfo.put("total_supply",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("供货总量：")){
                    productInfo.put("total_supply",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("发货期限：")){
                    productInfo.put("delivery_period",element.nextElementSibling().text().trim());
                }
                if (element.text().contains("所在地：")){
                    productInfo.put("delivery_place",element.nextElementSibling().text().trim());
                }
            }
            productInfo.put("product_desc",parse.select("#content").text().trim());
            Elements img = parse.select("div#content.content.c_b p img");
            if (img.size() != 0) {
                for (Element element : img) {
                    imgsList.add(element.attr("src"));
                    productInfo.put("images", imgsList.toString());//图片
                }
            }


            Elements name = parse.select("div.head > div > a > h1");
            if (!name.isEmpty()){
                productInfo.put("company_name",name.text().trim());
                productInfo.put("company_id",MD5Util.getMD5String(name.text().trim()));
            }else {
                productInfo.put("company_name","未知企业");
                productInfo.put("company_id",MD5Util.getMD5String("未知企业"));
            }

            ArrayList<String> list = new ArrayList<>();
            Elements select = parse.select("#side > div > ul > li");
            for (Element element : select) {
                if (element.text().contains("联系人：")){
                    productInfo.put("contacts",element.text().replace("联系人：","").trim());
                }
                if (element.text().contains("手机：")){
                    list.add(element.text().replace("手机：","").trim());
                }else if (element.text().contains("电话：")){
                    list.add(element.text().replace("电话：","").trim());
                }
                productInfo.put("contactInformation",list.toString());
            }

            productInfo.put("crawlerId", "61");
//            productInfo.put("timestamp", timestamp.format(new Date()));
//            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
//            productInfo.put("@timestamp", timestamp2.format(new Date()));
//            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            insert(productInfo);
//            esUtil.writeToES(productInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        maiProduct maiProduct = new maiProduct();
        maiProduct.maiProduct(homepage);
//        LOGGER.info("dzwNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }


    private void insert(JSONObject productInfo) {
        Map = (java.util.Map) productInfo;
        if (updateToMySQL.productUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }


}
