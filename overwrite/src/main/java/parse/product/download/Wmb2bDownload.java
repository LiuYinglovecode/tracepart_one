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

public class Wmb2bDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Wmb2bDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);


    //产品信息
    public void productInfo(String url) {
        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "wmb2b");
            if (null != html) {
                Document parse = Jsoup.parse(html);
                String title = parse.select("h1.title").text().trim();
                if (null != title) {
                    productInfo.put("product_name", title);//标题
                    Elements elements = parse.select("li.px15");
                    if (elements.size() != 0) {
                        for (Element element : elements) {
                            if (element.text().contains("参考价格：")) {
                                productInfo.put("prices", element.text().replace("参考价格：", "").replace("¥", ""));//价格
                            } else if (element.text().contains("产品品牌：")) {
                                productInfo.put("product_brand", element.text().replace("产品品牌：", ""));//品牌
                            } else if (element.text().contains("所在地区：")) {
                                productInfo.put("delivery_place", element.text().replace("所在地区：", ""));//发货地区
                            } else if (element.text().contains("更新时间：")) {
                                productInfo.put("release_time", element.text().replace("更新时间：", ""));//发布时间
                            } else if (element.text().contains("联系人：")) {
                                productInfo.put("contacts", element.text().replace("联系人：", ""));//联系人
                            } else if (element.text().contains("手机：")) {
                                productInfo.put("contactInformation", element.text().replace("手机：", ""));//联系方式
                            }
                        }
                    }

                    /**
                     * 产品图片
                     */
                    Elements img = parse.select("#t_0,#t_1,#t_2");
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
                    Elements productDesc = parse.select("#content");
                    if (0 != productDesc.size()) {
                        productInfo.put("product_desc", productDesc.text().trim());
                    }

                    /**
                     * 公司名
                     */
                    String nameId = null;
                    Elements name = parse.select("span.si-name,div.head > div > strong");
                    if (0 != name.size()) {
                        String trim = name.text().trim();
                        productInfo.put("company_name", trim);
                        nameId = MD5Util.getMD5String(trim);
                    } else {
                        String s = "企业未知";
                        productInfo.put("company_name", s);
                        nameId = MD5Util.getMD5String(s);
                    }
                    productInfo.put("company_id", nameId);

                    productInfo.put("crawlerId", "113");
                    mysqlUtil.insertProduct(productInfo);
                    productInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    productInfo.put("@timestamp", timestamp2.format(new Date()));
                    productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertProduct(productInfo);
                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", nameId)) {
                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
