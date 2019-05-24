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

/**
 * <a>http://product.ebdoor.com/</a>
 * <a>Product：一比多</a>
 * @author:chenyan
 */
public class ebdoorProduct {
    private final static Logger LOGGER = LoggerFactory.getLogger(ebdoorProduct.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header = null;
    private static String baseUrl = "http://product.ebdoor.com/";

    //首页
    private void productPage(String url) {
        try {
            String html = httpGet(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("dd.LddList ul li");
            for (Element e : select){
                e.select("b").remove();
                String href = e.select("a").attr("href");
                String trade_category = e.text().trim();
                paging(href,trade_category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分页
    private void paging(String url,String trade_category) {

        try {
            String link = url.replace("1.aspx", "");
            String html = httpGet(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            String pagesNumber  = parse.select("#PageBreak_2").text();
            String Total = pagesNumber.replace("共", "").replace("页", "") + 1;
            int total = Integer.valueOf(Total).intValue();
            int number = 1;
            for (number = 1; number < total; number++) {
                String nextPage = link + number + ".aspx";
                System.out.println("nextPage:"+nextPage);
                productLink(nextPage,trade_category);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品列表
    private void productLink(String url, String trade_category) {
        try {
            String html = httpGet(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("li.Rk_Cont1 dl dd a");
            for (Element e : select){
                String href = e.attr("href");
                productInfo(href,trade_category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //产品信息
    private void productInfo(String url,String trade_category) {
        JSONObject productInfo = new JSONObject();
        try {
            String html = httpGet(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            productInfo.put("product_desc",parse.select("div#descDiv.BmConn2").text().trim());
            productInfo.put("company_name",parse.select("label#LinkmanInfo1_comp_Name a").text().trim());
            productInfo.put("company_id", MD5Util.getMD5String(parse.select("label#LinkmanInfo1_comp_Name a").text().trim()));
            productInfo.put("product_name",parse.select("h2.tipproname").text().trim());
            Elements select = parse.select("div.proInfoBox ul li");
            for (Element info : select){
                switch (info.text().split("：")[0]){
                    case "当前售价:":
                        productInfo.put("prices", info.text().split("：",2)[1]);
                        break;
                    case "供应数量:":
                        productInfo.put("total_supply", info.text().split("：",2)[1]);
                        break;
//                    case "商品产地:":
//                        productInfo.put("production_place", info.text().split("：",2)[1]);
//                        break;
                    default:
                }
            }
            Elements contact = parse.select("div.ws");
            for (Element brand : contact) {
                switch (brand.text().trim()){
                    case "品牌：":
                        productInfo.put("product_brand",brand.text().trim().split("：",2)[1]);
                        break;
                    case "产品型号：":
                        productInfo.put("product_specifications",brand.text().trim().split("：",2)[1]);
                        break;
                    default:
                }
            }
            Elements contactinfo = parse.select("div#cardcontent.contentdetail table tbody tr");
            for (Element brand : contactinfo) {
                switch (brand.text().trim()) {
                    case "联 系 人：":
                        productInfo.put("contacts", brand.text().trim().split("：",2)[1]);
                        break;
                    case "手    机：":
                        productInfo.put("contactInformation", brand.text().trim().split("：",2)[1]);
                        break;
                    default:
                }
            }
            productInfo.put("detailUrl",url);
            productInfo.put("crawlerId","46");
            productInfo.put("trade_category",trade_category);
            insert(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ebdoorProduct ebdoorProduct = new ebdoorProduct();
        ebdoorProduct.productPage(baseUrl);
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
