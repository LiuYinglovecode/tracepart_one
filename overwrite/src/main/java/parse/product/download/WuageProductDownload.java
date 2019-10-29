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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class WuageProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(WuageProductDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    /**
     * 产品信息
     */
    public void productInfo(String url) throws InterruptedException {

        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            ArrayList<String> skuList = new ArrayList<>();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "钢材市场");
            Thread.sleep(30000);
            if (null != html) {
                Document parse = Jsoup.parse(html);
                productInfo.put("product_name", parse.select("h1.info-title").text().trim());//标题
                /***********************************************************************************************
                 * 价格：prices
                 */
                Elements prices = parse.select("div.item-content strong.price");
                if (prices.size() != 0) {
                    productInfo.put("prices", prices.text());//价格
                }
                /***********************************************************************************************
                 * 规格：product_specifications
                 */
                Elements sku = parse.select("div.sku-item > div > ul > li > div.sku");
                if (0 != sku.size()) {
                    for (Element element : sku) {
                        skuList.add(element.text());
                    }
                    productInfo.put("product_specifications", skuList.toString());
                }
                /***********************************************************************************************
                 * 最小起订：miniOrder
                 */
                Element miniOrder = parse.select("div.moq em").first();
                if (null != miniOrder) {
                    productInfo.put("mini_order", miniOrder.text());
                }
                /***********************************************************************************************
                 * 可售数量：totalSupply
                 */
                Element totalSupply = parse.select("div.total em").first();
                if (null != totalSupply) {
                    productInfo.put("total_supply", totalSupply.text());
                }
                /***********************************************************************************************
                 * 产品信息：product_desc
                 * 图片：product_images
                 */
                ArrayList<String> info = new ArrayList<>();
                Elements productDesc = parse.select("div.details-body ul li");
                if (null != productDesc) {
                    for (Element element : productDesc) {
                        info.add(element.text().replace(" ","："));
                    }
                    productInfo.put("product_desc", info.toString());
                }
                Elements productImages = productDesc.select("div.spec-items ul li img");
                if (0 != productImages.size()) {
                    for (Element imgs : productImages) {
                        if (imgs.attr("src").contains("https")) {
                            imgsList.add(imgs.attr("src"));
                        } else {
                            imgsList.add("https" + imgs.attr("src"));
                        }
                    }
                    productInfo.put("product_images", imgsList.toString());
                }
                /***********************************************************************************************
                 * 公司信息：companyInformation
                 * 公司名：company_name
                 * 公司名MD5加密：company_id
                 */
/*                Elements companyInformation = parse.select("ul.company-info-list li");
                if (0 != companyInformation.size()) {
                    for (Element element : companyInformation) {
                        if (element.text().contains("经营地址")) {
                            productInfo.put("production_place", element.text().replace("经营地址", ""));
                        }
                    }

                    }*/
                    Elements companyName = parse.select("div.header span.text");
                    productInfo.put("company_name", companyName.text().replace("...",""));
                    String productMd5Id = MD5Util.getMD5String(companyName.text().replace("...",""));
                    productInfo.put("company_id", productMd5Id);

                    productInfo.put("crawlerId", "99");
                    productInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    productInfo.put("@timestamp", timestamp2.format(new Date()));
                    productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertProduct(productInfo);
//                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", productMd5Id)) {
//                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                    }
                if (mysqlUtil.insertProduct(productInfo)){
                    RedisUtil.insertUrlToSet("catchedUrl-Product",url);
                }
                } else {
                    LOGGER.info("页面为空！");
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}