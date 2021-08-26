package spider.product;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.MD5Util;

import java.net.URL;

/**
 * <a>http://www.chemm.cn</a>
 * <p>product：中国化工机械网</p>
 * @author chenyan
 */
public class chemmProduct {
    private final static Logger LOGGER = LoggerFactory.getLogger(chemmProduct.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header = null;
    private static String baseUrl = "http://www.chemm.cn";

    //首页
    private void productPage(String url) {
        try {
            String html = httpGet(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements select = parse.select("a.dLinkFont");
            for (Element e : select){
                String href = baseUrl + e.attr("href");
                String trade_category = e.text().trim();
                productList(href,trade_category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品列表及分页
    private void productList(String url,String trade_category) {
        try {
            String html = httpGet(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements select = parse.select("li.ProListMainTitle span a.bBoldLinkFont");
            for (Element e : select){
                String href = baseUrl + e.attr("href");
                productInfo(href,trade_category);
            }
            Elements nextPage = parse.select(" a.pagelink");
            for (Element element : nextPage) {
                if (element.text().contains("下一页")){
                    String href = baseUrl + element.attr("href");
                    System.out.println("下一页："+href);
                    productList(href,trade_category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品信息
    private void productInfo(String url,String trade_category) {
        JSONObject productInfo = new JSONObject();
        try {
            String html = httpGet(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            productInfo.put("product_desc",parse.select("#MaininfoContent").text().trim());
            Elements select = parse.select("#ProductInfoList ul li");
            for (Element info : select){
                switch (info.text().split("：")[0]){
                    case "产品名称":
                        productInfo.put("product_name", info.text().split("：",2)[1]);
                        break;
                    case "产品编号":
                        productInfo.put("product_number", info.text().split("：",2)[1]);
                        break;
                    case "产品商标":
                        productInfo.put("product_brand", info.text().split("：",2)[1]);
                        break;
                    case "产品规格":
                        productInfo.put("product_specifications", info.text().split("：",2)[1]);
                        break;
                    case "参考价格":
                        productInfo.put("prices", info.text().split("：",2)[1]);
                        break;
                    default:
                }
            }
            Elements contact = parse.select("#contactusRight ul li");
            if (contact.size()!=0){
                for (Element info : contact) {
                    switch (info.select(".contactusRightTitle").text().trim()){
                        case "公司名称：":
                            productInfo.put("company_name",info.select("span.contactusRightInfo a.bLinkFont").text().trim());
                            productInfo.put("company_id", MD5Util.getMD5String(info.select("span.contactusRightInfo a.bLinkFont").text().trim()));
                            break;
                        case "联 系 人：":
                            productInfo.put("contacts",info.select("span.contactusRightInfo").text().trim());
                            break;
                        case "手　　机：":
                            productInfo.put("contactInformation",info.select("span.contactusRightInfo").text().trim());
                            break;
                        case "主　　页：":
                            productInfo.put("detailUrl",info.select("span.contactusRightInfo a").attr("href"));
                            break;
                        default:
                    }
                }
            }else {
                Elements contact1 = parse.select("#contactus2Right ul li");
                for (Element info1 : contact1) {
                    switch (info1.select(".contactusRightTitle").text().trim()) {
                        case "公司名称：":
                            productInfo.put("company_name", info1.select("span.contactusRightInfo a.bLinkFont").text().trim());
                            productInfo.put("company_id", MD5Util.getMD5String(info1.select("span.contactusRightInfo a.bLinkFont").text().trim()));
                            break;
                        case "联 系 人：":
                            productInfo.put("contacts", info1.select("span.contactusRightInfo").text().trim());
                            break;
                        case "手　　机：":
                            productInfo.put("contactInformation", info1.select("span.contactusRightInfo").text().trim());
                            break;
                        case "主　　页：":
                            productInfo.put("detailUrl", info1.select("span.contactusRightInfo a").attr("href"));
                            break;
                        default:
                    }
                }
            }
            productInfo.put("crawlerId","47");
            productInfo.put("trade_category",trade_category);
            insert(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        chemmProduct chemmProduct = new chemmProduct();
        chemmProduct.productPage("http://www.chemm.cn/Sample/");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject productInfo) {
        Map = (java.util.Map) productInfo;
        if (updateToMySQL.productUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    private String httpGet(String url, String judgeWord) {
        try {
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (null != url) {
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null && html.contains(judgeWord)) {
                    return html;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
