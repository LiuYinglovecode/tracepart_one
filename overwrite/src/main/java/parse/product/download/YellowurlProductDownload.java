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
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YellowurlProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(YellowurlProductDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);


    //产品信息
    public void productInfo(String url) {
        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "黄页网");
            if (null != html) {
                Document parse = Jsoup.parse(html);
                productInfo.put("product_name", parse.select("h1.clearfix").text().trim());//标题
                Elements elements = parse.select("ul.proIntroBase li");
                if (elements.size() != 0) {
                    for (Element element : elements) {
                        if (element.text().contains("价格：")) {
                            productInfo.put("prices", element.text().replace("价格：", ""));//价格
                        } else if (element.text().contains("起订量：")) {
                            productInfo.put("mini_order", element.text().replace("起订量：", ""));
                        } else if (element.text().contains("可售数量：")) {
                            productInfo.put("total_supply", element.text().replace("可售数量：", ""));//最小采购量
                        }
                    }
                }
                Elements img = parse.select("li.current a img");
                if (0 != img.size()) {
                    for (Element imgs : img) {
                        imgsList.add(imgs.attr("src"));
                    }
                    productInfo.put("product_images",imgsList.toString());
                }
                productInfo.put("contacts",parse.select("div.proContactName p strong").text().trim());
                String first = parse.select("div.pcCWCompanyName,p.cName a strong").first().text();
                productInfo.put("product_name",first);
                String md5String = MD5Util.getMD5String(first);
                productInfo.put("company_id",md5String);
                Elements select = parse.select("div.pcCWCompanyA,div.pcCWCompanyT");
                for (Element element : select) {
                    if (element.text().contains("地址：")){
                        productInfo.put("production_place",element.text().replace("地址：",""));
                    } else if (element.text().contains("电话：")){
                        productInfo.put("contactInformation",element.text().replace("电话：",""));
                    }
                }
                Elements select1 = parse.select("div.pcProDetailIntro");
                if (0!=select1.size()){
                    select1.select("#container").remove();
                    productInfo.put("product_desc",select1.text());
                }

                productInfo.put("crawlerId", "17");
                mysqlUtil.insertProduct(productInfo);
                productInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                productInfo.put("@timestamp", timestamp2.format(new Date()));
                productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertProduct(productInfo);
//                if (esUtil.writeToES(productInfo, "crawler-product-", "doc", md5String)) {
//                    RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                }
                if (mysqlUtil.insertProduct(productInfo)){
                    RedisUtil.insertUrlToSet("catchedUrl-Product",url);
                }
            } else {
                LOGGER.info("页面为空！");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}